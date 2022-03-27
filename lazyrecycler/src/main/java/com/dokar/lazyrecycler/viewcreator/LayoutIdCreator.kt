package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindViewScope
import com.dokar.lazyrecycler.viewbinder.SimpleBindable
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

internal class LayoutIdCreator<I>(
    private val layoutId: Int,
    private val bind: BindViewScope<I>.(view: View) -> Unit
) : ViewHolderCreator<SimpleBindable<I>> {
    override fun create(
        parent: ViewGroup,
        binder: ItemBinder<SimpleBindable<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<SimpleBindable<I>> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)

        val bindable = SimpleBindable<I>(itemProvider)

        bind(bindable, view)

        return LazyViewHolder(view, bindable, binder).also {
            bindable.viewHolder = it
        }
    }
}
