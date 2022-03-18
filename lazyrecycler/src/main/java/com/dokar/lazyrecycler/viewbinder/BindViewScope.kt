package com.dokar.lazyrecycler.viewbinder

import androidx.recyclerview.widget.RecyclerView

interface BindViewScope<I> {
    /**
     * Get position. If If this function is called before ViewHolder is attached
     * to RecyclerView, [RecyclerView.NO_POSITION] will be returned.
     */
    fun getPosition(): Int

    /**
     * Get item. If this function is called before ViewHolder is attached
     * to RecyclerView, null will be returned.
     */
    fun getItem(): I?

    /**
     * Bind item
     */
    fun bind(bind: (item: I) -> Unit)

    /**
     * Bind item
     */
    fun bindIndexed(bind: (index: Int, item: I) -> Unit)
}

internal class BindViewScopeImpl<I> : BindViewScope<I> {
    var bind: ((item: I) -> Unit)? = null
    var indexedBind: ((index: Int, item: I) -> Unit)? = null

    var itemProvider: ItemProvider? = null
    var viewHolder: RecyclerView.ViewHolder? = null

    override fun getPosition(): Int {
        val provider = itemProvider ?: return RecyclerView.NO_POSITION
        val vh = viewHolder ?: return RecyclerView.NO_POSITION
        return provider.getPositionInSection(vh.bindingAdapterPosition)
    }

    @Suppress("UNCHECKED_CAST")
    override fun getItem(): I? {
        val provider = itemProvider ?: return null
        val vh = viewHolder ?: return null
        return provider.getItem(vh.bindingAdapterPosition) as I?
    }

    override fun bind(bind: (item: I) -> Unit) {
        this.bind = bind
    }

    override fun bindIndexed(bind: (index: Int, item: I) -> Unit) {
        this.indexedBind = bind
    }
}
