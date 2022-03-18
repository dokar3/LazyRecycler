package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.BindViewScopeImpl
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider
import com.dokar.lazyrecycler.viewbinder.LayoutIdBindScope

class LayoutIdCreator<I>(
    private val layoutId: Int,
    private val bindScope: LayoutIdBindScope<I>
) : ViewHolderCreator<BindViewScope<I>> {
    override fun create(
        parent: ViewGroup,
        binder: ItemBinder<BindViewScope<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<BindViewScope<I>> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)

        val binds = BindViewScopeImpl<I>()
        binds.itemProvider = itemProvider

        bindScope(binds, view)

        return LazyViewHolder(view, binds, binder).also {
            binds.viewHolder = it
        }
    }
}
