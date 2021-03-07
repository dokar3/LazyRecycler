package com.dokar.lazyrecycler.rxjava3

import com.dokar.lazyrecycler.Section
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

typealias ShowWhile = () -> Observable<Boolean>

inline fun Section<*, *>.applyExtra(block: SectionExtra.() -> Unit) {
    val extra = findExtra(SectionExtra::class.java) ?: SectionExtra().also(::putExtra)
    block(extra)
}

class SectionExtra {
    var showWhile: ShowWhile? = null
    var showIfObserver: Disposable? = null
    var liveItems: Observable<*>? = null
    var liveItemsObserver: Disposable? = null
}