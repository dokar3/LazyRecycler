package com.dokar.lazyrecycler

import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.IndexedViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ViewBindingBind
import com.dokar.lazyrecycler.viewcreator.ViewBindingInflate
import kotlin.experimental.ExperimentalTypeInference

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * item(
 *     data = "Title",
 *     layout = ItemTitleViewBinding::inflate
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
@JvmName("viewBindingItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.item(
    data: I,
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewBindingBind<V, I>
) {
    items(listOf(data), layout, config, bind)
}

/**
 * Create a mutable item
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<Item> = ...
 * item(
 *     data = data.toMutableValue(),
 *     layout = ItemTitleViewBinding::inflate
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
@JvmName("viewBindingItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.item(
    data: MutableValue<I>,
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewBindingBind<V, I>
) {
    items(
        data = data.current?.let { listOf(it) } ?: emptyList(),
        layout = layout,
        config = config.addExtra(data),
        bind = bind,
    )
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * items(
 *     data = listOf("A", "B", "C"),
 *     layout = ItemViewBinding::inflate
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
@JvmName("viewBindingItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.items(
    data: List<I>,
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewBindingBind<V, I>
) {
    newBindingItems(data, layout, config, bind, null)
}

/**
 * Create mutable items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutableValue(),
 *     layout = ItemViewBinding::inflate
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
@JvmName("viewBindingMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewBindingBind<V, I>
) {
    items(
        data = data.current ?: emptyList(),
        layout = layout,
        config = config.addExtra(data),
        bind = bind,
    )
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * itemsIndexed(
 *     data = listOf("A", "B", "C"),
 *     layout = ItemViewBinding::inflate
 * ) { index, binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    data: List<I>,
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: IndexedViewBindingBind<V, I>
) {
    newBindingItems(data, layout, config, null, bind)
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * itemsIndexed(
 *     data = data.toMutableValue(),
 *     layout = ItemViewBinding::inflate
 * ) { index, binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    data: MutableValue<List<I>>,
    layout: ViewBindingInflate<V>,
    config: SectionConfig<I> = SectionConfig(),
    bind: IndexedViewBindingBind<V, I>
) {
    itemsIndexed(
        data = data.current ?: emptyList(),
        layout = layout,
        config = config.addExtra(data),
        bind = bind,
    )
}
