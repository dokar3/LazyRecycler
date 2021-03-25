package com.dokar.lazyrecycler

import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.PropertyNames

fun <I> Template<I>.config(): SectionConfigurator<I> {
    return SectionConfigurator(this)
}

class SectionConfigurator<I>(private val section: Section<out Any?, I>) {

    fun clicks(onItemClick: OnItemClick<I>): SectionConfigurator<I> {
        section.onItemClick = onItemClick
        section.subSections?.forEach {
            it.first.onItemClick = onItemClick
        }
        return this
    }

    fun longClicks(onItemLongClick: OnItemLongClick<I>): SectionConfigurator<I> {
        section.onItemLongClick = onItemLongClick
        section.subSections?.forEach {
            it.first.onItemLongClick = onItemLongClick
        }
        return this
    }

    fun differ(differ: Differ<I>): SectionConfigurator<I> {
        section.differ = differ
        return this
    }

    inline fun differ(body: Differ<I>.() -> Unit): SectionConfigurator<I> {
        val differ = Differ<I>()
        differ.body()
        section().differ = differ
        return this
    }

    fun spanSize(spanSizeLookup: SpanSizeLookup): SectionConfigurator<I> {
        section.spanCountLookup = spanSizeLookup
        return this
    }

    fun subSection(
        sub: Section<Any, I>,
        where: Where<I>
    ): SectionConfigurator<I> {
        section.addSubSection(sub, where)
        return this
    }

    inline fun showWhile(block: () -> MutableValue<Boolean>): SectionConfigurator<I> {
        val showWhile = block().also {
            it.name = PropertyNames.SHOW_WHILE
        }
        section().putExtra(showWhile)
        return this
    }

    fun section(): Section<out Any?, I> {
        return section
    }
}
