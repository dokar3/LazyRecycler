package com.dokar.lazyrecycler.data

abstract class MutableValue<I> {
    internal var tag: Any? = null

    internal var valueObserver: ValueObserver<I>? = null

    open var current: I? = null
        set(value) {
            if (field != value) {
                field = value
                valueObserver?.onChanged(this)
            }
        }

    internal fun observe(listener: ValueObserver<I>) {
        valueObserver = listener
        requestObserve()
    }

    internal fun unobserve() {
        valueObserver = null
        requestUnobserve()
    }

    abstract fun requestObserve()

    abstract fun requestUnobserve()
}
