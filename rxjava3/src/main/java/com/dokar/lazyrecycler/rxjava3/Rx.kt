package com.dokar.lazyrecycler.rxjava3

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableObserver

typealias OnChanged<T> = (newValue: T) -> Unit

fun <T> Observable<T>.observe(
    onChanged: OnChanged<T>
): Disposable {
    val observer = SectionObserver(onChanged)
    return subscribeWith(observer)
}

class SectionObserver<T>(
    private val onChanged: OnChanged<T>
) : DisposableObserver<T>() {

    override fun onNext(t: T) {
        onChanged(t)
    }

    override fun onError(e: Throwable?) {
    }

    override fun onComplete() {
    }
}
