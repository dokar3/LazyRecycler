package com.dokar.lazyrecycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LazyAdapter(
    private val sections: MutableList<Section<Any, Any>>
) : RecyclerView.Adapter<LazyViewHolder<Any>>() {

    private val viewTypes: MutableMap<Section<Any, Any>, Int> = hashMapOf()

    init {
        sections.forEach { section ->
            // add view types
            viewTypes[section] = section.viewType
            val subSections = section.subSections
            if (!subSections.isNullOrEmpty()) {
                subSections.forEach { (sub, _) ->
                    viewTypes[sub] = sub.viewType
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LazyViewHolder<Any> {
        // find target section
        val section = findSectionByViewType(viewType)
            ?: throw IllegalStateException("Cannot find section for viewType: $viewType")
        val itemViewCreator = section.viewCreator
        // create view
        val view = itemViewCreator.create(parent)
        // create ViewHolder
        val holder = LazyViewHolder(
            view.first,
            view.second,
            section.itemBinder
        )
        // clicks
        val onItemClick = section.onItemClick
        if (onItemClick != null) {
            holder.itemView.setOnClickListener {
                val pos = holder.adapterPosition
                val item = getItem(pos) ?: return@setOnClickListener
                onItemClick(it, item)
            }
        }
        // long clicks
        val onItemLongClick = section.onItemLongClick
        if (onItemLongClick != null) {
            holder.itemView.setOnLongClickListener {
                val pos = holder.adapterPosition
                val item = getItem(pos) ?: return@setOnLongClickListener false
                return@setOnLongClickListener onItemLongClick(it, item)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: LazyViewHolder<Any>, position: Int) {
        val itemWithOffset = getItemAndOffset(position)
        holder.bind(itemWithOffset.first, position - itemWithOffset.second)
    }

    override fun getItemCount(): Int {
        return sections.fold(0) { acc, s ->
            acc + if (s.visible) s.items.size else 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        var offset = 0
        var sectionIdx = 0
        while (sectionIdx < sections.size) {
            val s = sections[sectionIdx]
            val size = if (s.visible) s.items.size else 0
            if (position < offset + size) {
                break
            }
            offset += size
            sectionIdx++
        }

        val section = sections[sectionIdx]
        val subSections = section.subSections
        if (!subSections.isNullOrEmpty()) {
            val pos = position - offset
            val item = section.items[pos]
            for (sub in subSections) {
                val s = sub.first
                val where = sub.second
                if (where(item, pos)) {
                    return viewTypes[s]
                        ?: throw IllegalStateException("Cannot solve viewType for item: $position")
                }
            }
        }

        return viewTypes[section]
            ?: throw IllegalStateException("Cannot solve viewType for item: $position")
    }

    fun addSections(index: Int, newSections: List<Section<Any, Any>>) {
        val posOffset = when (index) {
            0 -> 0
            sections.size -> itemCount
            else -> getSectionPositionOffset(sections[index])
        }
        val newSize = newSections.fold(0) { acc, s ->
            // add view types
            viewTypes[s] = s.viewType
            val subSections = s.subSections
            if (!subSections.isNullOrEmpty()) {
                subSections.forEach { (sub, _) ->
                    viewTypes[sub] = sub.viewType
                }
            }
            acc + if (s.visible) s.items.size else 0
        }

        this.sections.addAll(index, newSections)

        notifyItemRangeInserted(posOffset, newSize)
    }

    fun removeSection(section: Section<Any, Any>): Boolean {
        val posOffset = getSectionPositionOffset(section)
        val size = if (section.visible) section.items.size else 0
        if (!sections.remove(section)) {
            return false
        }
        // remove view types
        val subSections = section.subSections
        if (!subSections.isNullOrEmpty()) {
            subSections.forEach { (sub, _) ->
                viewTypes.remove(sub)
            }
        }
        viewTypes.remove(section)

        notifyItemRangeRemoved(posOffset, size)

        return true
    }

    fun getSectionIndex(position: Int): Int {
        var size = 0
        return sections.indexOfFirst {
            size += if (it.visible) it.items.size else 0
            position < size
        }
    }

    fun setSectionVisible(section: Section<Any, Any>, visible: Boolean) {
        val visibleCurrently = section.visible
        if (!visibleCurrently && visible) {
            val offset = getSectionPositionOffset(section)
            section.visible = visible
            if (offset != -1) {
                notifyItemRangeInserted(offset, section.items.size)
            }
        } else if (visibleCurrently && !visible) {
            val offset = getSectionPositionOffset(section)
            section.visible = visible
            if (offset != -1) {
                notifyItemRangeRemoved(offset, section.items.size)
            }
        }
    }

    fun updateSectionItems(section: Section<Any, Any>, newItems: List<Any>) {
        val offset = getSectionPositionOffset(section)
        if (offset == -1) {
            return
        }
        val oldCount = section.items.size
        val newCount = newItems.size
        section.items = newItems

        when {
            oldCount == newCount -> {
                notifyItemRangeChanged(offset, oldCount)
            }
            oldCount == 0 -> {
                notifyItemRangeInserted(offset, newCount)
            }
            newCount == 0 -> {
                notifyItemRangeRemoved(offset, oldCount)
            }
            else -> {
                notifyItemRangeChanged(offset, newCount)
            }
        }
    }

    fun getSectionPositionOffset(section: Section<Any, Any>): Int {
        var offset = 0
        var index = 0
        var found = false
        while (index < sections.size) {
            val s = sections[index]
            if (s == section) {
                found = true
                break
            }
            val size = if (s.visible) s.items.size else 0
            offset += size
            index++
        }
        return if (found) offset else -1
    }

    private fun findSectionByViewType(viewType: Int): Section<Any, Any>? {
        for ((section, vt) in viewTypes) {
            if (vt == viewType) {
                return section
            }
        }
        return null
    }

    private fun getItem(position: Int): Any? {
        return getItemAndOffset(position).first
    }

    private fun getItemAndOffset(position: Int): Pair<Any?, Int> {
        var offset = 0
        var index = 0
        while (index < sections.size) {
            val section = sections[index]
            val size = if (section.visible) section.items.size else 0
            if (position < offset + size) {
                break
            }
            offset += size
            index++
        }
        return sections[index].items[position - offset] to offset
    }
}
