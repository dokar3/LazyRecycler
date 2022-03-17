package com.dokar.lazyrecycler

import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewcreator.ViewHolderCreator

/**
 * List section class
 *
 * @param V view type, used to bind item
 * @param I item type
 */
open class Section<V, I>(
    val id: Int,
    val viewHolderCreator: ViewHolderCreator<V>,
    val itemBinder: ItemBinder<V, I>,
    var items: List<I>,
    var onItemClick: OnItemClick<I>? = null,
    var onItemLongClick: OnItemLongClick<I>? = null,
    var differ: Differ<I>? = null,
    var spanSizeLookup: SpanSizeLookup? = null
) {
    internal var viewType: Int = Utils.newViewType()

    internal var subSections: MutableList<Pair<Section<Any, I>, Where<I>>>? = null

    private var extras: MutableList<Any>? = null

    fun addSubSection(section: Section<Any, I>, where: Where<I>) {
        if (subSections == null) {
            subSections = mutableListOf()
        }
        subSections!!.add(section to where)
    }

    fun putExtra(extra: Any) {
        if (extras == null) {
            extras = mutableListOf()
        }
        extras!!.add(extra)
    }

    fun putExtras(extras: List<Any>) {
        if (this.extras == null) {
            this.extras = mutableListOf()
        }
        this.extras!!.addAll(extras)
    }

    fun <E> findExtra(clz: Class<E>): E? {
        val ext = extras?.find { clz.isAssignableFrom(it::class.java) }
        return if (ext != null) clz.cast(ext)!! else null
    }

    fun <E> findExtras(clz: Class<E>): List<E>? {
        return extras?.filter {
            clz.isAssignableFrom(it::class.java)
        }?.map {
            clz.cast(it)!!
        }
    }
}
