package com.dokar.lazyrecycler

import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewcreator.ViewHolderCreator

@Suppress("UNCHECKED_CAST")
class Template<I>(
    viewCreator: ViewHolderCreator<out Any>,
    itemBinder: ItemBinder<out Any, I>,
) : Section<Any, I>(
    id = -1,
    viewHolderCreator = viewCreator as ViewHolderCreator<Any>,
    itemBinder = itemBinder as ItemBinder<Any, I>,
    items = emptyList()
)
