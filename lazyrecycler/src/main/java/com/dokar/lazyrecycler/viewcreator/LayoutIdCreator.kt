package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.BindViewScopeImpl
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

class LayoutIdCreator<I>(
    private val layoutId: Int,
    private val bind: BindViewScope<I>.(view: View) -> Unit
) : ViewHolderCreator<BindViewScope<I>> {
    override fun create(
        parent: ViewGroup,
        binder: ItemBinder<BindViewScope<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<BindViewScope<I>> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)

        val bindScope = BindViewScopeImpl<I>()
        bindScope.itemProvider = itemProvider

        bind(bindScope, view)

        return LazyViewHolder(view, bindScope, binder).also {
            bindScope.viewHolder = it
        }
    }
}
