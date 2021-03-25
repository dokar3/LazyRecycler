package com.dokar.lazyrecycler.livedata

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.ValueObserver

/**
 * LiveData<List<I>> to mutable data source
 * */
fun <I> LiveData<List<I>>.asMutSource(
    lifecycleOwner: LifecycleOwner
): MutableValue<List<I>> {
    return LiveValue(MutableValue.DATA_SOURCE, this, lifecycleOwner)
}

/**
 * LiveData<I> to mutable data source
 * */
@JvmName("singleMutableSource")
fun <I> LiveData<I>.asMutSource(
    lifecycleOwner: LifecycleOwner
): MutableValue<List<I>> {
    val source = this.map {
        listOf(it)
    }
    return LiveValue(MutableValue.DATA_SOURCE, source, lifecycleOwner).also {
        val defaultValue = this.value
        it.current = if (defaultValue != null) listOf(defaultValue) else emptyList()
    }
}

/**
 * LiveData<T> to mutable property
 * */
fun <T> LiveData<T>.asMutProperty(
    lifecycleOwner: LifecycleOwner
): LiveValue<T> {
    return LiveValue(MutableValue.PROPERTY, this, lifecycleOwner)
}

class LiveValue<T>(
    type: Int,
    private val value: LiveData<T>,
    private val lifecycleOwner: LifecycleOwner
) : MutableValue<T>(type) {

    private var observer = Observer<T> { t ->
        current = t
    }

    init {
        current = value.value
    }

    override fun observe(listener: ValueObserver<T>) {
        super.observe(listener)
        value.observe(lifecycleOwner, observer)
    }

    override fun unobserve() {
        super.unobserve()
        value.removeObserver(observer)
    }
}
