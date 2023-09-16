package com.dokar.lazyrecyclersample.paging3

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dokar.lazyrecycler.differCallback
import com.dokar.lazyrecycler.flow.toMutableValue
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import com.dokar.lazyrecycler.paging3.PagingLazyAdapter
import com.dokar.lazyrecycler.paging3.PagingValue
import com.dokar.lazyrecycler.paging3.toPagingValue
import com.dokar.lazyrecyclersample.R
import com.dokar.lazyrecyclersample.databinding.ActivityPaging3Binding
import com.dokar.lazyrecyclersample.databinding.ItemPostBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class Paging3Activity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPaging3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = ViewModelProvider(this)[Paging3ViewModel::class.java]
        val pagingValue = createPagingValue(viewModel.flow)

        setupRecyclerView(
            pagingValue = pagingValue,
            recyclerView = binding.rvPosts
        )

        binding.swipeFreshLayout.setOnRefreshListener(pagingValue.differ::refresh)

        lifecycleScope.launch {
            pagingValue.differ.onPagesUpdatedFlow
                .collect {
                    binding.tvTitle.text =
                        "Posts (${pagingValue.differ.itemCount})"
                }
        }
        lifecycleScope.launch {
            pagingValue.differ.loadStateFlow
                .map { it.refresh is LoadState.Loading || it.prepend is LoadState.Loading }
                .collect { binding.swipeFreshLayout.isRefreshing = it }
        }
    }

    private fun createPagingValue(flow: Flow<PagingData<Post>>): PagingValue<Post> {
        val diffCallback = differCallback<Post> {
            areItemsTheSame { oldItem, newItem -> oldItem.id == newItem.id }
            areContentsTheSame { oldItem, newItem -> oldItem == newItem }
        }
        return flow.toPagingValue(lifecycleScope, diffCallback)
    }

    private fun setupRecyclerView(
        pagingValue: PagingValue<Post>,
        recyclerView: RecyclerView
    ) {
        val footerData = pagingValue.differ.loadStateFlow
            // Emit an empty list to hide the footer
            .map { if (it.append is LoadState.Loading) listOf(Unit) else emptyList() }
            .toMutableValue(lifecycleScope)

        lazyRecycler(
            recyclerView = recyclerView,
            // Use the PagingLazyAdapter to trigger loads
            adapterCreator = ::PagingLazyAdapter,
        ) {
            items(
                data = pagingValue,
                layout = ItemPostBinding::inflate,
                diffCallback = pagingValue.diffCallback,
                clicks = { _, _ -> },
            ) { binding, item ->
                binding.tvTitle.text = item.title
                binding.tvAuthor.text = item.author
                binding.tvSummary.text = item.summary
                binding.ivThumbnail.load(item.thumbnail)
            }

            items(
                data = footerData,
                layout = R.layout.item_loading,
            ) {}
        }
    }
}

