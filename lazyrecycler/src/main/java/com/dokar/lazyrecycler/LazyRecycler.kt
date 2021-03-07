package com.dokar.lazyrecycler

import androidx.recyclerview.widget.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@DslMarker
annotation class LazyRecyclerMarker

@DslMarker
annotation class TemplateMarker

@LazyRecyclerMarker
fun LazyRecycler(
    rv: RecyclerView? = null,
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
        if (rv != null) {
            it.attachTo(rv)
        }
    }
}

@Suppress("DEPRECATION")
class LazyRecycler(
    @Deprecated("Should not access this field directly")
    val sections: MutableList<Section<Any, Any>>,
    private val setupLayoutManager: Boolean,
    private val isHorizontal: Boolean,
    private val reverseLayout: Boolean,
    private val stackFromEnd: Boolean,
    private val spanCount: Int,
) {

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private val adapter: LazyAdapter = LazyAdapter(sections)

    private val differs: MutableMap<Section<Any, Any>, AsyncListDiffer<Any>> = mutableMapOf()

    init {
        setupDiffers(sections)
    }

    fun attachTo(rv: RecyclerView) {
        if (setupLayoutManager) {
            setupLayoutManager(rv)
            rv.layoutManager = layoutManager
        }
        rv.adapter = adapter
    }

    fun newSections(body: RecyclerBuilder.() -> Unit) {
        newSections(sections.size, body)
    }

    fun newSections(index: Int, body: RecyclerBuilder.() -> Unit) {
        val builder = RecyclerBuilder().also(body)
        val newSections = builder.sections()
        setupDiffers(newSections)
        adapter.addSections(index, newSections)
    }

    fun removeSection(id: Int): Boolean {
        val section = sections.find { it.id == id } ?: return false
        return removeSection(section)
    }

    private fun removeSection(section: Section<Any, Any>): Boolean {
        return adapter.removeSection(section)
    }

    fun setSectionVisible(id: Int, visible: Boolean) {
        sections.find { it.id == id }?.let {
            setSectionVisible(it, visible)
        }
    }

    fun setSectionVisible(section: Section<Any, Any>, visible: Boolean) {
        section.visible = visible
        adapter.notifyDataSetChanged()
    }

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

    fun getSectionItems(id: Int): List<Any>? {
        return sections.find { it.id == id }?.items
    }

    fun getSectionCount(): Int {
        return sections.size
    }

    fun clearSections() {
        sections.clear()
        adapter.notifyDataSetChanged()
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
        differ: Differ<Any>
    ) {
        val diffCallback = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return differ.areItemsTheSame?.invoke(oldItem, newItem) ?: false
            }

            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
                return differ.areContentsTheSame?.invoke(oldItem, newItem) ?: false
            }

        }
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