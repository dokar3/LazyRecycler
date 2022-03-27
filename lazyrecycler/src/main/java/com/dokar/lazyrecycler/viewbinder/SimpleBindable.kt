package com.dokar.lazyrecycler.viewbinder

import androidx.recyclerview.widget.RecyclerView

internal class SimpleBindable<I>(
    private val itemProvider: ItemProvider
) : BindViewScope<I> {
    var bind: ((item: I) -> Unit)? = null
    var indexedBind: ((index: Int, item: I) -> Unit)? = null

    var viewHolder: RecyclerView.ViewHolder? = null

    override fun getPosition(): Int {
        val vh = viewHolder ?: return RecyclerView.NO_POSITION
        return itemProvider.getPositionInSection(vh.bindingAdapterPosition)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getItem(): I? {
        val vh = viewHolder ?: return null
        return itemProvider.getItem(vh.bindingAdapterPosition) as I?
    }

    override fun bind(bind: (item: I) -> Unit) {
        this.bind = bind
    }

    override fun bindIndexed(bind: (index: Int, item: I) -> Unit) {
        this.indexedBind = bind
    }
}
