package com.dokar.lazyrecyclersample.tetris.control

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import java.lang.ref.WeakReference
import kotlin.math.abs
import kotlin.math.max

class SwipeGameController(context: Context) : GameController(), View.OnTouchListener {
    private var downX = 0f
    private var downY = 0f

    private var currDownEvent = NONE

    private var distanceThreshold = 0
    private var velocityThreshold = 0

    private val touchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()

    private val delayedEventDispatcher = DelayedEventDispatcher(this)

    init {
        val density = context.resources.displayMetrics.density
        distanceThreshold = (16 * density).toInt()
        velocityThreshold = (20 * density).toInt()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        velocityTracker.addMovement(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                currDownEvent = NONE
                delayedEventDispatcher.sendEmptyMessageDelayed(SINGLE_TAP_DOWN, 150)
            }
            MotionEvent.ACTION_MOVE -> {
                if (currDownEvent != NONE) {
                    return false
                }
                val dx = event.x - downX
                val dy = event.y - downY
                velocityTracker.computeCurrentVelocity(1000)
                val vx = velocityTracker.xVelocity
                val vy = velocityTracker.yVelocity
                if (abs(dx) > abs(dy)) {
                    if (abs(dx) > distanceThreshold &&
                        abs(vx) > velocityThreshold
                    ) {
                        currDownEvent = if (dx > 0) {
                            RIGHT_KEY_DOWN
                        } else {
                            LEFT_KEY_DOWN
                        }
                    }
                } else {
                    if (abs(dy) > distanceThreshold &&
                        abs(vy) > velocityThreshold
                    ) {
                        currDownEvent = if (dy > 0) {
                            DOWN_KEY_DOWN
                        } else {
                            UP_KEY_DOWN
                        }
                    }
                }
                if (currDownEvent != NONE) {
                    delayedEventDispatcher.removeMessages(SINGLE_TAP_DOWN)
                    dispatchEvent(currDownEvent)
                }
            }
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                velocityTracker.clear()
                val delta = max(abs(event.x - downX), abs(event.y - downY))
                if (currDownEvent == NONE && delta <= touchSlop) {
                    currDownEvent = SINGLE_TAP_DOWN
                    delayedEventDispatcher.removeMessages(currDownEvent)
                    dispatchEvent(currDownEvent)
                }
                dispatchUpEvent(currDownEvent)
            }
        }
        return false
    }

    private fun dispatchUpEvent(pressedEvent: Int) {
        when (pressedEvent) {
            LEFT_KEY_DOWN -> {
                dispatchEvent(LEFT_KEY_UP)
            }
            RIGHT_KEY_DOWN -> {
                dispatchEvent(RIGHT_KEY_UP)
            }
            UP_KEY_DOWN -> {
                dispatchEvent(UP_KEY_UP)
            }
            DOWN_KEY_DOWN -> {
                dispatchEvent(DOWN_KEY_UP)
            }
            SINGLE_TAP_DOWN -> {
                dispatchEvent(SINGLE_TAP_UP)
            }
        }
    }

    class DelayedEventDispatcher(controller: SwipeGameController) :
        Handler(Looper.getMainLooper()) {

        private val ref = WeakReference(controller)

        override fun handleMessage(msg: Message) {
            val controller = ref.get() ?: return
            val event = msg.what
            controller.currDownEvent = event
            controller.dispatchEvent(event)
        }
    }
}
