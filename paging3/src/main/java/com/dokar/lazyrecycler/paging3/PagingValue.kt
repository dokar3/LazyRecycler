package com.dokar.lazyrecycler.paging3

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.dokar.lazyrecycler.data.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Convert the [PagingData] flow to [PagingValue].
 */
fun <T : Any> Flow<PagingData<T>>.toPagingValue(
    scope: CoroutineScope,
    diffCallback: DiffUtil.ItemCallback<T>
): PagingValue<T> = PagingValue(
    scope = scope,
    flow = this,
    diffCallback = diffCallback,
)

class PagingValue<T : Any>(
    private val scope: CoroutineScope,
    private val flow: Flow<PagingData<T>>,
    val diffCallback: DiffUtil.ItemCallback<T>,
) : MutableValue<List<T>>() {
    private var jobs: List<Job>? = null

    val differ = AsyncPagingDataDiffer(
        diffCallback = diffCallback,
        updateCallback = NoopDifferUpdateCallback,
    )

    override fun requestObserve() {
        jobs = listOf(
            scope.launch {
                differ.onPagesUpdatedFlow
                    .collect { current = differ.snapshot().items }
            },
            scope.launch {
                flow.collect(differ::submitData)
            },
        )
    }

    override fun requestUnobserve() {
        jobs?.forEach { it.cancel() }
    }

    companion object {
        private val NoopDifferUpdateCallback = object : ListUpdateCallback {
            override fun onInserted(position: Int, count: Int) {}
            override fun onRemoved(position: Int, count: Int) {}
            override fun onMoved(fromPosition: Int, toPosition: Int) {}
            override fun onChanged(position: Int, count: Int, payload: Any?) {}
        }
    }
}
