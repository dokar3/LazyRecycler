package com.dokar.lazyrecycler

import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.BindViewScope

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * item { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     root
 * }
 * ```
 */
fun RecyclerBuilder.item(
    id: Int = 0,
    clicks: ((itemView: View) -> Unit)? = null,
    longClicks: ((itemView: View) -> Boolean)? = null,
    span: Int = 0,
    bind: BindViewScope<Any>.(parent: ViewGroup) -> View
) {
    viewInstantiationItems(
        items = listOf(Any()),
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
 * item(data = data.toMutableValue()) { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     root
 * }
 * ```
 */
fun <I : Any> RecyclerBuilder.item(
    data: MutableValue<I>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: Int = 0,
    bind: BindViewScope<I>.(parent: ViewGroup) -> View
) {
    viewInstantiationItems(
        items = data.current?.let { listOf(it) } ?: emptyList(),
        id = id,
        mutableData = data,
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
 *     items = listOf("A", "B", "C")
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
fun <I : Any> RecyclerBuilder.items(
    items: List<I>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    bind: BindViewScope<I>.(parent: ViewGroup) -> View
) {
    viewInstantiationItems(
        items = items,
        id = id,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        bind = bind,
    )
}

/**
 * Create mutable items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(data = data.toMutableValue()) { parent ->
 *     val root = LinearLayout(context)
 *     ...
 *     bind { item ->
 *         ...
 *     }
 *     root
 * }
 * ```
 */
fun <I : Any> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    bind: BindViewScope<I>.(parent: ViewGroup) -> View
) {
    viewInstantiationItems(
        items = data.current ?: emptyList(),
        id = id,
        mutableData = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        bind = bind,
    )
}
