package com.dokar.lazyrecyclersample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dokar.lazyrecyclersample.chat.ChatActivity
import com.dokar.lazyrecyclersample.databinding.ActivityMainBinding
import com.dokar.lazyrecyclersample.gallery.GalleryActivity
import com.dokar.lazyrecyclersample.sticky.StickyHeaderActivity
import com.dokar.lazyrecyclersample.tetris.TetrisActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGalleryDemo.setOnClickListener {
            go(GalleryActivity::class.java)
        }
        binding.btnChatDemo.setOnClickListener {
            go(ChatActivity::class.java)
        }
        binding.btnTetrisDemo.setOnClickListener {
            go(TetrisActivity::class.java)
        }
        binding.btnStickyHeaderDemo.setOnClickListener {
            go(StickyHeaderActivity::class.java)
        }
    }

    private fun <T : Activity> go(clz: Class<T>) {
        Intent(this, clz).apply {
            startActivity(this)
        }
    }
}
