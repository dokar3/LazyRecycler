package com.dokar.lazyrecycler.viewbinder

import androidx.viewbinding.ViewBinding

internal class ViewBindingBinder<V : ViewBinding, I>(
    private val bind: ((binding: V, item: I) -> Unit)? = null,
    private val indexedBind: ((index: Int, binding: V, item: I) -> Unit)? = null
) : ItemBinder<V, I> {
    override fun bind(bindable: V, item: I, position: Int) {
        if (bind != null) {
            bind.invoke(bindable, item)
        } else {
            indexedBind?.invoke(position, bindable, item)
        }
    }
}
