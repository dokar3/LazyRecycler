package com.dokar.lazyrecycler

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.ValueObserver
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Create a LazyRecycler
 *
 * ### Sample:
 * ```kotlin
 * lazyRecycler(recyclerView) {
 *     item(
 *         data = info,
 *         layout = R.layout.item_header,
 *     ) { root ->
 *         val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *         bind { info ->
 *             // bind item
 *         }
 *     }
 *
 *     items(
 *      data = photos,
 *      layout = ItemPhotoBinding::inflate,
 *     ) { binding, photo ->
 *         // bind item
 *     }
 * }
 * ```
 */
fun lazyRecycler(
    recyclerView: RecyclerView? = null,
    setupLayoutManager: Boolean = true,
    isHorizontal: Boolean = false,
    reverseLayout: Boolean = false,
    stackFromEnd: Boolean = false,
    spanCount: Int = 1,
    adapterCreator: AdapterCreator = { LazyAdapter(it) },
    body: RecyclerBuilder.() -> Unit
): LazyRecycler {
    val builder = RecyclerBuilder().also(body)
    val sections = builder.sections()
    return LazyRecycler(
        adapterCreator(sections),
        setupLayoutManager,
        isHorizontal,
        reverseLayout,
        stackFromEnd,
        spanCount
    ).also {
        if (recyclerView != null) {
            it.attachTo(recyclerView)
        }
    }
}

class LazyRecycler(
    private val adapter: LazyAdapter,
    private val setupLayoutManager: Boolean,
    private val isHorizontal: Boolean,
    private val reverseLayout: Boolean,
    private val stackFromEnd: Boolean,
    private val spanCount: Int,
) {
    private val differs = mutableMapOf<Section<Any, Any>, AsyncListDiffer<Any>>()

    private val valueObserver = ValueObserver<Any> { onMutableValueChanged(it) }

    init {
        setupDiffers(adapter.sections())
    }

    /**
     * Setup with RecyclerView.
     *
     * @param rv Target RecyclerView.
     * @param observeChanges Observe section changes, set to false if LazyRecycler does not
     * contain any [MutableValue] to prevent additional check.
     */
    fun attachTo(rv: RecyclerView, observeChanges: Boolean = true) {
        if (observeChanges) {
            observeChanges()
        }
        if (setupLayoutManager) {
            setupLayoutManager(rv)
        }
        rv.adapter = adapter
    }

    /**
     * Insert new sections.
     *
     * @param body Builder body.
     */
    fun newSections(body: RecyclerBuilder.() -> Unit) {
        newSections(adapter.sections().size, true, body)
    }

    /**
     * Insert new sections at specific position.
     *
     * @param index Insert position
     * @param observeChanges Observe section changes, set to false if new sections do not
     * contain any [MutableValue] to prevent additional check.
     * @param body Builder body.
     */
    fun newSections(
        index: Int,
        observeChanges: Boolean = true,
        body: RecyclerBuilder.() -> Unit
    ) {
        val builder = RecyclerBuilder().also(body)
        val newSections = builder.sections()
        setupDiffers(newSections)
        if (observeChanges) {
            observeChanges(newSections)
        }
        adapter.addSections(index, newSections)
    }

    /**
     * Remove section.
     *
     * @param id Section id.
     * @return Returns true if successfully removed, false if target
     * section does not exist or failed to remove.
     */
    fun removeSection(id: Int): Boolean {
        val section = adapter.sections().find { it.id == id } ?: return false
        return removeSection(section)
    }

    private fun removeSection(section: Section<Any, Any>): Boolean {
        return adapter.removeSection(section)
    }

    /**
     * Check if target section is contained in sections.
     * */
    fun containsSection(id: Int): Boolean {
        return adapter.sections().find { it.id == id } != null
    }

    /**
     * Update items for target section.
     */
    fun updateSection(id: Int, items: List<Any>) {
        adapter.sections().find { it.id == id }?.let {
            updateSection(it, items)
        }
    }

    private fun updateSection(section: Section<Any, Any>, items: List<Any>) {
        val differ = differs[section]
        if (differ != null) {
            differ.submitList(items)
        } else {
            adapter.updateSectionItems(section, items)
        }
    }

    /**
     * Get all section items.
     */
    fun getSectionItems(id: Int): List<Any>? {
        return adapter.sections().find { it.id == id }?.items
    }

    /**
     * Get size of sections.
     */
    fun getSectionCount(): Int {
        return adapter.sections().size
    }

    /**
     * Clear all sections.
     */
    fun clearSections() {
        adapter.clearSections()
    }

    /**
     * Observe the mutable data sources, will be called when attach to [RecyclerView].
     */
    fun observeChanges() {
        observeChanges(adapter.sections())
    }

    @Suppress("UNCHECKED_CAST")
    private fun observeChanges(sectionList: List<Section<Any, Any>>) {
        sectionList.forEachMutableValues { mutVal ->
            if (mutVal.valueObserver != valueObserver) {
                (mutVal as MutableValue<Any>).observe(valueObserver)
            }
        }
    }

    /**
     * Stop observing the mutable data sources.
     */
    @Suppress("UNCHECKED_CAST")
    fun stopObserving() {
        adapter.sections().forEachMutableValues { mutVal ->
            mutVal.unobserve()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun onMutableValueChanged(value: MutableValue<*>) {
        // Mutable data source changed
        val section = adapter.sections().find { it.data == value } ?: return

        val current = value.current
        when {
            current is List<*> -> {
                updateSection(section, current as List<Any>)
            }

            current != null -> {
                updateSection(section, listOf(current))
            }

            else -> {
                updateSection(section, emptyList())
            }
        }
    }

    private inline fun List<Section<Any, Any>>.forEachMutableValues(
        block: (mutVal: MutableValue<*>) -> Unit
    ) {
        for (section in this) {
            if (section.data != null) {
                block(section.data)
            }
        }
    }

    private fun setupLayoutManager(rv: RecyclerView) {
        val orientation = if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        if (spanCount > 1) {
            val glm = GridLayoutManager(rv.context, spanCount, orientation, reverseLayout)
            glm.stackFromEnd = stackFromEnd
            glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val sectionIdx = adapter.getSectionIndex(position)
                    val section = adapter.sections()[sectionIdx]
                    val spanSizeLookup = section.span ?: return 1
                    val offset = adapter.getSectionPositionOffset(section)
                    return spanSizeLookup(position - offset)
                }
            }
            rv.layoutManager = glm
        } else {
            val llm = LinearLayoutManager(rv.context, orientation, reverseLayout)
            llm.stackFromEnd = stackFromEnd
            rv.layoutManager = llm
        }
    }

    private fun setupDiffers(sections: List<Section<Any, Any>>) {
        for (section in sections) {
            val differ = section.differ
            if (differ != null) {
                setupDiffer(adapter, section, differ)
            }
        }
    }

    private fun setupDiffer(
        adapter: LazyAdapter,
        section: Section<Any, Any>,
        diffCallback: DiffUtil.ItemCallback<Any>
    ) {
        val updateCallback = DiffUpdateCallback(adapter, section)
        val diffConfig = AsyncDifferConfig.Builder(diffCallback)
            .setBackgroundThreadExecutor(sExecutor)
            .build()
        differs[section] = AsyncListDiffer(updateCallback, diffConfig).also {
            it.submitList(section.items)
            it.addListListener { _, currentList ->
                section.items = currentList
            }
        }
    }

    companion object {
        // DiffUtil executor
        private val sExecutor: ExecutorService = Executors.newFixedThreadPool(2)
    }
}
