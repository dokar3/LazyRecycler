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
import com.dokar.lazyrecycler.viewcreator.ViewBindingCreator2
import com.dokar.lazyrecycler.viewcreator.ViewBindingInflate
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

    internal fun <I> newLayoutIdItems(
        items: List<I>,
        @LayoutRes layoutId: Int,
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

    internal fun <V : ViewBinding, I> newBindingItems(
        items: List<I>,
        inflate: ViewBindingInflate<V>,
        sectionId: Int,
        bind: ViewBindingBind<V, I>?,
        indexedBind: IndexedViewBindingBind<V, I>?
    ): SectionConfigurator<I> {
        val itemCreator = ViewBindingCreator2(inflate)
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

    internal fun <I> newViewRequiredItems(
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
fun <V : ViewBinding, I> template(
    inflate: ViewBindingInflate<V>,
    bind: ViewBindingBind<V, I>
): Template<I> {
    val itemViewCreator = ViewBindingCreator2(inflate)
    val itemBinder = ViewBindingBinder(bind, null)
    return Template(
        itemViewCreator,
        itemBinder,
    )
}

@JvmName("viewBindingTemplate")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> template(
    inflate: ViewBindingInflate<V>,
    bind: IndexedViewBindingBind<V, I>
): Template<I> {
    val itemViewCreator = ViewBindingCreator2(inflate)
    val itemBinder = ViewBindingBinder(null, bind)
    return Template(
        itemViewCreator,
        itemBinder
    )
}

fun <I> RecyclerBuilder.item(
    data: I,
    @LayoutRes layoutId: Int,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(listOf(data), layoutId, sectionId, bindScope)
}

fun <I> RecyclerBuilder.items(
    data: List<I>,
    @LayoutRes layoutId: Int,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return newLayoutIdItems(data, layoutId, sectionId, bindScope)
}

fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    @LayoutRes layoutId: Int,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(
        data.current ?: emptyList(), layoutId, sectionId, bindScope
    ).also {
        it.section().putExtra(data)
    }
}

@JvmName("viewBindingItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.item(
    data: I,
    inflate: ViewBindingInflate<V>,
    sectionId: Int = -1,
    bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(listOf(data), inflate, sectionId, bind)
}

@JvmName("viewBindingItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.items(
    data: List<I>,
    inflate: ViewBindingInflate<V>,
    sectionId: Int = -1,
    bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return newBindingItems(data, inflate, sectionId, bind, null)
}

@JvmName("viewBindingMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    inflate: ViewBindingInflate<V>,
    sectionId: Int = -1,
    bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(
        data.current ?: emptyList(),
        inflate,
        sectionId,
        bind
    ).also {
        it.section().putExtra(data)
    }
}

fun <V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    data: List<I>,
    inflate: ViewBindingInflate<V>,
    sectionId: Int = -1,
    bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return newBindingItems(data, inflate, sectionId, null, bind)
}

fun <V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    data: MutableValue<List<I>>,
    inflate: ViewBindingInflate<V>,
    sectionId: Int = -1,
    bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return itemsIndexed(
        data.current ?: emptyList(),
        inflate,
        sectionId,
        bind
    ).also {
        it.section().putExtra(data)
    }
}

@JvmName("viewRequiredItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    data: I,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return newViewRequiredItems(listOf(data), sectionId, bindScope)
}

@JvmName("viewRequiredItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    data: List<I>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return newViewRequiredItems(data, sectionId, bindScope)
}

@JvmName("viewRequiredMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(
        data.current ?: emptyList(),
        sectionId,
        bindScope
    ).also {
        it.section().putExtra(data)
    }
}

/**
 * item from a template
 * */
fun <I> RecyclerBuilder.item(
    data: I,
    template: Template<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    val creator = template.viewCreator
    val binder = template.itemBinder
    val section = Section(sectionId, creator, binder, listOf(data)).apply {
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
    data: List<I>,
    template: Template<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    val creator = template.viewCreator
    val binder = template.itemBinder
    val section = Section(sectionId, creator, binder, data).apply {
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
    data: MutableValue<List<I>>,
    template: Template<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    return items(
        data.current ?: emptyList(),
        template,
        sectionId
    ).also {
        it.section().putExtra(data)
    }
}
