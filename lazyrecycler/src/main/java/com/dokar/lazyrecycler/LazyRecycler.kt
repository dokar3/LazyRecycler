package com.dokar.lazyrecycler

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.PropertyNames
import com.dokar.lazyrecycler.data.ValueObserver
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Create a LazyRecycler
 *
 * ```
 * Sample:
 *  LazyRecycler(recyclerView) {
 *      item(R.layout.item_header, user) {
 *          bind { user ->
 *              // bindings
 *          }
 *      }
 *
 *      items(photos) { binding: ItemPhotoBinding, photo ->
 *          // bindings
 *      }
 *  }
 * ```
 * */
fun LazyRecycler(
    recyclerView: RecyclerView? = null,
    setupLayoutManager: Boolean = true,
    isHorizontal: Boolean = false,
    reverseLayout: Boolean = false,
    stackFromEnd: Boolean = false,
    spanCount: Int = 1,
    body: RecyclerBuilder.() -> Unit
): LazyRecycler {
    val builder = RecyclerBuilder().also(body)
    return LazyRecycler(
        builder.sections().toMutableList(),
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
    private val sections: MutableList<Section<Any, Any>>,
    private val setupLayoutManager: Boolean,
    private val isHorizontal: Boolean,
    private val reverseLayout: Boolean,
    private val stackFromEnd: Boolean,
    private val spanCount: Int,
) {

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val adapter: LazyAdapter = LazyAdapter(sections)

    private val differs: MutableMap<Section<Any, Any>, AsyncListDiffer<Any>> = mutableMapOf()

    private val dataSourceObserver: ValueObserver<Any> = ValueObserver {
        onDataSourceChanged(it)
    }

    private val propertiesObserver: ValueObserver<Any> = ValueObserver {
        onPropertyChanged(it)
    }

    init {
        setupDiffers(sections)
    }

    fun attachTo(rv: RecyclerView) {
        observeChanges()

        if (setupLayoutManager) {
            setupLayoutManager(rv)
            rv.layoutManager = layoutManager
        }
        rv.adapter = adapter
    }

    /**
     * Insert new sections at end
     *
     * @param body DSL body
     * */
    fun newSections(body: RecyclerBuilder.() -> Unit) {
        newSections(sections.size, body)
    }

    /**
     * Insert new sections at specific position
     * @param index Insert position
     * @param body DSL body
     * */
    fun newSections(index: Int, body: RecyclerBuilder.() -> Unit) {
        val builder = RecyclerBuilder().also(body)
        val newSections = builder.sections()
        setupDiffers(newSections)
        adapter.addSections(index, newSections)
    }

    /**
     * Remove section
     * @param id Section id
     * @return Returns true if successfully removed, false if target
     * section does not existing or failed to remove
     * */
    fun removeSection(id: Int): Boolean {
        val section = sections.find { it.id == id } ?: return false
        return removeSection(section)
    }

    private fun removeSection(section: Section<Any, Any>): Boolean {
        return adapter.removeSection(section)
    }

    /**
     * Check if target section is contained in sections
     * */
    fun containsSection(id: Int): Boolean {
        return sections.find { it.id == id } != null
    }

    /**
     * Hide or show a section
     * */
    fun setSectionVisible(id: Int, visible: Boolean) {
        sections.find { it.id == id }?.let {
            setSectionVisible(it, visible)
        }
    }

    fun setSectionVisible(section: Section<Any, Any>, visible: Boolean) {
        if (section.visible == visible) {
            return
        }
        adapter.setSectionVisible(section, visible)
    }

    /**
     * Check if target section is visible
     * */
    fun isSectionVisible(id: Int): Boolean {
        return sections.find { it.id == id }?.visible ?: false
    }

    /**
     * Update items for target section
     * */
    fun updateSection(id: Int, items: List<Any>) {
        sections.find { it.id == id }?.let {
            updateSection(it, items)
        }
    }

    fun updateSection(section: Section<Any, Any>, items: List<Any>) {
        val differ = differs[section]
        if (differ != null) {
            differ.submitList(items)
        } else {
            adapter.updateSectionItems(section, items)
        }
    }

    /**
     * Get all section items
     * */
    fun getSectionItems(id: Int): List<Any>? {
        return sections.find { it.id == id }?.items
    }

    /**
     * Get size of sections
     * */
    fun getSectionCount(): Int {
        return sections.size
    }

    /**
     * Clear all sections
     * */
    fun clearSections() {
        sections.clear()
        adapter.notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    private fun onDataSourceChanged(data: MutableValue<*>) {
        // Mutable data source changed
        val section = sections.find {
            it.findExtras(MutableValue::class.java)?.contains(data) == true
        } ?: return

        val current = data.current
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

    private fun onPropertyChanged(property: MutableValue<Any>) {
        // Mutable property changed
        val section = sections.find {
            it.findExtras(MutableValue::class.java)?.contains(property) == true
        } ?: return

        val name = property.name ?: return
        val value = property.current ?: return

        when (name) {
            PropertyNames.SHOW_WHILE -> {
                val visible = value as? Boolean ?: return
                setSectionVisible(section, visible)
            }
        }
    }

    /**
     * Observe the mutable data sources, will be called when attach to [RecyclerView]
     * */
    @Suppress("UNCHECKED_CAST")
    fun observeChanges() {
        sections.forEach {
            it.findExtras(MutableValue::class.java)?.let { values ->
                values.forEach { value ->
                    if (value.type == MutableValue.DATA_SOURCE) {
                        (value as MutableValue<Any>).observe(dataSourceObserver)
                    } else if (value.type == MutableValue.PROPERTY) {
                        (value as MutableValue<Any>).observe(propertiesObserver)
                    }
                }
            }
        }
    }

    /**
     * Stop observing the mutable data sources
     * */
    @Suppress("UNCHECKED_CAST")
    fun stopObserving() {
        sections.forEach {
            it.findExtras(MutableValue::class.java)?.let { values ->
                values.forEach { value ->
                    value.unobserve()
                }
            }
        }
    }

    private fun setupLayoutManager(rv: RecyclerView) {
        val orientation = if (isHorizontal) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
        layoutManager = if (spanCount > 1) {
            val glm = GridLayoutManager(rv.context, spanCount, orientation, reverseLayout)
            glm.stackFromEnd = stackFromEnd
            glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val sectionIdx = adapter.getSectionIndex(position)
                    val section = sections[sectionIdx]
                    val spanSizeLookup = section.spanCountLookup ?: return 1
                    var offset = 0
                    for (i in 0 until sectionIdx) {
                        val sect = sections[i]
                        offset += if (sect.visible) sect.items.size else 0
                    }
                    return spanSizeLookup(position - offset)
                }
            }
            glm
        } else {
            LinearLayoutManager(rv.context, orientation, reverseLayout).also {
                it.stackFromEnd = stackFromEnd
            }
        }
    }

    private fun setupDiffers(sections: List<Section<Any, Any>>) {
        sections.forEach { section ->
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

        private val sExecutor: ExecutorService = Executors.newFixedThreadPool(2)
    }
}
