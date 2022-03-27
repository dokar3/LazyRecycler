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
