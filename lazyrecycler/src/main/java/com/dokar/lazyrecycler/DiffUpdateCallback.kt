package com.dokar.lazyrecycler

import androidx.recyclerview.widget.ListUpdateCallback

internal class DiffUpdateCallback(
    private val adapter: LazyAdapter,
    private val section: Section<Any, Any>,
) : ListUpdateCallback {

    override fun onInserted(position: Int, count: Int) {
        val offset = adapter.getSectionPositionOffset(section)
        adapter.notifyItemRangeInserted(offset + position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        val offset = adapter.getSectionPositionOffset(section)
        adapter.notifyItemRangeRemoved(offset + position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        val offset = adapter.getSectionPositionOffset(section)
        adapter.notifyItemMoved(offset + fromPosition, offset + toPosition)
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        val offset = adapter.getSectionPositionOffset(section)
        adapter.notifyItemRangeChanged(offset + position, count, payload)
    }
}
