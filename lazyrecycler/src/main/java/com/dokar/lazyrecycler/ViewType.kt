package com.dokar.lazyrecycler

class ViewType<I : Any>(
    val template: Template<I>,
    val where: (position: Int, item: I) -> Boolean,
)
