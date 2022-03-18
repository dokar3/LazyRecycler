package com.dokar.lazyrecycler.viewbinder

import androidx.viewbinding.ViewBinding

class ViewBindingBinder<V : ViewBinding, I>(
    private val bind: ((binding: V, item: I) -> Unit)? = null,
    private val indexedBind: ((index: Int, binding: V, item: I) -> Unit)? = null
) : ItemBinder<V, I> {
    override fun bind(view: V, item: I, position: Int) {
        if (bind != null) {
            bind.invoke(view, item)
        } else indexedBind?.invoke(position, view, item)
    }
}
