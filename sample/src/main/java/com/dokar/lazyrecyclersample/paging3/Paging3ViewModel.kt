package com.dokar.lazyrecyclersample.paging3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import java.lang.StringBuilder
import kotlin.math.min
import kotlin.random.Random
import kotlinx.coroutines.delay

class Paging3ViewModel : ViewModel() {
    val flow = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = ::newPagingSource,
    ).flow.cachedIn(viewModelScope)

    private fun newPagingSource() = object : PagingSource<Int, Post>() {
        private var offset = 0

        override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
            // Try to find the page key of the closest page to anchorPosition from
            // either the prevKey or the nextKey; you need to handle nullability
            // here.
            //  * prevKey == null -> anchorPage is the first page.
            //  * nextKey == null -> anchorPage is the last page.
            //  * both prevKey and nextKey are null -> anchorPage is the
            //    initial page, so return null.
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
            delay(1000L)

            val page = params.key ?: 0
            val list = List(params.loadSize) { randomPost(index = offset + it + 1) }
            offset += list.size

            return LoadResult.Page(
                data = list,
                prevKey = if (page <= 0) null else page - 1,
                nextKey = if (page == MAX_PAGES - 1) null else page + 1,
            )
        }
    }

    private fun randomPost(index: Int): Post = Post(
        id = index,
        title = randomString(minLen = 10, maxLen = 50),
        author = randomString(minLen = 5, maxLen = 20),
        summary = randomString(minLen = 20, maxLen = 100),
        thumbnail = "https://picsum.photos/seed/$index/200/150"
    )

    private fun randomString(minLen: Int, maxLen: Int): String {
        require(minLen > 0)
        require(maxLen >= minLen)
        val builder = StringBuilder()
        val len = Random.nextInt(minLen, maxLen)
        var i = 0
        while (i < len) {
            val wordLen = min(Random.nextInt(2, 10), len - i)
            repeat(wordLen) {
                builder.append(('a' + Random.nextInt(26)))
            }
            i += wordLen
            if (i < len - 1) {
                builder.append(' ')
                i++
            }
        }
        return builder.toString().replaceFirstChar { it.uppercase() }
    }

    companion object {
        private const val PAGE_SIZE = 10
        private const val MAX_PAGES = 5
    }
}