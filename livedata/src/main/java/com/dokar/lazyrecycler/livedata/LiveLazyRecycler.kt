package com.dokar.lazyrecycler.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.dokar.lazyrecycler.LazyRecycler
import com.dokar.lazyrecycler.LazyRecyclerMarker
import com.dokar.lazyrecycler.Section

fun LazyRecycler.observeChanges(lifecycleOwner: LifecycleOwner) {
    withLiveData(lifecycleOwner, true)
}

fun LazyRecycler.stopObserving(lifecycleOwner: LifecycleOwner) {
    withLiveData(lifecycleOwner, false)
}

@Suppress("DEPRECATION")
private fun LazyRecycler.withLiveData(lifecycleOwner: LifecycleOwner, live: Boolean) {
    val allExtras: MutableList<SectionExtra> = mutableListOf()
    val sectionsWithExtras = sections.filter { section ->
        val extra = section.findExtra(SectionExtra::class.java)
        if (extra != null) {
            allExtras.add(extra)
            true
        } else {
            false
        }
    }
    for (i in sectionsWithExtras.indices) {
        val section = sectionsWithExtras[i]
        val extra = allExtras[i]

        if (live) {
            registerObservers(lifecycleOwner, this, section, extra)
        } else {
            removeObservers(extra)
        }
    }
}

private fun registerObservers(
    lifecycleOwner: LifecycleOwner,
    recycler: LazyRecycler,
    section: Section<Any, Any>,
    extra: SectionExtra
) {
    val showIf = extra.showWhile?.invoke()
    if (showIf != null && extra.showIfObserver == null) {
        val observer = Observer<Boolean> { t ->
            if (t != null) {
                recycler.setSectionVisible(section, t)
            }
        }
        showIf.observe(lifecycleOwner, observer)
        extra.showIfObserver = observer
    }

    val liveItems = extra.liveItems
    if (liveItems != null && extra.liveItemsObserver == null) {
        val observer = Observer<Any> { t ->
            if (t is List<*>) {
                @Suppress("UNCHECKED_CAST")
                recycler.updateSection(section, t as List<Any>)
            } else {
                recycler.updateSection(section, listOfNotNull(t))
            }
        }
        liveItems.observe(lifecycleOwner, observer)
        extra.liveItemsObserver = observer
    }
}

private fun removeObservers(
    extra: SectionExtra
) {
    val showIf = extra.showWhile?.invoke()
    val showIfObserver = extra.showIfObserver
    if (showIf != null && showIfObserver != null) {
        showIf.removeObserver(showIfObserver)
        extra.showIfObserver = null
    }

    val liveItems = extra.liveItems
    val liveItemsObserver = extra.liveItemsObserver
    if (liveItems != null && liveItemsObserver != null) {
        liveItems.removeObserver(liveItemsObserver)
        extra.liveItemsObserver = null
    }
}