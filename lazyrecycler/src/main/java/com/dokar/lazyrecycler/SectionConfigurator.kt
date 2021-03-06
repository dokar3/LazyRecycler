package com.dokar.lazyrecycler

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

    fun differ(body: Differ<I>.() -> Unit): SectionConfigurator<I> {
        val differ = Differ<I>()
        differ.body()
        section.differ = differ
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

    fun section(): Section<out Any?, I> {
        return section
    }
}