package com.dokar.lazyrecycler

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.ItemViewBinder
import com.dokar.lazyrecycler.viewbinder.ViewBindingBinder
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
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: ((position: Int) -> Int)? = null,
    bind: BindViewScope<I>.(view: View) -> Unit
): Template<I> {
    val viewHolderCreator = LayoutIdCreator(layout, bind)
    val itemBinder = ItemViewBinder<I>()
    return Template(
        viewHolderCreator = viewHolderCreator,
        itemBinder = itemBinder,
        clicks = clicks,
        longClicks = longClicks,
        differ = if (differ != null) Differ<I>().also(differ) else null,
        spans = spans,
    )
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
fun <I> template(
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: ((position: Int) -> Int)? = null,
    bind: BindViewScope<I>.(parent: ViewGroup) -> View
): Template<I> {
    val viewHolderCreator = ViewInstantiationCreator(bind)
    val itemBinder = ItemViewBinder<I>()
    return Template(
        viewHolderCreator = viewHolderCreator,
        itemBinder = itemBinder,
        clicks = clicks,
        longClicks = longClicks,
        differ = if (differ != null) Differ<I>().also(differ) else null,
        spans = spans,
    )
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
fun <V : ViewBinding, I> template(
    layout: ViewBindingInflate<V>,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: ((position: Int) -> Int)? = null,
    bind: (binding: V, item: I) -> Unit
): Template<I> {
    val viewHolderCreator = ViewBindingCreator(layout)
    val itemBinder = ViewBindingBinder(bind, null)
    return Template(
        viewHolderCreator = viewHolderCreator,
        itemBinder = itemBinder,
        clicks = clicks,
        longClicks = longClicks,
        differ = if (differ != null) Differ<I>().also(differ) else null,
        spans = spans,
    )
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
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <V : ViewBinding, I> template(
    layout: ViewBindingInflate<V>,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: ((position: Int) -> Int)? = null,
    bind: (index: Int, binding: V, item: I) -> Unit
): Template<I> {
    val viewHolderCreator = ViewBindingCreator(layout)
    val itemBinder = ViewBindingBinder(null, bind)
    return Template(
        viewHolderCreator = viewHolderCreator,
        itemBinder = itemBinder,
        clicks = clicks,
        longClicks = longClicks,
        differ = if (differ != null) Differ<I>().also(differ) else null,
        spans = spans,
    )
}

/**
 * Create an item from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * item(template = template)
 * ```
 */
fun RecyclerBuilder.item(
    template: Template<Any>,
    id: Int = 0,
    clicks: ((itemView: View) -> Unit)? = null,
    longClicks: ((itemView: View) -> Boolean)? = null,
    spans: Int = 0,
) {
    templateItems(
        items = listOf(Any()),
        template = template,
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
        spans = if (spans != 0) ({ spans }) else null,
    )
}

/**
 * Create an item from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * item(
 *     item = "Title",
 *     template = template,
 * )
 * ```
 */
fun <I> RecyclerBuilder.item(
    item: I,
    template: Template<I>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: Int = 0,
) {
    templateItems(
        items = listOf(item),
        template = template,
        id = id,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        spans = if (spans != 0) ({ spans }) else null,
    )
}

/**
 * Create a mutable item from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * val data: Observable<Item> = ...
 * item(
 *     data = data.toMutableValue(),
 *     template = template,
 * )
 * ```
 */
fun <I> RecyclerBuilder.item(
    data: MutableValue<I>,
    template: Template<I>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: Int = 0,
) {
    templateItems(
        items = data.current?.let { listOf(it) } ?: emptyList(),
        template = template,
        id = id,
        mutableData = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        spans = if (spans != 0) ({ spans }) else null,
    )
}

/**
 * Create items from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * items(
 *     items = listOf("A", "B", "C"),
 *     template = template,
 * )
 * ```
 */
fun <I> RecyclerBuilder.items(
    items: List<I>,
    template: Template<I>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
) {
    templateItems(
        items = items,
        template = template,
        id = id,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        spans = spans,
        extraViewTypes = extraViewTypes,
    )
}

/**
 * Create mutable items from a template
 *
 * ### Example:
 * ```kotlin
 * val template = template { ... }
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutableValue(),
 *     template = template,
 * )
 * ```
 */
fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    template: Template<I>,
    id: Int = 0,
    clicks: ((itemView: View, item: I) -> Unit)? = null,
    longClicks: ((itemView: View, item: I) -> Boolean)? = null,
    differ: (Differ<I>.() -> Unit)? = null,
    spans: ((position: Int) -> Int)? = null,
    extraViewTypes: List<ViewType<I>>? = null,
) {
    templateItems(
        items = data.current ?: emptyList(),
        template = template,
        id = id,
        mutableData = data,
        clicks = clicks,
        longClicks = longClicks,
        differ = differ,
        spans = spans,
        extraViewTypes = extraViewTypes,
    )
}
