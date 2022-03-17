package com.dokar.lazyrecycler

import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
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
import kotlin.experimental.ExperimentalTypeInference

/**
 * Create a template
 *
 * ### Example:
 * ```kotlin
 * val normalItem = template(
 *     layout = R.layout.item
 * ) { root ->
 *      val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *      bind {
 *          ...
 *      }
 * }
 *
 * items(
 *     data = listOf("A", "B", "C"),
 *     template = normalItem
 * )
 * ```
 */
fun <I> template(
    @LayoutRes layout: Int,
    config: SectionConfig<I> = SectionConfig(),
    bind: LayoutIdBindScope<I>
): Template<I> {
    val itemViewCreator = LayoutIdCreator(layout, bind)
    val itemBinder = ItemViewBinder<I>()
    return Template(
        itemViewCreator,
        itemBinder
    ).also {
        config.applyTo(it)
    }
}

/**
 * Create a template
 *
 * ### Example:
 * ```kotlin
 * val normalItem = template { parent ->
 *      val root = LinearLayout(context)
 *      // bind
 *      ...
 *      return@template root
 * }
 *
 * items(
 *     data = listOf("A", "B", "C"),
 *     template = normalItem
 * )
 * ```
 */
@JvmName("viewRequiredTemplate")
fun <I> template(
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewInstantiationBindScope<I>
): Template<I> {
    val itemViewCreator = ViewInstantiationCreator(bind)
    val itemBinder = ItemViewBinder<I>()
    return Template(
        itemViewCreator,
        itemBinder
    ).also {
        config.applyTo(it)
    }
}

/**
 * Create a template
 *
 * ### Example:
 * ```kotlin
 * val normalItem = template(ItemNormalViewBinding::inflate) { binding, item: String ->
 *      // bind
 *      ...
 * }
 *
 * items(
 *     data = listOf("A", "B", "C"),
 *     template = normalItem
 * )
 * ```
 */
@JvmName("viewBindingTemplate")
fun <V : ViewBinding, I> template(
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewBindingBind<V, I>
): Template<I> {
    val itemViewCreator = ViewBindingCreator(layout)
    val itemBinder = ViewBindingBinder(bind, null)
    return Template(
        itemViewCreator,
        itemBinder,
    ).also {
        config.applyTo(it)
    }
}

/**
 * Create a template
 *
 * ### Example:
 * ```kotlin
 * val normalItem = template(ItemNormalViewBinding::inflate) { index, binding, item: String ->
 *      // bind
 *      ...
 * }
 *
 * items(
 *     data = listOf("A", "B", "C"),
 *     template = normalItem
 * )
 * ```
 */
@JvmName("viewBindingTemplate")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> template(
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: IndexedViewBindingBind<V, I>
): Template<I> {
    val itemViewCreator = ViewBindingCreator(layout)
    val itemBinder = ViewBindingBinder(null, bind)
    return Template(
        itemViewCreator,
        itemBinder
    ).also {
        config.applyTo(it)
    }
}

/**
 * Create an item from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * item(data = "Title", template = template)
 * ```
 */
fun <I> RecyclerBuilder.item(
    data: I,
    template: Template<I>,
    config: SectionConfig<I> = SectionConfig(),
) {
    val creator = template.viewHolderCreator
    val binder = template.itemBinder
    val section = Section(config.sectionId, creator, binder, listOf(data)).apply {
        onItemClick = template.onItemClick
        onItemLongClick = template.onItemLongClick
        differ = template.differ
        spanSizeLookup = template.spanSizeLookup
    }
    section.viewType = template.viewType
    config.applyTo(section)
    addSection(section)
}

/**
 * Create a mutable item from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * val data: Observable<Item> = ...
 * item(data = data.toMutableValue(), template = template)
 * ```
 */
fun <I> RecyclerBuilder.item(
    data: MutableValue<I>,
    template: Template<I>,
    config: SectionConfig<I> = SectionConfig(),
) {
    items(
        data = data.current?.let { listOf(it) } ?: emptyList(),
        template = template,
        config = config.addExtra(data),
    )
}

/**
 * Create items from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * item(data = listOf("A", "B", "C"), template = template)
 * ```
 */
fun <I> RecyclerBuilder.items(
    data: List<I>,
    template: Template<I>,
    config: SectionConfig<I> = SectionConfig(),
) {
    val creator = template.viewHolderCreator
    val binder = template.itemBinder
    val section = Section(config.sectionId, creator, binder, data).apply {
        onItemClick = template.onItemClick
        onItemLongClick = template.onItemLongClick
        differ = template.differ
        spanSizeLookup = template.spanSizeLookup
    }
    section.viewType = template.viewType
    config.applyTo(section)
    addSection(section)
}

/**
 * Create items from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * val data: Observable<List<Item>> = ...
 * item(data = data.toMutableValue(), template = template)
 * ```
 */
fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    template: Template<I>,
    config: SectionConfig<I> = SectionConfig(),
) {
    items(
        data.current ?: emptyList(),
        template,
        config.addExtra(data)
    )
}
