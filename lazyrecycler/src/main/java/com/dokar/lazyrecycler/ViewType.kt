package com.dokar.lazyrecycler

class ViewType<I>(
    val template: Template<I>,
    val where: (position: Int, item: I) -> Boolean,
)
