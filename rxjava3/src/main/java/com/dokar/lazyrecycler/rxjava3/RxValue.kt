package com.dokar.lazyrecycler.rxjava3

import com.dokar.lazyrecycler.data.MutableValue
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Observable<I> to mutable value.
 */
fun <I : Any> Observable<I>.toMutableValue(): MutableValue<I> {
    return RxValue(this)
}

class RxValue<T : Any>(
    private val value: Observable<T>
) : MutableValue<T>() {
    private var disposable: Disposable? = null

    override fun requestObserve() {
        disposable = value.subscribe(
            {
                current = it
            },
            {
                // Do nothing when error occurred
            }
        )
    }

    override fun requestUnobserve() {
        disposable?.dispose()
    }
}
