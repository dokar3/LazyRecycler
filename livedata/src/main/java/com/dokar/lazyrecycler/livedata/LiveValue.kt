package com.dokar.lazyrecycler.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.dokar.lazyrecycler.data.MutableValue

/**
 * LiveData<List<I>> to mutable value.
 */
fun <I> LiveData<I>.toMutableValue(
    lifecycleOwner: LifecycleOwner
): MutableValue<I> {
    return LiveValue(this, lifecycleOwner)
}

class LiveValue<T>(
    private val value: LiveData<T>,
    private val lifecycleOwner: LifecycleOwner
) : MutableValue<T>() {

    private var observer = Observer<T> { t ->
        current = t
    }

    init {
        current = value.value
    }

    override fun requestObserve() {
        value.observe(lifecycleOwner, observer)
    }

    override fun requestUnobserve() {
        value.removeObserver(observer)
    }
}
