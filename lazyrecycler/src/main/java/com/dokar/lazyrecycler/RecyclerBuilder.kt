@file:Suppress("DEPRECATION")

package com.dokar.lazyrecycler

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.ItemViewBinder
import com.dokar.lazyrecycler.viewbinder.ViewBindingBinder
import com.dokar.lazyrecycler.viewcreator.LayoutIdCreator
import com.dokar.lazyrecycler.viewcreator.ViewBindingCreator
import com.dokar.lazyrecycler.viewcreator.ViewBindingInflate
import com.dokar.lazyrecycler.viewcreator.ViewInstantiationCreator

class RecyclerBuilder {
    private val sections: MutableList<Section<Any, Any>> = mutableListOf()

    internal fun sections(): List<Section<Any, Any>> {
        return sections
    }

    @Suppress("UNCHECKED_CAST")
    internal fun addSection(section: Section<*, *>) {
        sections.add(section as Section<Any, Any>)
    }

    internal fun <I> layoutIdItems(
        items: List<I>,
        @LayoutRes layoutId: Int,
        id: Int = 0,
        mutableValue: MutableValue<*>? = null,
        clicks: ((itemView: View, item: I) -> Unit)? = null,
        longClicks: ((itemView: View, item: I) -> Boolean)? = null,
        differ: (Differ<I>.() -> Unit)? = null,
        span: ((position: Int) -> Int)? = null,
        extraViewTypes: List<ViewType<I>>? = null,
        bind: BindViewScope<I>.(root: View) -> Unit
    ) {
        val viewHolderCreator = LayoutIdCreator(layoutId, bind)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            id = id,
            viewHolderCreator = viewHolderCreator,
            itemBinder = itemBinder,
            items = items,
            data = mutableValue,
            clicks = clicks,
            longClicks = longClicks,
            differ = if (differ != null) Differ<I>().also(differ) else null,
            span = span,
            extraViewTypes = extraViewTypes,
        )
        addSection(section)
    }

    internal fun <V : ViewBinding, I> viewBindingItems(
        items: List<I>,
        inflate: ViewBindingInflate<V>,
        id: Int = 0,
        mutableData: MutableValue<*>? = null,
        clicks: ((itemView: View, item: I) -> Unit)? = null,
        longClicks: ((itemView: View, item: I) -> Boolean)? = null,
        differ: (Differ<I>.() -> Unit)? = null,
        span: ((position: Int) -> Int)? = null,
        bind: ((binding: V, item: I) -> Unit)?,
        extraViewTypes: List<ViewType<I>>? = null,
        indexedBind: ((index: Int, binding: V, item: I) -> Unit)?
    ) {
        val viewHolderCreator = ViewBindingCreator(inflate)
        val itemBinder = ViewBindingBinder(bind, indexedBind)
        val section = Section(
            id = id,
            viewHolderCreator = viewHolderCreator,
            itemBinder = itemBinder,
            items = items,
            data = mutableData,
            clicks = clicks,
            longClicks = longClicks,
            differ = if (differ != null) Differ<I>().also(differ) else null,
            span = span,
            extraViewTypes = extraViewTypes,
        )
        addSection(section)
    }

    internal fun <I> viewInstantiationItems(
        items: List<I>,
        id: Int = 0,
        mutableData: MutableValue<*>? = null,
        clicks: ((itemView: View, item: I) -> Unit)? = null,
        longClicks: ((itemView: View, item: I) -> Boolean)? = null,
        differ: (Differ<I>.() -> Unit)? = null,
        span: ((position: Int) -> Int)? = null,
        extraViewTypes: List<ViewType<I>>? = null,
        bind: BindViewScope<I>.(parent: ViewGroup) -> View
    ) {
        val viewHolderCreator = ViewInstantiationCreator(bind)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            id = id,
            viewHolderCreator = viewHolderCreator,
            itemBinder = itemBinder,
            items = items,
            data = mutableData,
            clicks = clicks,
            longClicks = longClicks,
            differ = if (differ != null) Differ<I>().also(differ) else null,
            span = span,
            extraViewTypes = extraViewTypes,
        )
        addSection(section)
    }

    internal fun <I> templateItems(
        items: List<I>,
        template: Template<I>,
        id: Int = 0,
        mutableData: MutableValue<*>? = null,
        clicks: ((itemView: View, item: I) -> Unit)? = null,
        longClicks: ((itemView: View, item: I) -> Boolean)? = null,
        differ: (Differ<I>.() -> Unit)? = null,
        span: ((position: Int) -> Int)? = null,
        extraViewTypes: List<ViewType<I>>? = null,
    ) {
        val section = Section(
            id = id,
            viewHolderCreator = template.viewHolderCreator,
            itemBinder = template.itemBinder,
            items = items,
            data = mutableData,
            clicks = clicks,
            longClicks = longClicks,
            differ = if (differ != null) Differ<I>().also(differ) else null,
            span = span,
            extraViewTypes = extraViewTypes,
        )
        addSection(section)
    }
}
