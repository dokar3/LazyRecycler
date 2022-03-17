package com.dokar.lazyrecycler.viewbinder

interface ItemBinder<V, I> {
    fun bind(view: V, item: I, position: Int)
}
