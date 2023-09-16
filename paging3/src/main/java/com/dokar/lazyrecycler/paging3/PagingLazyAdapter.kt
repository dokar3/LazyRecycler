package com.dokar.lazyrecycler.paging3

import com.dokar.lazyrecycler.LazyAdapter
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.Section

/**
 * The adapter supports paging.
 */
open class PagingLazyAdapter(
    sections: List<Section<Any, Any>>,
) : LazyAdapter(sections = sections) {
    override fun onBindViewHolder(holder: LazyViewHolder<Any>, position: Int) {
        val itemData = getItemData(position)
        val section = itemData.section
        val sectionData = section.data

        if (sectionData is PagingValue<*>) {
            val positionInSection = position - itemData.adapterPositionOffset
            // Access item to trigger loads if needed
            sectionData.differ.getItem(positionInSection)
        }

        holder.bind(itemData.item, position - itemData.adapterPositionOffset)
    }
}