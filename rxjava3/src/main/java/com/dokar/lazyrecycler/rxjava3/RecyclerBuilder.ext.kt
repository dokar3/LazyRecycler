package com.dokar.lazyrecycler.rxjava3

import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.*
import com.dokar.lazyrecycler.SectionConfigurator
import com.dokar.lazyrecycler.RecyclerBuilder
import com.dokar.lazyrecycler.viewbinder.IndexedViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ViewBindingBind
import com.dokar.lazyrecycler.viewcreator.BindScope
import com.dokar.lazyrecycler.viewcreator.ViewRequiredBindScope
import io.reactivex.rxjava3.core.Observable
import kotlin.experimental.ExperimentalTypeInference

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    layoutId: Int,
    item: Observable<I>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(layoutId, emptyList(), sectionId, bindScope).also {
        it.section().computeExtra { extra ->  extra.liveItems = item }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    layoutId: Int,
    items: Observable<List<I>>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    return items(layoutId, emptyList(), sectionId, bindScope).also {
        it.section().computeExtra { extra ->  extra.liveItems = items }
    }
}


@LazyRecyclerMarker
@JvmName("viewBindingObservableItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.item(
    item: Observable<I>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bind).also {
        it.section().computeExtra { extra ->  extra.liveItems = item }
    }
}

@LazyRecyclerMarker
@JvmName("viewBindingObservableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.items(
    items: Observable<List<I>>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bind).also {
        it.section().computeExtra { extra ->  extra.liveItems = items }
    }
}

@LazyRecyclerMarker
inline fun <reified V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    items: Observable<List<I>>,
    sectionId: Int = -1,
    noinline bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    return itemsIndexed(emptyList(), sectionId, bind).also {
        it.section().computeExtra { extra ->  extra.liveItems = items }
    }
}

@LazyRecyclerMarker
@JvmName("viewRequiredObservableItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    item: Observable<I>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bindScope).also {
        it.section().computeExtra { extra ->  extra.liveItems = item }
    }
}

@LazyRecyclerMarker
@JvmName("viewRequiredObservableItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    items: Observable<List<I>>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bindScope).also {
        it.section().computeExtra { extra ->  extra.liveItems = items }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    template: Template<I>,
    item: Observable<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    return items(template, emptyList(), sectionId).also {
        it.section().computeExtra { extra ->  extra.liveItems = item }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    template: Template<I>,
    items: Observable<List<I>>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    return items(template, emptyList(), sectionId).also {
        it.section().computeExtra { extra ->  extra.liveItems = items }
    }
}
