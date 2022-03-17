package com.dokar.lazyrecycler.flow

import com.dokar.lazyrecycler.data.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


/**
 * Flow<I> to mutable value.
 */
fun <I> Flow<I>.toMutableValue(
    scope: CoroutineScope
): MutableValue<I> {
    return FlowValue(this, scope)
}

class FlowValue<T>(
    private val value: Flow<T>,
    private val scope: CoroutineScope
) : MutableValue<T>() {
    private var job: Job? = null

    override fun requestObserve() {
        job = scope.launch {
            value.collect {
                current = it
            }
        }
    }

    override fun requestUnobserve() {
        job?.cancel()
    }
}
