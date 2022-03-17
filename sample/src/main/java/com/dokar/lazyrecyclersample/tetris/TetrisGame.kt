package com.dokar.lazyrecyclersample.tetris

import com.dokar.lazyrecyclersample.tetris.control.ControlEventHandler
import com.dokar.lazyrecyclersample.tetris.control.GameController.Companion.DOWN_KEY_DOWN
import com.dokar.lazyrecyclersample.tetris.control.GameController.Companion.DOWN_KEY_UP
import com.dokar.lazyrecyclersample.tetris.control.GameController.Companion.LEFT_KEY_UP
import com.dokar.lazyrecyclersample.tetris.control.GameController.Companion.RIGHT_KEY_UP
import com.dokar.lazyrecyclersample.tetris.control.GameController.Companion.SINGLE_TAP_UP
import com.dokar.lazyrecyclersample.tetris.shape.LShape
import com.dokar.lazyrecyclersample.tetris.shape.Shape
import com.dokar.lazyrecyclersample.tetris.shape.SkewShape
import com.dokar.lazyrecyclersample.tetris.shape.SquareShape
import com.dokar.lazyrecyclersample.tetris.shape.StraightShape
import com.dokar.lazyrecyclersample.tetris.shape.TShape
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays
import kotlin.math.max
import kotlin.math.min

