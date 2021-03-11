@file:Suppress("DEPRECATION")

package com.dokar.lazyrecycler

import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.viewbinder.*
import com.dokar.lazyrecycler.viewcreator.*
import kotlin.experimental.ExperimentalTypeInference

open class RecyclerBuilder {

    private val sections: MutableList<Section<Any, Any>> = mutableListOf()

    @JvmSynthetic
    fun sections(): List<Section<Any, Any>> {
        return sections
    }

    @Suppress("UNCHECKED_CAST")
    @Deprecated(
        "Should not call this function directly, use item/items instead",
        ReplaceWith("")
    )
    fun addSection(section: Section<*, *>) {
        sections.add(section as Section<Any, Any>)
    }

    fun <I> newLayoutIdItems(
        @LayoutRes layoutId: Int,
        items: List<I>,
        sectionId: Int,
        bindScope: BindScope<I>
    ): SectionConfigurator<I> {
        val itemViewCreator = LayoutIdCreator(layoutId, bindScope)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            sectionId,
            itemViewCreator,
            itemBinder,
            items
        )

        addSection(section)

        return SectionConfigurator(section)
    }

    inline fun <reified V : ViewBinding, I> newBindingItems(
        items: List<I>,
        sectionId: Int,
        noinline bind: ViewBindingBind<V, I>?,
        noinline indexedBind: IndexedViewBindingBind<V, I>?
    ): SectionConfigurator<I> {
        val itemCreator = ViewBindingCreator(V::class.java)
        val itemBinder = ViewBindingBinder(bind, indexedBind)
        val section = Section(
            sectionId,
            itemCreator,
            itemBinder,
            items
        )
        addSection(section)
        return SectionConfigurator(section)
    }

    fun <I> newViewRequiredItems(
        items: List<I>,
        sectionId: Int,
        bindScope: ViewRequiredBindScope<I>
    ): SectionConfigurator<I> {
        val itemCreator = ViewRequiredCreator(bindScope)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            sectionId,
            itemCreator,
            itemBinder,
            items
        )
        addSection(section)
        return SectionConfigurator(section)
    }
}

@TemplateMarker
fun <I> template(
    @LayoutRes layoutId: Int,
    bindScope: BindScope<I>
): Template<I> {
    val itemViewCreator = LayoutIdCreator(layoutId, bindScope)
    val itemBinder = ItemViewBinder<I>()
    return Template(
        itemViewCreator,
        itemBinder
    )
}

@TemplateMarker
fun <I> template(
    bindScope: ViewRequiredBindScope<I>
): Template<I> {
    val itemViewCreator = ViewRequiredCreator(bindScope)
    val itemBinder = ItemViewBinder<I>()
    return Template(
        itemViewCreator,
        itemBinder
    )
}

@TemplateMarker
inline fun <reified V : ViewBinding, I> template(
    noinline bind: ViewBindingBind<V, I>
): Template<I> {
    val itemViewCreator = ViewBindingCreator(V::class.java)
    val itemBinder = ViewBindingBinder(bind, null)
    return Template(
        itemViewCreator,
        itemBinder,
    )
}

@TemplateMarker
inline fun <reified V : ViewBinding, I> template(
    noinline bind: IndexedViewBindingBind<V, I>
): Template<I> {
    val itemViewCreator = ViewBindingCreator(V::class.java)
    val itemBinder = ViewBindingBinder(null, bind)
    return Template(
        itemViewCreator,
        itemBinder
    )
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    @LayoutRes layoutId: Int,
    item: I,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(layoutId, listOf(item), sectionId, bindScope)
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    @LayoutRes layoutId: Int,
    items: List<I>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return newLayoutIdItems(layoutId, items, sectionId, bindScope)
}

@LazyRecyclerMarker
@JvmName("viewBindingItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.item(
    item: I,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(listOf(item), sectionId, bind)
}

@LazyRecyclerMarker
@JvmName("viewBindingItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.items(
    items: List<I>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return newBindingItems(items, sectionId, bind, null)
}

@LazyRecyclerMarker
inline fun <reified V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    items: List<I>,
    sectionId: Int = -1,
    noinline bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return newBindingItems(items, sectionId, null, bind)
}

@LazyRecyclerMarker
@JvmName("viewRequiredItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    item: I,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return newViewRequiredItems(listOf(item), sectionId, bindScope)
}

@LazyRecyclerMarker
@JvmName("viewRequiredItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    items: List<I>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return newViewRequiredItems(items, sectionId, bindScope)
}

/**
 * item from a template
 * */
@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    template: Template<I>,
    item: I,
    sectionId: Int = -1
): SectionConfigurator<I> {
    val creator = template.viewCreator
    val binder = template.itemBinder
    val section = Section(sectionId, creator, binder, listOf(item)).apply {
        onItemClick = template.onItemClick
        onItemLongClick = template.onItemLongClick
        differ = template.differ
        spanCountLookup = template.spanCountLookup
    }
    section.viewType = template.viewType
    addSection(section)
    return SectionConfigurator(section)
}

/**
 * items from a template
 * */
@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    template: Template<I>,
    items: List<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    val creator = template.viewCreator
    val binder = template.itemBinder
    val section = Section(sectionId, creator, binder, items).apply {
        onItemClick = template.onItemClick
        onItemLongClick = template.onItemLongClick
        differ = template.differ
        spanCountLookup = template.spanCountLookup
    }
    section.viewType = template.viewType
    addSection(section)
    return SectionConfigurator(section)
}
