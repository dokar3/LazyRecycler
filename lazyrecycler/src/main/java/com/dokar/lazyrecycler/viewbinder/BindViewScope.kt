package com.dokar.lazyrecycler.viewbinder

import androidx.recyclerview.widget.RecyclerView

typealias Bind<I> = (item: I) -> Unit

typealias IndexedBind<I> = (index: Int, item: I) -> Unit

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
    fun bind(bind: Bind<I>)

    /**
     * Bind item
     */
    fun bindIndexed(bind: IndexedBind<I>)
}

internal class BindViewScopeImpl<I> : BindViewScope<I> {
    var bind: Bind<I>? = null
    var indexedBind: IndexedBind<I>? = null

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

    override fun bind(bind: Bind<I>) {
        this.bind = bind
    }

    override fun bindIndexed(bind: IndexedBind<I>) {
        this.indexedBind = bind
    }
}
