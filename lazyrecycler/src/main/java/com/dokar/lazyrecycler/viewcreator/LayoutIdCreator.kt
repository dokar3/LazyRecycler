package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.viewbinder.ItemViewBinder

typealias BindScope<I> = ItemViewBinder.BindWrapper<I>.(view: View) -> Unit

class LayoutIdCreator<I>(
    private val layoutId: Int,
    private val bindScope: BindScope<I>
) : ViewCreator<ItemViewBinder.BindWrapper<I>> {

    override fun create(parent: ViewGroup): Pair<View, ItemViewBinder.BindWrapper<I>> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        val wrapper = ItemViewBinder.BindWrapper<I>()
        bindScope(wrapper, view)
        return view to wrapper
    }
}