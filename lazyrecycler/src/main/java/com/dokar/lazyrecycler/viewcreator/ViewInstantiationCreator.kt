package com.dokar.lazyrecycler.viewcreator

import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.SimpleBindable
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

internal class ViewInstantiationCreator<I>(
    private val bind: BindViewScope<I>.(parent: ViewGroup) -> View
) : ViewHolderCreator<SimpleBindable<I>> {
    override fun create(
        parent: ViewGroup,
        binder: ItemBinder<SimpleBindable<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<SimpleBindable<I>> {
        val bindable = SimpleBindable<I>(itemProvider)

        val view = bind(bindable, parent)

        require(view != parent) {
            "The parent cannot be the item view."
        }

        return LazyViewHolder(view, bindable, binder).also {
            bindable.viewHolder = it
        }
    }
}
