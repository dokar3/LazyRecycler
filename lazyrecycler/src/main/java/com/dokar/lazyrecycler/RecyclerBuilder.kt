@file:Suppress("DEPRECATION")

package com.dokar.lazyrecycler

import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.IndexedViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ItemViewBinder
import com.dokar.lazyrecycler.viewbinder.ViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ViewBindingBinder
import com.dokar.lazyrecycler.viewcreator.BindScope
import com.dokar.lazyrecycler.viewcreator.LayoutIdCreator
import com.dokar.lazyrecycler.viewcreator.ViewBindingCreator
import com.dokar.lazyrecycler.viewcreator.ViewRequiredBindScope
import com.dokar.lazyrecycler.viewcreator.ViewRequiredCreator
import kotlin.experimental.ExperimentalTypeInference

open class RecyclerBuilder {

    private val sections: MutableList<Section<Any, Any>> = mutableListOf()

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

@JvmName("viewRequiredTemplate")
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

@JvmName("viewBindingTemplate")
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

fun <I> RecyclerBuilder.item(
    @LayoutRes layoutId: Int,
    item: I,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(layoutId, listOf(item), sectionId, bindScope)
}

fun <I> RecyclerBuilder.items(
    @LayoutRes layoutId: Int,
    items: List<I>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return newLayoutIdItems(layoutId, items, sectionId, bindScope)
}

fun <I> RecyclerBuilder.items(
    @LayoutRes layoutId: Int,
    source: MutableValue<List<I>>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(
        layoutId, source.current ?: emptyList(), sectionId, bindScope
    ).also {
        it.section().putExtra(source)
    }
}

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

@JvmName("viewBindingMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.items(
    source: MutableValue<List<I>>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(source.current ?: emptyList(), sectionId, bind).also {
        it.section().putExtra(source)
    }
}

inline fun <reified V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    items: List<I>,
    sectionId: Int = -1,
    noinline bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return newBindingItems(items, sectionId, null, bind)
}

inline fun <reified V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    source: MutableValue<List<I>>,
    sectionId: Int = -1,
    noinline bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return itemsIndexed(source.current ?: emptyList(), sectionId, bind).also {
        it.section().putExtra(source)
    }
}

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

@JvmName("viewRequiredMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    source: MutableValue<List<I>>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(
        source.current ?: emptyList(),
        sectionId,
        bindScope
    ).also {
        it.section().putExtra(source)
    }
}

/**
 * item from a template
 * */
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

fun <I> RecyclerBuilder.items(
    template: Template<I>,
    source: MutableValue<List<I>>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    return items(
        template,
        source.current ?: emptyList(),
        sectionId
    ).also {
        it.section().putExtra(source)
    }
}
