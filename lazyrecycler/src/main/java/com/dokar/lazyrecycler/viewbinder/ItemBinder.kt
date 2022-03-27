package com.dokar.lazyrecycler.viewbinder

interface ItemBinder<V, I> {
    fun bind(bindable: V, item: I, position: Int)
}
