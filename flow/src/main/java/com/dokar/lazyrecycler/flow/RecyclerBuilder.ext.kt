package com.dokar.lazyrecycler.flow

import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.*
import com.dokar.lazyrecycler.viewbinder.IndexedViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ViewBindingBind
import com.dokar.lazyrecycler.viewcreator.BindScope
import com.dokar.lazyrecycler.viewcreator.ViewRequiredBindScope
import kotlinx.coroutines.flow.Flow
import kotlin.experimental.ExperimentalTypeInference

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    layoutId: Int,
    item: Flow<I>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(layoutId, emptyList(), sectionId, bindScope).also {
        it.section().applyExtra { liveItems = item }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    layoutId: Int,
    items: Flow<List<I>>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(layoutId, emptyList(), sectionId, bindScope).also {
        it.section().applyExtra { liveItems = items }
    }
}

@LazyRecyclerMarker
@JvmName("viewBindingFlowItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.item(
    item: Flow<I>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bind).also {
        it.section().applyExtra { liveItems = item }
    }
}

@LazyRecyclerMarker
@JvmName("viewBindingFlowItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.items(
    items: Flow<List<I>>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bind).also {
        it.section().applyExtra { liveItems = items }
    }
}

@LazyRecyclerMarker
inline fun <reified V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    items: Flow<List<I>>,
    sectionId: Int = -1,
    noinline bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return itemsIndexed(emptyList(), sectionId, bind).also {
        it.section().applyExtra { liveItems = items }
    }
}

@LazyRecyclerMarker
@JvmName("viewRequiredFlowItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    item: Flow<I>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bindScope).also {
        it.section().applyExtra { liveItems = item }
    }
}

@LazyRecyclerMarker
@JvmName("viewRequiredFlowItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    items: Flow<List<I>>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bindScope).also {
        it.section().applyExtra { liveItems = items }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    template: Template<I>,
    item: Flow<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    return items(template, emptyList(), sectionId).also {
        it.section().applyExtra { liveItems = item }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    template: Template<I>,
    items: Flow<List<I>>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    return items(template, emptyList(), sectionId).also {
        it.section().applyExtra { liveItems = items }
    }
}
