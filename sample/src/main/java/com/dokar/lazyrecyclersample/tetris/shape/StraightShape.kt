package com.dokar.lazyrecyclersample.tetris.shape

class StraightShape : Shape() {
    override val transformations: Map<Int, Array<CharArray>>
        get() = TRANSFORMATIONS

    override fun getRotationOffset(): IntArray {
        return if (currentRotation == 0) {
            intArrayOf(1, 0)
        } else {
            intArrayOf(-1, 0)
        }
    }

    companion object {
        private val TRANSFORMATIONS = mapOf(
            0 to arrayOf(
                charArrayOf(
                    '-', '-', '-', '-'
                )
            ),
            90 to arrayOf(
                charArrayOf('-'),
                charArrayOf('-'),
                charArrayOf('-'),
                charArrayOf('-'),
            )
        )
    }
}
