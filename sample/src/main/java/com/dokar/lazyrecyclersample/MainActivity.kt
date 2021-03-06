package com.dokar.lazyrecyclersample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dokar.lazyrecyclersample.chat.ChatActivity
import com.dokar.lazyrecyclersample.databinding.ActivityMainBinding
import com.dokar.lazyrecyclersample.gallery.GalleryActivity
import com.dokar.lazyrecyclersample.tetris.TetrisActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGalleryDemo.setOnClickListener {
            Intent(this, GalleryActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnChatDemo.setOnClickListener {
            Intent(this, ChatActivity::class.java).apply {
                startActivity(this)
            }
        }
        binding.btnTetrisDemo.setOnClickListener {
            Intent(this, TetrisActivity::class.java).apply {
                startActivity(this)
            }
        }
    }


}