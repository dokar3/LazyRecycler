package com.dokar.lazyrecycler.viewcreator

import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.BindViewScopeImpl
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider
import com.dokar.lazyrecycler.viewbinder.ViewInstantiationBindScope

class ViewInstantiationCreator<I>(
    private val bindScope: ViewInstantiationBindScope<I>
) : ViewHolderCreator<BindViewScope<I>> {
    override fun create(
        parent: ViewGroup,
        binder: ItemBinder<BindViewScope<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<BindViewScope<I>> {
        val binds = BindViewScopeImpl<I>()
        binds.itemProvider = itemProvider

        val view = bindScope(binds, parent)

        require(view != parent) {
            "The parent cannot be the item view."
        }

        return LazyViewHolder(view, binds, binder).also {
            binds.viewHolder = it
        }
    }
}
