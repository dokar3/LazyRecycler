package com.dokar.lazyrecycler.viewbinder

import androidx.recyclerview.widget.RecyclerView

typealias Bind<I> = (item: I) -> Unit

typealias IndexedBind<I> = (index: Int, item: I) -> Unit

class BindHolder<I> {

    internal var bind: Bind<I>? = null
    internal var indexedBind: IndexedBind<I>? = null

    internal var itemProvider: ItemProvider? = null
    internal var viewHolder: RecyclerView.ViewHolder? = null

    /**
     * Get position. If If this function is called before ViewHolder is attached
     * to RecyclerView, [RecyclerView.NO_POSITION] will be returned.
     * */
    fun getPosition(): Int {
        val provider = itemProvider ?: return RecyclerView.NO_POSITION
        val vh = viewHolder ?: return RecyclerView.NO_POSITION
        return provider.getPositionInSection(vh.adapterPosition)
    }

    /**
     * Get item. If this function is called before ViewHolder is attached
     * to RecyclerView, null will be returned.
     */
    @Suppress("UNCHECKED_CAST")
    fun getItem(): I? {
        val provider = itemProvider ?: return null
        val vh = viewHolder ?: return null
        return provider.getItem(vh.adapterPosition) as I?
    }

    fun bind(bind: Bind<I>) {
        this.bind = bind
    }

    fun bindIndexed(bind: IndexedBind<I>) {
        this.indexedBind = bind
    }
}
