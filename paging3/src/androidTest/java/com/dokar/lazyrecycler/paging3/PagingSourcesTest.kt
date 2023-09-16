package com.dokar.lazyrecycler.paging3

import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import androidx.recyclerview.widget.DiffUtil
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

private const val PAGE_SIZE = 10
private const val INITIAL_LOAD_SIZE = 10

@RunWith(AndroidJUnit4::class)
class PagingSourcesTest {
    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean =
            oldItem == newItem
    }

    @Test
    fun test_load_next() = runTest {
        val flow = newPagingDataFlow()

        val pagingValue = PagingValue(
            scope = this,
            flow = flow,
            diffCallback = diffCallback,
        )
        val recycler = lazyRecycler {
            items(
                data = pagingValue,
                layout = 0,
            ) {}
        }

        recycler.observeChanges()

        pagingValue.differ.loadStateFlow
            .filter { it.refresh is LoadState.NotLoading }
            .drop(1)
            .first()

        // Assert the first load
        assertEquals(
            List(INITIAL_LOAD_SIZE) { it },
            recycler.getSectionItems(0),
        )

        // Trigger the next page load
        flow.asSnapshot {
            appendScrollWhile { item -> item == INITIAL_LOAD_SIZE - 1 }
        }

        // Assert the next page load
        assertEquals(
            List(INITIAL_LOAD_SIZE + PAGE_SIZE) { it },
            recycler.getSectionItems(0),
        )

        // Cancel all observing jobs
        recycler.stopObserving()
    }

    private fun newPagingDataFlow(): Flow<PagingData<Int>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = INITIAL_LOAD_SIZE,
            ),
            pagingSourceFactory = ::newPagingSource,
        ).flow
    }

    private fun newPagingSource() = object : PagingSource<Int, Int>() {
        private var offset = 0

        override fun getRefreshKey(state: PagingState<Int, Int>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Int> {
            val page = params.key ?: 0
            val loadSize = params.loadSize
            val list = List(loadSize) { offset + it }
            offset += list.size
            return LoadResult.Page(
                data = list,
                prevKey = if (page > 0) page - 1 else null,
                nextKey = page + 1,
            )
        }
    }
}
