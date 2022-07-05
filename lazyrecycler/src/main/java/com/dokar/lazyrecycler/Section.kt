package com.dokar.lazyrecycler

import android.view.View
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewcreator.ViewHolderCreator

/**
 * List section.
 *
 * @param V View type, could be [ViewBinding] or [BindViewScope].
 * @param I Item type.
 */
open class Section<V, I : Any>(
    val id: Int,
    val viewHolderCreator: ViewHolderCreator<V>,
    val itemBinder: ItemBinder<V, I>,
    var items: List<I>,
    val data: MutableValue<*>? = null,
    val clicks: ((View, I) -> Unit)? = null,
    val longClicks: ((View, I) -> Boolean)? = null,
    val differ: Differ<I>? = null,
    val span: ((Int) -> Int)? = null,
    val extraViewTypes: List<ViewType<I>>? = null
) {
    val defaultViewType: Int = Utils.newViewType()
}
