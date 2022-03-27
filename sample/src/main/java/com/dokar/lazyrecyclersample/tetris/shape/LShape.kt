package com.dokar.lazyrecyclersample.tetris.shape

class LShape : Shape() {
    override val transformations: Map<Int, Array<CharArray>>
        get() = TRANSFORMATIONS

    companion object {
        private val TRANSFORMATIONS = mapOf(
            0 to arrayOf(
                charArrayOf('-', ' '),
                charArrayOf('-', ' '),
                charArrayOf('-', '-'),
            ),
            90 to arrayOf(
                charArrayOf('-', '-', '-'),
                charArrayOf('-', ' ', ' '),
            ),
            180 to arrayOf(
                charArrayOf('-', '-'),
                charArrayOf(' ', '-'),
                charArrayOf(' ', '-'),
            ),
            270 to arrayOf(
                charArrayOf(' ', ' ', '-'),
                charArrayOf('-', '-', '-'),
            ),
        )
    }
}
