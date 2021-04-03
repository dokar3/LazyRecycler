package com.dokar.lazyrecycler.viewbinder

class ItemViewBinder<I> : ItemBinder<BindHolder<I>, I> {

    override fun bind(view: BindHolder<I>, item: I, position: Int) {
        val bind = view.bind
        val bindIndexed = view.indexedBind
        if (bind != null) {
            bind.invoke(item)
        } else bindIndexed?.invoke(position, item)
    }
}
