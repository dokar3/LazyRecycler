package com.dokar.lazyrecyclersample.sticky

import com.dokar.lazyrecycler.LazyAdapter
import com.dokar.lazyrecycler.Section
import com.jay.widget.StickyHeaders

class StickyHeaderAdapter(
    sections: MutableList<Section<Any, Any>>
) : LazyAdapter(sections), StickyHeaders {

    override fun isStickyHeader(position: Int) = getItem(position) is Header
}