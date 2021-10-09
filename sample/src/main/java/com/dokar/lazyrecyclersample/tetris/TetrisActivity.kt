package com.dokar.lazyrecyclersample.tetris

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dokar.lazyrecycler.SectionConfig
import com.dokar.lazyrecycler.differ
import com.dokar.lazyrecycler.flow.asMutSource
import com.dokar.lazyrecycler.items
import com.dokar.lazyrecycler.lazyRecycler
import com.dokar.lazyrecycler.spanSize
import com.dokar.lazyrecyclersample.R
import com.dokar.lazyrecyclersample.databinding.ActivityTetrisBinding
import com.dokar.lazyrecyclersample.databinding.ItemTetrisBlockBinding
import com.dokar.lazyrecyclersample.databinding.ItemTetrisScoreBinding
import com.dokar.lazyrecyclersample.tetris.control.SwipeGameController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max

class TetrisActivity : AppCompatActivity() {

    private val cols = 10
    private val rows = 20

    private val score = MutableStateFlow(intArrayOf(0, 0))
    private val matrix = MutableStateFlow(List(rows * cols) { false })

    private var blockColor: Int = 0
    private var fillColor: Int = 0

    private lateinit var binding: ActivityTetrisBinding

    private lateinit var tetrisGame: TetrisGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTetrisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blockColor = ContextCompat.getColor(this, R.color.tetris_block)
        fillColor = ContextCompat.getColor(this, R.color.tetris_block_active)

        lifecycleScope.launch(Dispatchers.Default) {
            createCanvas()

            initGame()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::tetrisGame.isInitialized) {
            tetrisGame.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::tetrisGame.isInitialized) {
            tetrisGame.resume()
        }
    }

    private suspend fun createCanvas() = withContext(Dispatchers.Default) {
        lazyRecycler(spanCount = cols) {
            items(
                data = score.asMutSource(lifecycleScope),
                layout = ItemTetrisScoreBinding::inflate,
                config = SectionConfig<IntArray>().spanSize { cols }
            ) { binding, score ->
                binding.tvScore.text = String.format("%03d", score[0])
                binding.tvHighestScore.text = score[1].toString()
            }

            items(
                data = matrix.asMutSource(lifecycleScope),
                layout = ItemTetrisBlockBinding::inflate,
                config = SectionConfig<Boolean>()
                    .spanSize { 1 }
                    .differ {
                        areItemsTheSame { _, _ -> true }
                        areContentsTheSame { oldItem, newItem -> oldItem == newItem }
                    }
            ) { binding, fillBlock ->
                val color = if (fillBlock) fillColor else blockColor
                binding.block.setBackgroundColor(color)
            }
        }.let { recycler ->
            withContext(Dispatchers.Main.immediate) {
                val rv = binding.rvTetris
                recycler.attachTo(rv)
                rv.overScrollMode = View.OVER_SCROLL_NEVER
                (rv.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
                rv.visibility = View.INVISIBLE

                resizeGameCanvas {
                    rv.visibility = View.VISIBLE
                    tetrisGame.start()
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private suspend fun initGame() {
        tetrisGame = TetrisGame(cols, rows, lifecycleScope)
        tetrisGame.doOnTick { m ->
            matrix.value = m.toList()
        }
        tetrisGame.doOnRemove {
            updateScore(it)
        }
        tetrisGame.doOnGameOver {
            if (tetrisGame.getScore() > score.value[1]) {
                updateHighestScore(tetrisGame.getScore())
            }
            AlertDialog.Builder(this)
                .setTitle("GameOver!")
                .setPositiveButton("I know") { _, _ ->
                    updateScore(0)
                    lifecycleScope.launch {
                        tetrisGame.start()
                    }
                }
                .show()
        }

        withContext(Dispatchers.Main) {
            val swipeGameController = SwipeGameController(this@TetrisActivity)
            swipeGameController.setEventHandler(tetrisGame)
            binding.rvTetris.setOnTouchListener { v, event ->
                swipeGameController.onTouch(v, event)
            }
        }
    }

    private inline fun resizeGameCanvas(crossinline onDone: () -> Unit) {
        binding.rvTetris.post {
            val gameHeader = binding.rvTetris.findViewHolderForAdapterPosition(0)!!.itemView
            gameHeader.post {
                val res = resources
                val paddingTop = res.getDimensionPixelSize(R.dimen.tetris_padding_top)
                val paddingBottom = res.getDimensionPixelSize(R.dimen.tetris_padding_bottom)
                val rootHeight = binding.root.height
                val rootWidth = binding.root.width
                val headerHeight = gameHeader.height
                val verticalRestSpace = rootHeight - headerHeight - paddingTop - paddingBottom
                val blockSize = verticalRestSpace / rows
                val hPadding = max(0, (rootWidth - blockSize * cols) / 2)
                binding.rvTetris.setPadding(hPadding, paddingTop, hPadding, paddingBottom)
                onDone()
            }
        }
    }

    private fun updateScore(value: Int) {
        val s = score.value
        score.value = intArrayOf(value, s[1])
    }

    private fun updateHighestScore(value: Int) {
        val s = score.value
        score.value = intArrayOf(s[0], value)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::tetrisGame.isInitialized && tetrisGame.isRunning()) {
            tetrisGame.finish()
        }
    }
}
