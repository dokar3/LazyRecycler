package com.dokar.lazyrecyclersample.sticky

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.item
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import com.dokar.lazyrecycler.template
import com.dokar.lazyrecyclersample.R
import com.dokar.lazyrecyclersample.databinding.ItemAlphabetBinding
import com.jay.widget.StickyHeadersLinearLayoutManager

class StickyHeaderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rv = RecyclerView(this)

        setContentView(rv)

        createList(rv)
    }

    private fun createList(rv: RecyclerView) {
        lazyRecycler(
            recyclerView = rv,
            adapterCreator = { sections ->
                StickyHeaderAdapter(sections)
            },
            setupLayoutManager = false
        ) {
            val header = template(ItemAlphabetBinding::inflate) { binding, item: Header ->
                val ctx = this@StickyHeaderActivity
                binding.root.setBackgroundColor(ContextCompat.getColor(ctx, R.color.primary))
                binding.tv.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                binding.tv.text = item.title
            }

            val alphabet = template(ItemAlphabetBinding::inflate) { binding, item: Char ->
                binding.tv.text = item.toString()
            }

            item(Header("A - F"), header)
            items(('A'..'F').toList(), alphabet)

            item(Header("G - M"), header)
            items(('G'..'M').toList(), alphabet)

            item(Header("N - Z"), header)
            items(('N'..'Z').toList(), alphabet)
        }

        val layoutManager = StickyHeadersLinearLayoutManager<StickyHeaderAdapter>(this)
        rv.layoutManager = layoutManager
    }

}