package com.dokar.lazyrecycler

typealias AreItemsTheSame<I> = (oldItem: I, newItem: I) -> Boolean

typealias AreContentsTheSame<I> = (oldItem: I, newItem: I) -> Boolean

typealias AdapterCreator = (sections: MutableList<Section<Any, Any>>) -> LazyAdapter
