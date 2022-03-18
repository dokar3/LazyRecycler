package com.dokar.lazyrecycler

import android.view.View
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewcreator.ViewHolderCreator

@Suppress("UNCHECKED_CAST")
class Template<I>(
    viewHolderCreator: ViewHolderCreator<out Any>,
    itemBinder: ItemBinder<out Any, I>,
    clicks: ((View, I) -> Unit)? = null,
    longClicks: ((View, I) -> Boolean)? = null,
    differ: Differ<I>? = null,
    span: ((Int) -> Int)? = null,
) : Section<Any, I>(
    id = -1,
    viewHolderCreator = viewHolderCreator as ViewHolderCreator<Any>,
    itemBinder = itemBinder as ItemBinder<Any, I>,
    items = emptyList(),
    clicks = clicks,
    longClicks = longClicks,
    differ = differ,
    span = span,
)
