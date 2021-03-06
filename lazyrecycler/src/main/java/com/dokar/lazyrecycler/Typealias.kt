package com.dokar.lazyrecycler

import android.view.View

typealias Where<I> = (item: I, position: Int) -> Boolean

typealias AreItemsTheSame<I> = (oldItem: I, newItem: I) -> Boolean

typealias AreContentsTheSame<I> = (oldItem: I, newItem: I) -> Boolean

typealias SpanSizeLookup = (position: Int) -> Int

typealias OnItemClick<I> = (view: View, item: I) -> Unit

typealias OnItemLongClick<I> = (view: View, item: I) -> Boolean
