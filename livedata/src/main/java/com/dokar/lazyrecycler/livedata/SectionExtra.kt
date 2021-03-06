package com.dokar.lazyrecycler.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.dokar.lazyrecycler.Section

typealias ShowWhile = (() -> LiveData<Boolean>)

inline fun Section<*, *>.computeExtra(compute: (extra: SectionExtra) -> Unit) {
    val extra = findExtra(SectionExtra::class.java) ?: SectionExtra().also(::putExtra)
    compute(extra)
}

class SectionExtra {
    var showWhile: ShowWhile? = null
    var showIfObserver: Observer<Boolean>? = null
    var liveItems: LiveData<*>? = null
    var liveItemsObserver: Observer<Any>? = null
}