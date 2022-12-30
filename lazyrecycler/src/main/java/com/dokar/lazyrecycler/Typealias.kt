package com.dokar.lazyrecycler

typealias AreItemsTheSame<I> = (oldItem: I, newItem: I) -> Boolean

typealias AreContentsTheSame<I> = (oldItem: I, newItem: I) -> Boolean

typealias AdapterCreator = (sections: List<Section<Any, Any>>) -> LazyAdapter
