package com.dokar.lazyrecycler.viewbinder

typealias Bind<I> = (item: I) -> Unit

typealias IndexedBind<I> = (index: Int, item: I) -> Unit

class ItemViewBinder<I> : ItemBinder<ItemViewBinder.BindWrapper<I>, I> {

    override fun bind(view: BindWrapper<I>, item: I, position: Int) {
        val bind = view.bind
        val bindIndexed = view.indexedBind
        if (bind != null) {
            bind.invoke(item)
        } else bindIndexed?.invoke(position, item)
    }

    class BindWrapper<I> {

        internal var bind: Bind<I>? = null
        internal var indexedBind: IndexedBind<I>? = null

        fun bind(bind: Bind<I>) {
            this.bind = bind
        }

        fun bindIndexed(bind: IndexedBind<I>) {
            this.indexedBind = bind
        }
    }
}
