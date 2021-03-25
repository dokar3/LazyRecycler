package com.dokar.lazyrecycler.viewbinder

import androidx.viewbinding.ViewBinding

typealias ViewBindingBind<V, I> = (binding: V, item: I) -> Unit

typealias IndexedViewBindingBind<V, I> = (index: Int, binding: V, item: I) -> Unit

class ViewBindingBinder<V : ViewBinding, I>(
    private val bind: ViewBindingBind<V, I>? = null,
    private val indexedBind: IndexedViewBindingBind<V, I>? = null
) : ItemBinder<V, I> {

    override fun bind(view: V, item: I, position: Int) {
        if (bind != null) {
            bind.invoke(view, item)
        } else indexedBind?.invoke(position, view, item)
    }
}
