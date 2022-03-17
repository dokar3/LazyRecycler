package com.dokar.lazyrecycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.viewbinder.ItemProvider

/**
 * The adapter to display [Section]s, can be extended for advanced using.
 */
open class LazyAdapter(
    private val sections: MutableList<Section<Any, Any>>
) : RecyclerView.Adapter<LazyViewHolder<Any>>(), ItemProvider {
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
        val itemViewCreator = section.viewHolderCreator
        // create ViewHolder
        val holder = itemViewCreator.create(parent, section.itemBinder, this)
        // clicks
        val onItemClick = section.onItemClick
        if (onItemClick != null) {
            holder.itemView.setOnClickListener click@{
                val pos = holder.bindingAdapterPosition
                val item = getItem(pos) ?: return@click
                onItemClick(it, item)
            }
        }
        // long clicks
        val onItemLongClick = section.onItemLongClick
        if (onItemLongClick != null) {
            holder.itemView.setOnLongClickListener longClick@{
                val pos = holder.bindingAdapterPosition
                val item = getItem(pos) ?: return@longClick false
                return@longClick onItemLongClick(it, item)
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: LazyViewHolder<Any>, position: Int) {
        val itemAndOffset = getItemAndOffset(position)
        holder.bind(itemAndOffset.first, position - itemAndOffset.second)
    }

    override fun getItemCount(): Int {
        return sections.fold(0) { acc, s ->
            acc + s.items.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        var offset = 0
        var sectionIdx = 0
        while (sectionIdx < sections.size) {
            val s = sections[sectionIdx]
            val size = s.items.size
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

    override fun getItem(adapterPosition: Int): Any? {
        if (adapterPosition < 0) return null
        return getItemAndOffset(adapterPosition).first
    }

    override fun getPositionInSection(adapterPosition: Int): Int {
        if (adapterPosition == -1) return adapterPosition
        return adapterPosition - getItemAndOffset(adapterPosition).second
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
            acc + s.items.size
        }

        this.sections.addAll(index, newSections)

        notifyItemRangeInserted(posOffset, newSize)
    }

    fun removeSection(section: Section<Any, Any>): Boolean {
        val posOffset = getSectionPositionOffset(section)
        val size = section.items.size
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
            size += it.items.size
            position < size
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
            offset += s.items.size
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

    private fun getItemAndOffset(position: Int): Pair<Any?, Int> {
        var offset = 0
        var index = 0
        while (index < sections.size) {
            val section = sections[index]
            val size = section.items.size
            if (position < offset + size) {
                break
            }
            offset += size
            index++
        }
        return sections[index].items[position - offset] to offset
    }
}
