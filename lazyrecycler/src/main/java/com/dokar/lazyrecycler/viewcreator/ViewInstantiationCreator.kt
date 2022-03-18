package com.dokar.lazyrecycler.viewcreator

import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.BindViewScopeImpl
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

class ViewInstantiationCreator<I>(
    private val bind: BindViewScope<I>.(parent: ViewGroup) -> View
) : ViewHolderCreator<BindViewScope<I>> {
    override fun create(
        parent: ViewGroup,
        binder: ItemBinder<BindViewScope<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<BindViewScope<I>> {
        val binds = BindViewScopeImpl<I>()
        binds.itemProvider = itemProvider

        val view = bind(binds, parent)

        require(view != parent) {
            "The parent cannot be the item view."
        }

        return LazyViewHolder(view, binds, binder).also {
            binds.viewHolder = it
        }
    }
}
