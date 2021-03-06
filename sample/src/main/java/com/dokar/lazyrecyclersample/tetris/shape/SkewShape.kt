package com.dokar.lazyrecyclersample.tetris.shape

class SkewShape : Shape() {

    override fun getTransforms(): Array<Array<BooleanArray>> {
        return TRANSFORMS
    }

    companion object {

        private val TRANSFORMS = arrayOf(
            arrayOf(
                booleanArrayOf(false, true, true),
                booleanArrayOf(true, true, false),
            ),
            arrayOf(
                booleanArrayOf(true, false),
                booleanArrayOf(true, true),
                booleanArrayOf(false, true),
            )
        )
    }
}
