package com.dokar.lazyrecyclersample.tetris.shape

class SkewShape : Shape() {
    override val transformations: Map<Int, Array<CharArray>>
        get() = TRANSFORMATIONS

    companion object {
        private val TRANSFORMATIONS = mapOf(
            0 to arrayOf(
                charArrayOf(' ', '-', '-'),
                charArrayOf('-', '-', ' '),
            ),
            90 to arrayOf(
                charArrayOf('-', ' '),
                charArrayOf('-', '-'),
                charArrayOf(' ', '-'),
            )
        )
    }
}
