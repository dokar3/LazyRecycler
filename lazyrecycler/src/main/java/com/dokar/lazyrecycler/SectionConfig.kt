package com.dokar.lazyrecycler

import com.dokar.lazyrecycler.data.MutableValue
import com.dokar.lazyrecycler.data.PropertyNames

open class SectionConfig<I> {

    var sectionId: Int = 1

    var onItemClick: OnItemClick<I>? = null

    var onItemLongClick: OnItemLongClick<I>? = null

    var differ: Differ<I>? = null

    var spanSizeLookup: SpanSizeLookup? = null

    var subSections: MutableList<Pair<Section<Any, I>, Where<I>>>? = null

    var extras: MutableList<MutableValue<out Any>>? = null

    fun addExtra(extra: MutableValue<out Any>): SectionConfig<I> {
        if (extras == null) {
            extras = mutableListOf()
        }
        extras!!.add(extra)
        return this
    }

    fun applyTo(section: Section<*, I>) {
        section.onItemClick = onItemClick
        section.onItemLongClick = onItemLongClick
        section.differ = differ
        section.spanSizeLookup = spanSizeLookup
        section.subSections = subSections
        section.putExtras(extras ?: emptyList())
    }
}

fun <I> SectionConfig<I>.id(id: Int): SectionConfig<I> {
    this.sectionId = id
    return this
}

fun <I> SectionConfig<I>.clicks(onItemClick: OnItemClick<I>): SectionConfig<I> {
    this.onItemClick = onItemClick
    this.subSections?.forEach {
        it.first.onItemClick = onItemClick
    }
    return this
}

fun <I> SectionConfig<I>.longClicks(onItemLongClick: OnItemLongClick<I>): SectionConfig<I> {
    this.onItemLongClick = onItemLongClick
    this.subSections?.forEach {
        it.first.onItemLongClick = onItemLongClick
    }
    return this
}

fun <I> SectionConfig<I>.differ(differ: Differ<I>): SectionConfig<I> {
    this.differ = differ
    return this
}

inline fun <I> SectionConfig<I>.differ(body: Differ<I>.() -> Unit): SectionConfig<I> {
    val differ = Differ<I>()
    differ.body()
    this.differ = differ
    return this
}

fun <I> SectionConfig<I>.spanSize(spanSizeLookup: SpanSizeLookup): SectionConfig<I> {
    this.spanSizeLookup = spanSizeLookup
    return this
}

fun <I> SectionConfig<I>.viewType(template: Template<I>, where: Where<I>): SectionConfig<I> {
    if (this.subSections == null) {
        this.subSections = mutableListOf()
    }
    this.subSections!!.add(template to where)
    return this
}

inline fun <I> SectionConfig<I>.showWhile(block: () -> MutableValue<Boolean>): SectionConfig<I> {
    val showWhile = block().also {
        it.name = PropertyNames.SHOW_WHILE
    }
    addExtra(showWhile)
    return this
}