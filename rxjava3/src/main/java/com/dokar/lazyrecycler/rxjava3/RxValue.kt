package com.dokar.lazyrecycler.rxjava3

import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.ValueObserver
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Observable<List<I>> to mutable data source
 * */
fun <I> Observable<List<I>>.asMutSource(): MutableValue<List<I>> {
    return RxValue(MutableValue.DATA_SOURCE, this)
}

/**
 * Observable<I> to mutable data source
 * */
@JvmName("singleMutableSource")
fun <I> Observable<I>.asMutSource(): MutableValue<List<I>> {
    val source = this.map {
        listOf(it)
    }
    return RxValue(MutableValue.DATA_SOURCE, source)
}

/**
 * Observable<T> to mutable property
 * */
fun <T> Observable<T>.asMutProperty(): MutableValue<T> {
    return RxValue(MutableValue.PROPERTY, this)
}

class RxValue<T>(
    type: Int,
    private val value: Observable<T>
) : MutableValue<T>(type) {

    private var disposable: Disposable? = null

    override fun observe(listener: ValueObserver<T>) {
        super.observe(listener)
        disposable = value.observe {
            current = it
        }
    }

    override fun unobserve() {
        super.unobserve()
        disposable?.dispose()
    }
}
