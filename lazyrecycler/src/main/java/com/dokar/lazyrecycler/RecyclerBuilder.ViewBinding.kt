package com.dokar.lazyrecycler

import android.view.View
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewcreator.ViewBindingInflate

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * item(layout = ItemTitleViewBinding::inflate) { binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding> RecyclerBuilder.item(
    layout: ViewBindingInflate<V>,
    id: Int = 0,
    clicks: ((itemView: View) -> Unit)? = null,
    longClicks: ((itemView: View) -> Boolean)? = null,
    span: Int = 0,
    bind: (binding: V) -> Unit
) {
    viewBindingItems(
        items = listOf(Any()),
        inflate = layout,
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
        bind = { binding, _ -> bind(binding) },
        indexedBind = null,
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
 *     layout = ItemTitleViewBinding::inflate,
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I : Any> RecyclerBuilder.item(
    data: MutableValue<I>,
    layout: ViewBindingInflate<V>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: Int = 0,
    bind: (binding: V, item: I) -> Unit
) {
    viewBindingItems(
        items = data.current?.let { listOf(it) } ?: emptyList(),
        inflate = layout,
        id = id,
        mutableData = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = if (span != 0) ({ span }) else null,
        bind = bind,
        indexedBind = null,
    )
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * items(
 *     items = listOf("A", "B", "C"),
 *     layout = ItemViewBinding::inflate,
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I : Any> RecyclerBuilder.items(
    items: List<I>,
    layout: ViewBindingInflate<V>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
    bind: (binding: V, item: I) -> Unit
) {
    viewBindingItems(
        items = items,
        inflate = layout,
        id = id,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        extraViewTypes = extraViewTypes,
        bind = bind,
        indexedBind = null,
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
 *     layout = ItemViewBinding::inflate,
 * ) { binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I : Any> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    layout: ViewBindingInflate<V>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
    bind: (binding: V, item: I) -> Unit
) {
    viewBindingItems(
        items = data.current ?: emptyList(),
        inflate = layout,
        id = id,
        mutableData = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        extraViewTypes = extraViewTypes,
        bind = bind,
        indexedBind = null,
    )
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * itemsIndexed(
 *     items = listOf("A", "B", "C"),
 *     layout = ItemViewBinding::inflate,
 * ) { index, binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I : Any> RecyclerBuilder.itemsIndexed(
    items: List<I>,
    layout: ViewBindingInflate<V>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
    bind: (index: Int, binding: V, item: I) -> Unit
) {
    viewBindingItems(
        items = items,
        inflate = layout,
        id = id,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        extraViewTypes = extraViewTypes,
        bind = null,
        indexedBind = bind,
    )
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * itemsIndexed(
 *     data = data.toMutableValue(),
 *     layout = ItemViewBinding::inflate,
 * ) { index, binding, item ->
 *     ...
 * }
 * ```
 */
fun <V : ViewBinding, I : Any> RecyclerBuilder.itemsIndexed(
    data: MutableValue<List<I>>,
    layout: ViewBindingInflate<V>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    span: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
    bind: (index: Int, binding: V, item: I) -> Unit
) {
    viewBindingItems(
        items = data.current ?: emptyList(),
        inflate = layout,
        id = id,
        mutableData = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        span = span,
        extraViewTypes = extraViewTypes,
        bind = null,
        indexedBind = bind,
    )
}
