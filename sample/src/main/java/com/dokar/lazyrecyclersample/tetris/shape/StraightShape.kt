package com.dokar.lazyrecyclersample.tetris.shape

class StraightShape : Shape() {
    override fun getTransforms(): Array<Array<BooleanArray>> {
        return TRANSFORMS
    }

    override fun getRotationXYOffset(): IntArray {
        return if (rotation == 0) {
            intArrayOf(1, 0)
        } else {
            intArrayOf(-1, 0)
        }
    }

    companion object {
        private val TRANSFORMS = arrayOf(
            arrayOf(
                booleanArrayOf(
                    true, true, true, true
                )
            ),
            arrayOf(
                booleanArrayOf(true),
                booleanArrayOf(true),
                booleanArrayOf(true),
                booleanArrayOf(true),
            )
        )
    }
}
