package com.dokar.lazyrecyclersample.tetris.shape

class LShape : Shape() {

    override fun getTransforms(): Array<Array<BooleanArray>> {
        return TRANSFORMS
    }

    companion object {

        private val TRANSFORMS = arrayOf(
            arrayOf(
                booleanArrayOf(true, false),
                booleanArrayOf(true, false),
                booleanArrayOf(true, true),
            ),
            arrayOf(
                booleanArrayOf(true, true, true),
                booleanArrayOf(true, false, false),
            ),
            arrayOf(
                booleanArrayOf(true, true),
                booleanArrayOf(false, true),
                booleanArrayOf(false, true),
            ),
            arrayOf(
                booleanArrayOf(false, false, true),
                booleanArrayOf(true, true, true),
            ),
        )
    }
}
