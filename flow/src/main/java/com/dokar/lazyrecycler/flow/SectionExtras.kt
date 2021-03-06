package com.dokar.lazyrecycler.flow

import com.dokar.lazyrecycler.Section
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

typealias ShowWhile = () -> Flow<Boolean>

inline fun Section<*, *>.computeExtra(compute: (extra: SectionExtra) -> Unit) {
    val extra = findExtra(SectionExtra::class.java) ?: SectionExtra().also(::putExtra)
    compute(extra)
}

class SectionExtra {
    var showWhile: ShowWhile? = null
    var showIfObserveJob: Job? = null
    var liveItems: Flow<*>? = null
    var liveItemsObserveJob: Job? = null
}