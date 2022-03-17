package com.dokar.lazyrecycler.data

fun interface ValueObserver<I> {
    fun onChanged(data: MutableValue<I>)
}
