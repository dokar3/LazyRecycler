package com.dokar.lazyrecycler

import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewcreator.ViewCreator

@Suppress("UNCHECKED_CAST")
class Template<I>(
    viewCreator: ViewCreator<out Any>,
    itemBinder: ItemBinder<out Any, I>,
) : Section<Any, I>(
    id = -1,
    viewCreator = viewCreator as ViewCreator<Any>,
    itemBinder = itemBinder as ItemBinder<Any, I>,
    items = emptyList()
)
