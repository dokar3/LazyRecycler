package com.dokar.lazyrecyclersample.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.LazyRecycler
import com.dokar.lazyrecycler.Template
import com.dokar.lazyrecycler.differCallback
import com.dokar.lazyrecycler.flow.toMutableValue
import com.dokar.lazyrecycler.item
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import com.dokar.lazyrecycler.template
import com.dokar.lazyrecyclersample.Constants
import com.dokar.lazyrecyclersample.Constants.ID_PAINTINGS
import com.dokar.lazyrecyclersample.Constants.OPTIONS
import com.dokar.lazyrecyclersample.Constants.VINCENT_PAINTINGS
import com.dokar.lazyrecyclersample.Option
import com.dokar.lazyrecyclersample.Painting
import com.dokar.lazyrecyclersample.databinding.ItemGalleryHeaderBinding
import com.dokar.lazyrecyclersample.databinding.ItemOptionBinding
import com.dokar.lazyrecyclersample.databinding.ItemPaintingDataBinding
import com.dokar.lazyrecyclersample.databinding.ItemSectionTitleBinding
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow

class GalleryActivity : AppCompatActivity() {
    private lateinit var lazyRecycler: LazyRecycler

    private lateinit var titleTemplate: Template<String>
    private lateinit var paintingTemplate: Template<Painting>

    private val paintings = MutableStateFlow(VINCENT_PAINTINGS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rv = RecyclerView(this)

        createList(rv)

        setContentView(rv)
    }

    @SuppressLint("SetTextI18n")
    private fun createList(rv: RecyclerView) {
        titleTemplate = template(ItemSectionTitleBinding::inflate) { binding, title ->
            binding.tvSectionTitle.text = title
        }

        paintingTemplate = template(ItemPaintingDataBinding::inflate) { binding, item ->
            binding.painting = item
        }

        lazyRecycler(rv, spanCount = 6) {
            item(
                layout = ItemGalleryHeaderBinding::inflate,
                span = 6,
            ) { binding ->
                binding.title.text = "Vincent Gallery"
            }

            items(
                items = OPTIONS,
                layout = ItemOptionBinding::inflate,
                span = { 2 },
                clicks = { _, item -> onOptionItemClicked(item) },
            ) { binding, opt ->
                binding.title.text = opt.text
            }

            items(
                data = paintings.toMutableValue(lifecycleScope),
                template = paintingTemplate,
                id = ID_PAINTINGS,
                clicks = { _, item -> openUrl(item.url) },
                span = { position -> if (position % 3 == 0) 6 else 3 },
                diffCallback = differCallback {
                    areItemsTheSame { oldItem, newItem ->
                        oldItem.id == newItem.id
                    }
                    areContentsTheSame { oldItem, newItem ->
                        oldItem.title == newItem.title
                    }
                },
            )
        }.let {
            lazyRecycler = it
        }
    }

    private fun onOptionItemClicked(item: Option) {
        when (item.id) {
            Constants.OPT_SHUFFLE -> {
                paintings.value = VINCENT_PAINTINGS.shuffled()
            }
            Constants.OPT_REMOVE_SECTION -> {
                lazyRecycler.removeSection(ID_PAINTINGS)
            }
            Constants.OPT_NEW_SECTIONS -> {
                newSections()
            }
        }
    }

    private fun newSections() {
        val newItems = VINCENT_PAINTINGS.shuffled().subList(0, Random.nextInt(2, 4))
        lazyRecycler.newSections(index = 2) {
            item(
                item = "Dynamic section",
                template = titleTemplate,
                span = 6,
            )
            items(
                items = newItems,
                template = paintingTemplate,
                span = { 6 },
            )
        }
    }

    private fun openUrl(url: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            startActivity(this)
        }
    }
}
