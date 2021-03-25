package com.dokar.lazyrecyclersample.gallery

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.dokar.lazyrecycler.LazyRecycler
import com.dokar.lazyrecycler.Template
import com.dokar.lazyrecycler.flow.asMutProperty
import com.dokar.lazyrecycler.flow.asMutSource
import com.dokar.lazyrecycler.item
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.template
import com.dokar.lazyrecyclersample.Constants
import com.dokar.lazyrecyclersample.Constants.ID_PAINTINGS
import com.dokar.lazyrecyclersample.Constants.OPTIONS
import com.dokar.lazyrecyclersample.Constants.VINCENT_PAINTINGS
import com.dokar.lazyrecyclersample.Option
import com.dokar.lazyrecyclersample.Painting
import com.dokar.lazyrecyclersample.databinding.ItemGalleryHeaderBinding
import com.dokar.lazyrecyclersample.databinding.ItemOptionBinding
import com.dokar.lazyrecyclersample.databinding.ItemPaintingBinding
import com.dokar.lazyrecyclersample.databinding.ItemSectionTitleBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

class GalleryActivity : AppCompatActivity() {

    private lateinit var lazyRecycler: LazyRecycler

    private lateinit var titleTemplate: Template<String>
    private lateinit var paintingTemplate: Template<Painting>

    private val paintings = MutableStateFlow(VINCENT_PAINTINGS)
    private val paintingsVisible = MutableStateFlow(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rv = RecyclerView(this)

        createList(rv)

        setContentView(rv)
    }

    @SuppressLint("SetTextI18n")
    private fun createList(rv: RecyclerView) {
        titleTemplate = template { binding: ItemSectionTitleBinding, title ->
            binding.tvSectionTitle.text = title
        }

        paintingTemplate = template { binding: ItemPaintingBinding, item ->
            binding.title.text = item.title
            binding.summary.text = item.year.toString()
            binding.image.load(item.thumbnail)
        }

        LazyRecycler(rv, spanCount = 6) {
            item(Unit) { binding: ItemGalleryHeaderBinding, _ ->
                binding.title.text = "Vincent Gallery"
            }.spanSize {
                6
            }

            items(OPTIONS) { binding: ItemOptionBinding, opt ->
                binding.title.text = opt.text
            }.spanSize {
                2
            }.clicks { _, item ->
                onOptionItemClicked(item)
            }

            items(
                paintingTemplate,
                paintings.asMutSource(lifecycleScope),
                ID_PAINTINGS
            ).clicks { _, item ->
                openUrl(item.url)
            }.spanSize { position ->
                if (position % 3 == 0) 6 else 3
            }.differ {
                areItemsTheSame { oldItem, newItem ->
                    oldItem.id == newItem.id
                }
                areContentsTheSame { oldItem, newItem ->
                    oldItem.title == newItem.title
                }
            }.showWhile {
                paintingsVisible.asMutProperty(lifecycleScope)
            }
        }.let {
            lazyRecycler = it
        }
    }

    private fun onOptionItemClicked(item: Option) {
        when (item.id) {
            Constants.OPT_SHOW -> {
                paintingsVisible.value = true
            }
            Constants.OPT_HIDE -> {
                paintingsVisible.value = false
            }
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
            item(titleTemplate, "Dynamic section").spanSize { 6 }
            items(paintingTemplate, newItems).spanSize { 6 }
        }
    }

    private fun openUrl(url: String) {
        Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            startActivity(this)
        }
    }
}
