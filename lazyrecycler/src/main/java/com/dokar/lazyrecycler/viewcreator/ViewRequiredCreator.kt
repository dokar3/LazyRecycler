package com.dokar.lazyrecycler.viewcreator

import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.viewbinder.ItemViewBinder

typealias ViewRequiredBindScope<I> = ItemViewBinder.BindWrapper<I>.() -> View

class ViewRequiredCreator<I>(
    private val bindScope: ItemViewBinder.BindWrapper<I>.() -> View
) : ViewCreator<ItemViewBinder.BindWrapper<I>> {

    override fun create(parent: ViewGroup): Pair<View, ItemViewBinder.BindWrapper<I>> {
        val wrapper = ItemViewBinder.BindWrapper<I>()
        val view = bindScope(wrapper)
        return view to wrapper
    }

}