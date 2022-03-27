package com.dokar.lazyrecycler.viewbinder

internal class ItemViewBinder<I> : ItemBinder<SimpleBindable<I>, I> {
    override fun bind(bindable: SimpleBindable<I>, item: I, position: Int) {
        val bind = bindable.bind
        if (bind != null) {
            bind.invoke(item)
        } else {
            bindable.indexedBind?.invoke(position, item)
        }
    }
}
