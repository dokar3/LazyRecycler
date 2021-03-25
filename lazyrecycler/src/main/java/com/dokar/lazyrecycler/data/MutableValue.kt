package com.dokar.lazyrecycler.data

abstract class MutableValue<I>(val type: Int) {

    private var valueObserver: ValueObserver<I>? = null

    var name: String? = null

    open var current: I? = null
        set(value) {
            field = value
            onValueChanged()
        }

    private fun onValueChanged() {
        valueObserver?.onChanged(this)
    }

    open fun observe(listener: ValueObserver<I>) {
        valueObserver = listener
    }

    open fun unobserve() {
        valueObserver = null
    }

    companion object {

        const val NONE = -1

        const val DATA_SOURCE = 0

        const val PROPERTY = 1
    }
}
