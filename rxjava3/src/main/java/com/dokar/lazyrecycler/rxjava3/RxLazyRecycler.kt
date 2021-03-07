package com.dokar.lazyrecycler.rxjava3

import com.dokar.lazyrecycler.LazyRecycler
import com.dokar.lazyrecycler.LazyRecyclerMarker
import com.dokar.lazyrecycler.Section

fun LazyRecycler.observeChanges() {
    withRxJava(true)
}

fun LazyRecycler.stopObserving() {
    withRxJava(false)
}

@Suppress("DEPRECATION")
private fun LazyRecycler.withRxJava(live: Boolean) {
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
            registerObservers(this, section, extra)
        } else {
            removeObservers(extra)
        }
    }
}

private fun registerObservers(
    recycler: LazyRecycler,
    section: Section<Any, Any>,
    extra: SectionExtra
) {
    val showIf = extra.showWhile?.invoke()
    if (showIf != null && extra.showIfObserver == null) {
        showIf.observe { visible ->
            recycler.setSectionVisible(section, visible)
        }.also {
            extra.showIfObserver = it
        }
    }

    val liveItems = extra.liveItems
    if (liveItems != null && extra.liveItemsObserver == null) {
        liveItems.observe { newValue ->
            if (newValue is List<*>) {
                @Suppress("UNCHECKED_CAST")
                recycler.updateSection(section, newValue as List<Any>)
            } else {
                recycler.updateSection(section, listOf(newValue))
            }
        }.also {
            extra.liveItemsObserver = it
        }
    }
}

private fun removeObservers(extra: SectionExtra) {
    extra.showIfObserver?.dispose()
    extra.showIfObserver = null
    extra.liveItemsObserver?.dispose()
    extra.liveItemsObserver = null
}