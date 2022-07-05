package com.dokar.lazyrecycler

import android.view.View
import androidx.annotation.LayoutRes
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.BindViewScope

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * item(layout = R.layout.item_title) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun RecyclerBuilder.item(
    @LayoutRes layout: Int,
    id: Int = 0,
    clicks: ((itemView: View) -> Unit)? = null,
    longClicks: ((itemView: View) -> Boolean)? = null,
    span: Int = 0,
    bind: BindViewScope<Any>.(view: View) -> Unit
) {
    layoutIdItems(
        items = listOf(Any()),
        layoutId = layout,
        id = id,
        clicks = if (clicks != null) {
            { v, _ -> clicks(v) }
        } else {
            null
        },
        longClicks = if (longClicks != null) {
            { v, _ -> longClicks(v) }
        } else {
            null
        },
        differ = {
            areItemsTheSame { oldItem, newItem -> oldItem == newItem }
            areContentsTheSame { oldItem, newItem -> oldItem == newItem }
        },
        span = if (span != 0) ({ span }) else null,
        bind = bind,
    )
}

/**
 * Create a mutable item
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<Item> = ...
 * item(
 *     data = data.toMutableValue(),
 *     layout = R.layout.item,
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I : Any> RecyclerBuilder.item(
    data: MutableValue<I>,
    @LayoutRes layout: Int,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: Int = 0,
    bind: BindViewScope<I>.(view: View) -> Unit
) {
    layoutIdItems(
        items = data.current?.let { listOf(it) } ?: emptyList(),
        layoutId = layout,
        id = id,
        mutableValue = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = if (span != 0) ({ span }) else null,
        bind = bind,
    )
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * items(
 *     items = listOf("A", "B", "C"),
 *     layout = R.layout.item,
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I : Any> RecyclerBuilder.items(
    items: List<I>,
    @LayoutRes layout: Int,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
    bind: BindViewScope<I>.(root: View) -> Unit,
) {
    layoutIdItems(
        items = items,
        layoutId = layout,
        id = id,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        extraViewTypes = extraViewTypes,
        bind = bind,
    )
}

/**
 * Create mutable items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutableValue(),
 *     layout = R.layout.item,
 * ) { root ->
 *     val tvTitle: TextView = root.findViewById(R.id.tv_title)
 *     bind { item ->
 *         ...
 *     }
 * }
 * ```
 */
fun <I : Any> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    @LayoutRes layout: Int,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
    bind: BindViewScope<I>.(view: View) -> Unit
) {
    layoutIdItems(
        items = data.current ?: emptyList(),
        layoutId = layout,
        id = id,
        mutableValue = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        extraViewTypes = extraViewTypes,
        bind = bind,
    )
}
