package com.dokar.lazyrecyclersample.tetris.shape

class TShape : Shape() {
    override fun getTransforms(): Array<Array<BooleanArray>> {
        return TRANSFORMS
    }

    companion object {
        private val TRANSFORMS = arrayOf(
            arrayOf(
                booleanArrayOf(true, true, true),
                booleanArrayOf(false, true, false),
            ),
            arrayOf(
                booleanArrayOf(false, true),
                booleanArrayOf(true, true),
                booleanArrayOf(false, true),
            ),
            arrayOf(
                booleanArrayOf(false, true, false),
                booleanArrayOf(true, true, true),
            ),
            arrayOf(
                booleanArrayOf(true, false),
                booleanArrayOf(true, true),
                booleanArrayOf(true, false),
            )
        )
    }
}
