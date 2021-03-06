package com.dokar.lazyrecyclersample.tetris.shape

class SquareShape : Shape() {

    override fun getTransforms(): Array<Array<BooleanArray>> {
        return TRANSFORMS
    }

    companion object {
        private val TRANSFORMS = arrayOf(
            arrayOf(
                booleanArrayOf(true, true),
                booleanArrayOf(true, true),
            )
        )
    }
}