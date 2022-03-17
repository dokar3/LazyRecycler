package com.dokar.lazyrecycler

import androidx.annotation.LayoutRes
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.LayoutIdBindScope

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * item(
 *     data = "Title",
 *     layout = R.layout.item_title
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I> RecyclerBuilder.item(
    data: I,
    @LayoutRes layout: Int,
    config: SectionConfig<I> = SectionConfig(),
    bind: LayoutIdBindScope<I>
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
 *     layout = R.layout.item
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I> RecyclerBuilder.item(
    data: MutableValue<I>,
    @LayoutRes layout: Int,
    config: SectionConfig<I> = SectionConfig(),
    bind: LayoutIdBindScope<I>
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
 *     layout = R.layout.item
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I> RecyclerBuilder.items(
    data: List<I>,
    @LayoutRes layout: Int,
    config: SectionConfig<I> = SectionConfig(),
    bind: LayoutIdBindScope<I>
) {
    newLayoutIdItems(data, layout, config, bind)
}

/**
 * Create mutable items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutableValue(),
 *     layout = R.layout.item
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    @LayoutRes layout: Int,
    config: SectionConfig<I> = SectionConfig(),
    bind: LayoutIdBindScope<I>
) {
    items(
        data = data.current ?: emptyList(),
        layout = layout,
        config = config.addExtra(data),
        bind = bind,
    )
}
