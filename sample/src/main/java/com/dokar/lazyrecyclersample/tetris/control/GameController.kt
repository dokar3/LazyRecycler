package com.dokar.lazyrecyclersample.tetris.control

abstract class GameController {
    private var eventHandler: ControlEventHandler? = null

    fun setEventHandler(handler: ControlEventHandler) {
        eventHandler = handler
    }

    fun dispatchEvent(event: Int) {
        eventHandler?.handleEvent(event)
    }

    companion object {
        const val NONE = -1

        const val LEFT_KEY_DOWN = 0
        const val LEFT_KEY_UP = 1

        const val UP_KEY_DOWN = 2
        const val UP_KEY_UP = 3

        const val RIGHT_KEY_DOWN = 4
        const val RIGHT_KEY_UP = 5

        const val DOWN_KEY_DOWN = 6
        const val DOWN_KEY_UP = 7

        const val SINGLE_TAP_DOWN = 8
        const val SINGLE_TAP_UP = 9
    }
}
