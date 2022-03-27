package com.dokar.lazyrecyclersample.tetris.shape

abstract class Shape {
    protected var currentRotation: Int = 0
        private set

    open fun getShape(): Array<CharArray> {
        return checkNotNull(transformations[currentRotation])
    }

    open fun rotate() {
        currentRotation = nextRotation()
    }

    open fun tryRotate(): Array<CharArray> {
        return checkNotNull(transformations[nextRotation()])
    }

    open fun getRotationOffset(): IntArray? {
        return null
    }

    open fun reset() {
        currentRotation = 0
    }

    private fun nextRotation(): Int {
        val size = transformations.size
        return when {
            currentRotation == 0 && size > 1 -> 90
            currentRotation == 90 && size > 2 -> 180
            currentRotation == 180 && size > 3 -> 270
            else -> 0
        }
    }

    abstract val transformations: Map<Int, Array<CharArray>>
}