class TetrisGame(
    private val cols: Int,
    private val rows: Int,
    private val scope: CoroutineScope
) : ControlEventHandler {
    private val matrix: BooleanArray = BooleanArray(cols * rows) { false }
    private val activeMatrix: BooleanArray = BooleanArray(cols * rows) { false }

    private lateinit var currShape: Shape

    private var isRunning = false

    private var isPaused = false

    private var score = 0

    private var pauseChannel: Channel<Unit> = Channel(0)

    private val tickInterval = 500L
    private val initialSpeed = 1.0f
    private val maxExtraSpeed = 8.0f

    private var shapeLeft: Int = 0
    private var shapeBottom: Int = 0

    private var currSpeed = initialSpeed
    private var extraSpeed = 0.0f

    private var tickJob: Job? = null
    private var speedUpFallingJob: Job? = null

    private var onGameOver: (() -> Unit)? = null
    private var onRemove: ((score: Int) -> Unit)? = null
    private var onTick: ((matrix: BooleanArray) -> Unit)? = null

    fun start() {
        if (isRunning) {
            finish()
        }
        scope.launch {
            startInternal()
        }
    }

    fun finish() {
        isRunning = false
        pauseChannel.trySend(Unit)
        tickJob?.cancel()
        speedUpFallingJob?.cancel()
    }

    fun pause() {
        if (!isRunning) {
            return
        }
        isPaused = true
    }

    fun resume() {
        if (!isRunning) {
            return
        }
        isPaused = false
        pauseChannel.trySend(Unit)
    }

    fun doOnTick(onTick: (matrix: BooleanArray) -> Unit) {
        this.onTick = onTick
    }

    fun doOnRemove(onRemove: (score: Int) -> Unit) {
        this.onRemove = onRemove
    }

    fun doOnGameOver(onGameOver: () -> Unit) {
        this.onGameOver = onGameOver
    }

    fun getScore(): Int {
        return score
    }

    fun isRunning(): Boolean {
        return isRunning
    }

    override fun handleEvent(event: Int) {
        if (!isRunning) {
            return
        }
        when (event) {
            SINGLE_TAP_UP -> {
                rotateInternal()
            }
            LEFT_KEY_UP -> {
                translateInternal(-1)
            }
            RIGHT_KEY_UP -> {
                translateInternal(1)
            }
            DOWN_KEY_DOWN -> {
                extraSpeed = 4.0f
                speedUpFallingJob?.cancel()
                speedUpFallingJob = scope.launch {
                    while (isActive) {
                        extraSpeed += 0.4f
                        extraSpeed = min(maxExtraSpeed, extraSpeed)
                        if (extraSpeed == maxExtraSpeed) {
                            break
                        }
                        delay(50)
                    }
                }
                scope.launch {
                    tickNow(true)
                }
            }
            DOWN_KEY_UP -> {
                speedUpFallingJob?.cancel()
                extraSpeed = 0.0f
            }
        }
    }

    private suspend fun startInternal() = withContext(Dispatchers.Default) {
        Arrays.fill(activeMatrix, false)
        Arrays.fill(matrix, false)
        withContext(Dispatchers.Main) {
            onTick?.invoke(matrix)
        }
        newShape()
        isRunning = true
        isPaused = false
        score = 0
        currSpeed = initialSpeed
        extraSpeed = 0.0f

        tickNow(true)
    }

    private suspend fun tick(fall: Boolean) {
        while (isPaused) {
            pauseChannel.receive()
        }

        if (!isRunning) {
            return
        }

        if (fall) {
            shapeBottom++
        }

        val removedCount = checkRemoval()
        if (removedCount > 0) {
            onRemoveInternal(removedCount)
            if (fall) {
                tickDelay()
            }
            return
        }

        if (checkCollision(currShape.getShape(), shapeBottom, shapeLeft)) {
            if (shapeBottom == 1) {
                onGameOverInternal()
            } else if (fall) {
                newShape()
                tickDelay()
            }
            return
        }

        updateActiveMatrix()

        onTickInternal()

        if (fall && shapeBottom == rows) {
            newShapeDelay()
        }

        if (fall) {
            tickDelay()
        }
    }

    private fun tickDelay() {
        tickJob = scope.launch {
            val speed = currSpeed + extraSpeed
            delay((tickInterval / speed).toLong())
            tick(true)
        }
    }

    private suspend fun tickNow(fall: Boolean) {
        if (fall) {
            tickJob?.cancel()
        }
        tick(fall)
    }

    private fun checkCollision(
        shape: Array<BooleanArray>,
        checkBottom: Int,
        checkLeft: Int
    ): Boolean {
        val shapeWidth = shape[0].size
        val shapeHeight = shape.size

        if (checkLeft < 0 || checkLeft + shapeWidth > cols || checkBottom > rows) {
            return true
        }

        val top = checkBottom - shapeHeight
        val right = checkLeft + shapeWidth
        for ((i, row) in (top until checkBottom).withIndex()) {
            if (row < 0) continue
            for ((j, col) in (checkLeft until right).withIndex()) {
                if (matrix[row * cols + col] && shape[i][j]) {
                    return true
                }
            }
        }

        return false
    }

    private fun checkRemoval(): Int {
        var removedRows = 0
        var row = rows - 1

        while (row >= 0) {
            var canRemove = true
            for (col in (cols - 1) downTo 0) {
                if (!matrix[row * cols + col]) {
                    canRemove = false
                    break
                }
            }

            if (canRemove) {
                for (i in row downTo 0) {
                    var empty = true
                    for (j in 0 until cols) {
                        matrix[i * cols + j] = if (i == 0) {
                            false
                        } else {
                            matrix[(i - 1) * cols + j]
                        }
                        if (empty && matrix[i * cols + j]) {
                            empty = false
                        }
                    }
                    if (empty) {
                        break
                    }
                }
                removedRows++
                row++
            }

            row--
        }

        return removedRows
    }

    private fun updateActiveMatrix() {
        System.arraycopy(matrix, 0, activeMatrix, 0, matrix.size)
        val shape = currShape.getShape()
        val shapeWidth = shape[0].size
        val shapeHeight = shape.size
        val top = shapeBottom - shapeHeight
        val right = shapeLeft + shapeWidth
        for ((i, row) in (top until shapeBottom).withIndex()) {
            if (row < 0) continue
            for ((j, col) in (shapeLeft until right).withIndex()) {
                if (shape[i][j]) {
                    activeMatrix[row * cols + col] = true
                }
            }
        }
    }

    private suspend fun newShapeDelay() {
        delay(300)
        newShape()
    }

    private fun newShape() {
        System.arraycopy(activeMatrix, 0, matrix, 0, matrix.size)
        currShape = SHAPES.random()
        currShape.reset()
        shapeLeft = (cols - currShape.getShape()[0].size) / 2
        shapeBottom = 0
    }

    private fun rotateInternal(): Boolean {
        val rotated = currShape.tryRotate()
        val shapeWidth = rotated[0].size
        val rotationXYOffset = currShape.getRotationXYOffset()
        val offsetX: Int
        val offsetY: Int
        if (rotationXYOffset != null) {
            offsetX = rotationXYOffset[0]
            offsetY = rotationXYOffset[1]
        } else {
            offsetX = 0
            offsetY = 0
        }

        val topLeft = min(cols - shapeWidth, max(0, shapeLeft + offsetX))

        if (checkCollision(rotated, shapeBottom + offsetY, topLeft)) {
            return false
        }

        currShape.rotate()

        shapeLeft = topLeft
        shapeBottom += offsetY

        scope.launch {
            tickNow(false)
        }

        return true
    }

    private fun translateInternal(dx: Int): Boolean {
        val newLeft = shapeLeft + dx
        val shape = currShape.getShape()
        val shapeWidth = shape[0].size
        if (newLeft < 0 || newLeft + shapeWidth > cols) {
            return false
        }

        if (checkCollision(shape, shapeBottom, newLeft)) {
            return false
        }

        shapeLeft = newLeft

        scope.launch {
            tickNow(false)
        }

        return true
    }

    private suspend fun onTickInternal() {
        val onTick = this.onTick
        if (onTick != null) {
            withContext(Dispatchers.Main) {
                onTick(activeMatrix)
            }
        }
    }

    private suspend fun onRemoveInternal(removedRows: Int) {
        System.arraycopy(matrix, 0, activeMatrix, 0, matrix.size)
        score += removedRows * 10 * removedRows

        val extraSpeed = when {
            score < 500 -> 0f
            score < 1000 -> 0.1f
            score < 2000 -> 0.2f
            score < 3000 -> 0.3f
            score < 5000 -> 0.4f
            else -> 0.5f
        }
        if (currSpeed != initialSpeed + extraSpeed) {
            currSpeed = initialSpeed + extraSpeed
        }

        val onRemove = this.onRemove
        if (onRemove != null) {
            withContext(Dispatchers.Main) {
                onRemove(score)
            }
        }

        onTickInternal()
    }

    private suspend fun onGameOverInternal() {
        isRunning = false
        val onGameOver = this.onGameOver
        if (onGameOver != null) {
            withContext(Dispatchers.Main) {
                onGameOver()
            }
        }
    }

    companion object {
        private val SHAPES = arrayOf(
            StraightShape(),
            SquareShape(),
            TShape(),
            LShape(),
            SkewShape()
        )
    }
}
