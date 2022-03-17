package com.dokar.lazyrecycler.viewbinder

class ItemViewBinder<I> : ItemBinder<BindViewScope<I>, I> {
    override fun bind(view: BindViewScope<I>, item: I, position: Int) {
        // TODO: Handle this in better way
        view as BindViewScopeImpl
        val bind = view.bind
        val bindIndexed = view.indexedBind
        if (bind != null) {
            bind.invoke(item)
        } else bindIndexed?.invoke(position, item)
    }
}
