package com.dokar.lazyrecycler.flow

import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.ValueObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Flow<List<I>> to mutable data source
 * */
fun <I> Flow<List<I>>.asMutSource(
    scope: CoroutineScope
): MutableValue<List<I>> {
    return FlowValue(MutableValue.DATA_SOURCE, this, scope)
}

/**
 * Flow<I> to mutable data source
 * */
@JvmName("singleMutableSource")
fun <I> Flow<I>.asMutSource(
    scope: CoroutineScope
): MutableValue<List<I>> {
    val source = this.map {
        listOf(it)
    }
    return FlowValue(MutableValue.DATA_SOURCE, source, scope)
}

/**
 * Flow<T> to mutable property
 * */
fun <T> Flow<T>.asMutProperty(
    scope: CoroutineScope
): MutableValue<T> {
    return FlowValue(MutableValue.PROPERTY, this, scope)
}

class FlowValue<T>(
    type: Int,
    private val value: Flow<T>,
    private val scope: CoroutineScope
) : MutableValue<T>(type) {

    private var job: Job? = null

    @Suppress("UNCHECKED_CAST")
    override fun observe(listener: ValueObserver<T>) {
        super.observe(listener)
        job = scope.launch {
            value.collect {
                current = it
            }
        }
    }

    override fun unobserve() {
        super.unobserve()
        job?.cancel()
    }
}
