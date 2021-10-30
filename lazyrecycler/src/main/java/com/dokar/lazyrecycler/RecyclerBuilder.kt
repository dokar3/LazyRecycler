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
        config: SectionConfig<I>,
        bindScope: BindScope<I>
    ) {
        val itemViewCreator = LayoutIdCreator(layoutId, bindScope)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            config.sectionId,
            itemViewCreator,
            itemBinder,
            items
        )
        config.applyTo(section)
        addSection(section)
    }

    internal fun <V : ViewBinding, I> newBindingItems(
        items: List<I>,
        inflate: ViewBindingInflate<V>,
        config: SectionConfig<I>,
        bind: ViewBindingBind<V, I>?,
        indexedBind: IndexedViewBindingBind<V, I>?
    ) {
        val itemCreator = ViewBindingCreator2(inflate)
        val itemBinder = ViewBindingBinder(bind, indexedBind)
        val section = Section(
            config.sectionId,
            itemCreator,
            itemBinder,
            items
        )
        config.applyTo(section)
        addSection(section)
    }

    internal fun <I> newViewRequiredItems(
        items: List<I>,
        config: SectionConfig<I>,
        bindScope: ViewRequiredBindScope<I>
    ) {
        val itemCreator = ViewRequiredCreator(bindScope)
        val itemBinder = ItemViewBinder<I>()
        val section = Section(
            config.sectionId,
            itemCreator,
            itemBinder,
            items
        )
        config.applyTo(section)
        addSection(section)
    }
}

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
    bind: BindScope<I>
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
    bind: ViewRequiredBindScope<I>
): Template<I> {
    val itemViewCreator = ViewRequiredCreator(bind)
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
    val itemViewCreator = ViewBindingCreator2(layout)
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
    val itemViewCreator = ViewBindingCreator2(layout)
    val itemBinder = ViewBindingBinder(null, bind)
    return Template(
        itemViewCreator,
        itemBinder
    ).also {
        config.applyTo(it)
    }
}

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
    bind: BindScope<I>
) {
    items(listOf(data), layout, config, bind)
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
    bind: BindScope<I>
) {
    newLayoutIdItems(data, layout, config, bind)
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutSource(),
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
    bind: BindScope<I>
) {
    items(
        data.current ?: emptyList(),
        layout,
        config.addExtra(data),
        bind
    )
}

/**
 * Create item
 *
 * ### Example:
 * ```kotlin
 * items(
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
 * Create items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutSource(),
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
        data.current ?: emptyList(),
        layout,
        config.addExtra(data),
        bind
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
 *     data = data.toMutSource(),
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
        data.current ?: emptyList(),
        layout,
        config.addExtra(data),
        bind
    )
}

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
    bind: ViewRequiredBindScope<I>
) {
    newViewRequiredItems(listOf(data), config, bind)
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
 *     return@items root
 * }
 * ```
 */
@JvmName("viewRequiredItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    data: List<I>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewRequiredBindScope<I>
) {
    newViewRequiredItems(data, config, bind)
}

/**
 * Create items
 *
 * ### Example:
 * ```kotlin
 * val data: Observable<List<Item>> = ...
 * items(
 *     data = data.toMutSource()
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
@JvmName("viewRequiredMutableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    data: MutableValue<List<I>>,
    config: SectionConfig<I> = SectionConfig(),
    bind: ViewRequiredBindScope<I>
) {
    items(
        data.current ?: emptyList(),
        config.addExtra(data),
        bind
    )
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
    val creator = template.viewCreator
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
    val creator = template.viewCreator
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
 * item(data = data.toMutSource(), template = template)
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
