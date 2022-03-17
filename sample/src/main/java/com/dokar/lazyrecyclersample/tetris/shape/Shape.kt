package com.dokar.lazyrecyclersample.tetris.shape

abstract class Shape {
    protected var rotation: Int = 0

    open fun getShape(): Array<BooleanArray> {
        val transforms = getTransforms()
        val count = transforms.size
        return if (count == 1) {
            transforms[0]
        } else if (rotation == 90 && count > 1) {
            transforms[1]
        } else if (rotation == 180 && count > 2) {
            transforms[2]
        } else if (rotation == 270 && count > 3) {
            transforms[3]
        } else {
            transforms[0]
        }
    }

    open fun rotate() {
        val transforms = getTransforms()
        val size = transforms.size
        if (size == 1) {
            return
        }
        rotation = if (rotation == 0 && size > 1) {
            90
        } else if (rotation == 90 && size > 2) {
            180
        } else if (rotation == 180 && size > 3) {
            270
        } else {
            0
        }
    }

    open fun tryRotate(): Array<BooleanArray> {
        val transforms = getTransforms()
        val count = transforms.size
        return if (rotation == 0 && count > 1) {
            transforms[1]
        } else if (rotation == 90 && count > 2) {
            transforms[2]
        } else if (rotation == 180 && count > 3) {
            transforms[3]
        } else {
            transforms[0]
        }
    }

    open fun getRotationXYOffset(): IntArray? {
        return null
    }

    open fun reset() {
        rotation = 0
    }

    abstract fun getTransforms(): Array<Array<BooleanArray>>
}
