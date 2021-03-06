package com.dokar.lazyrecycler.livedata

import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.SectionConfigurator
import com.dokar.lazyrecycler.RecyclerBuilder
import com.dokar.lazyrecycler.*
import com.dokar.lazyrecycler.viewbinder.IndexedViewBindingBind
import com.dokar.lazyrecycler.viewbinder.ViewBindingBind
import com.dokar.lazyrecycler.viewcreator.BindScope
import com.dokar.lazyrecycler.viewcreator.ViewRequiredBindScope
import kotlin.experimental.ExperimentalTypeInference

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    layoutId: Int,
    item: LiveData<I>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    val currItems: List<I> = if (item.value != null) {
        listOf(item.value!!)
    } else {
        emptyList()
    }
    return items(layoutId, currItems, sectionId, bindScope).also {
        it.section().computeExtra { extra -> extra.liveItems = item }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    layoutId: Int,
    items: LiveData<List<I>>,
    sectionId: Int = -1,
    bindScope: BindScope<I>
): SectionConfigurator<I> {
    val currList = items.value ?: emptyList()
    return items(layoutId, currList, sectionId, bindScope).also {
        it.section().computeExtra { extra -> extra.liveItems = items }
    }
}

@LazyRecyclerMarker
@JvmName("viewBindingLiveDataItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.item(
    item: LiveData<I>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    val currItems: List<I> = if (item.value != null) {
        listOf(item.value!!)
    } else {
        emptyList()
    }
    return items(currItems, sectionId, bind).also {
        it.section().computeExtra { extra -> extra.liveItems = item }
    }
}

@LazyRecyclerMarker
@JvmName("viewBindingLiveDataItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
inline fun <reified V : ViewBinding, I> RecyclerBuilder.items(
    items: LiveData<List<I>>,
    sectionId: Int = -1,
    noinline bind: ViewBindingBind<V, I>
): SectionConfigurator<I> {
    val currList = items.value ?: emptyList()
    return items(currList, sectionId, bind).also {
        it.section().computeExtra { extra -> extra.liveItems = items }
    }
}

@LazyRecyclerMarker
inline fun <reified V : ViewBinding, I> RecyclerBuilder.itemsIndexed(
    items: LiveData<List<I>>,
    sectionId: Int = -1,
    noinline bind: IndexedViewBindingBind<V, I>
): SectionConfigurator<I> {
    val currList = items.value ?: emptyList()
    return itemsIndexed(currList, sectionId, bind).also {
        it.section().computeExtra { extra -> extra.liveItems = items }
    }
}

@LazyRecyclerMarker
@JvmName("viewRequiredLiveDataItem")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.item(
    item: LiveData<I>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bindScope).also {
        it.section().computeExtra { extra -> extra.liveItems = item }
    }
}

@LazyRecyclerMarker
@JvmName("viewRequiredLiveDataItems")
@OverloadResolutionByLambdaReturnType
@OptIn(ExperimentalTypeInference::class)
fun <I> RecyclerBuilder.items(
    items: LiveData<List<I>>,
    sectionId: Int = -1,
    bindScope: ViewRequiredBindScope<I>
): SectionConfigurator<I> {
    return items(emptyList(), sectionId, bindScope).also {
        it.section().computeExtra { extra -> extra.liveItems = items }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.item(
    template: Template<I>,
    item: LiveData<I>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    val currItems: List<I> = if (item.value != null) {
        listOf(item.value!!)
    } else {
        emptyList()
    }
    return items(template, currItems, sectionId).also {
        it.section().computeExtra { extra -> extra.liveItems = item }
    }
}

@LazyRecyclerMarker
fun <I> RecyclerBuilder.items(
    template: Template<I>,
    items: LiveData<List<I>>,
    sectionId: Int = -1
): SectionConfigurator<I> {
    val currList = items.value ?: emptyList()
    return items(template, currList, sectionId).also {
        it.section().computeExtra { extra -> extra.liveItems = items }
    }
}