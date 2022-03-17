package com.dokar.lazyrecycler

import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.ViewInstantiationBindScope
import kotlin.experimental.ExperimentalTypeInference

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * item(
 *  data = "Title"
 * ) { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     return@items root
 * }
 * ```
 */
@JvmName("viewRequiredItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    data: I,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewInstantiationBindScope<I>
) {
    newViewInstantiationItems(listOf(data), config, bind)
}

/**
 * Create a mutable item
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<Item> = ...
 * item(
 *  data = data.toMutableValue()
 * ) { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     root
 * }
 * ```
 */
@JvmName("viewRequiredItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    data: MutableValue<I>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewInstantiationBindScope<I>
) {
    items(
        data = data.current?.let { listOf(it) } ?: emptyList(),
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
 *     data = listOf("A", "B", "C")
 * ) { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     root
 * }
 * ```
 */
@JvmName("viewRequiredItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    data: List<I>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewInstantiationBindScope<I>
) {
    newViewInstantiationItems(data, config, bind)
}

/**
 * Create mutable items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutableValue()
 * ) { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     root
 * }
 * ```
 */
@JvmName("viewRequiredMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewInstantiationBindScope<I>
) {
    items(
        data.current ?: emptyList(),
        config.addExtra(data),
        bind
    )
}
