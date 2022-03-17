package com.dokar.lazyrecycler

import java.util.concurrent.atomic.AtomicInteger

internal object Utils {
    private val ID = AtomicInteger(0)

    fun newViewType(): Int {
        return ID.getAndIncrement()
    }
}
