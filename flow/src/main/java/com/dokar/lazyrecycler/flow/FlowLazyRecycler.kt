package com.dokar.lazyrecycler.flow

import com.dokar.lazyrecycler.LazyRecycler
import com.dokar.lazyrecycler.LazyRecyclerMarker
import com.dokar.lazyrecycler.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun LazyRecycler.observeChanges(scope: CoroutineScope) {
    withFlow(scope, true)
}

fun LazyRecycler.stopObserving(scope: CoroutineScope) {
    withFlow(scope, false)
}

@Suppress("DEPRECATION")
private fun LazyRecycler.withFlow(scope: CoroutineScope, live: Boolean) {
    val rxExtras: MutableList<SectionExtra> = mutableListOf()
    val sectionsWithExtra = sections.filter { section ->
        val extra = section.findExtra(SectionExtra::class.java)
        if (extra != null) {
            rxExtras.add(extra)
            true
        } else {
            false
        }
    }
    sectionsWithExtra.forEachIndexed { i, section ->
        val extra = rxExtras[i]
        if (live) {
            registerObservers(scope, this, section, extra)
        } else {
            removeObservers(extra)
        }
    }
}

private fun registerObservers(
    scope: CoroutineScope,
    recycler: LazyRecycler,
    section: Section<Any, Any>,
    extra: SectionExtra
) {
    val showIf = extra.showWhile?.invoke()
    if (showIf != null) {
        scope.launch {
            showIf.collect { visible ->
                recycler.setSectionVisible(section, visible)
            }
        }.also {
            extra.showIfObserveJob = it
        }
    }

    val liveItems = extra.liveItems
    if (liveItems != null) {
        scope.launch {
            liveItems.collect { newValue ->
                if (newValue is List<*>) {
                    @Suppress("UNCHECKED_CAST")
                    recycler.updateSection(section, newValue as List<Any>)
                } else {
                    recycler.updateSection(section, listOfNotNull(newValue))
                }
            }
        }.also {
            extra.liveItemsObserveJob = it
        }
    }
}

private fun removeObservers(extra: SectionExtra) {
    extra.showIfObserveJob?.cancel()
    extra.liveItemsObserveJob?.cancel()
}