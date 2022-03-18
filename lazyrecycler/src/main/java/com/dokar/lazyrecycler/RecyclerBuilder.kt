@file:Suppress("DEPRECATION")

package com.dokar.lazyrecycler

import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.viewbinder.IndexedViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ItemViewBinder
import com.dokar.lazyrecycler.viewbinder.LayoutIdBindScope
import com.dokar.lazyrecycler.viewbinder.ViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ViewBindingBinder
import com.dokar.lazyrecycler.viewbinder.ViewInstantiationBindScope
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

    internal fun <I> newLayoutIdItems(
        items: List<I>,
        @LayoutRes layoutId: Int,
        config: SectionConfig<I>,
        bindScope: LayoutIdBindScope<I>
    ) {
        val itemViewCreator = LayoutIdCreator(layoutId, bindScope)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            config.sectionId,
            itemViewCreator,
            itemBinder,
            items
        )
        config.applyTo(section)
        addSection(section)
    }

    internal fun <V : ViewBinding, I> newBindingItems(
        items: List<I>,
        inflate: ViewBindingInflate<V>,
        config: SectionConfig<I>,
        bind: ViewBindingBind<V, I>?,
        indexedBind: IndexedViewBindingBind<V, I>?
    ) {
        val itemCreator = ViewBindingCreator(inflate)
        val itemBinder = ViewBindingBinder(bind, indexedBind)
        val section = Section(
            config.sectionId,
            itemCreator,
            itemBinder,
            items
        )
        config.applyTo(section)
        addSection(section)
    }

    internal fun <I> newViewInstantiationItems(
        items: List<I>,
        config: SectionConfig<I>,
        bindScope: ViewInstantiationBindScope<I>
    ) {
        val itemCreator = ViewInstantiationCreator(bindScope)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            config.sectionId,
            itemCreator,
            itemBinder,
            items
        )
        config.applyTo(section)
        addSection(section)
    }
}
