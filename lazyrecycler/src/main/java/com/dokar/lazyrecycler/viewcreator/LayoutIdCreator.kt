package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindHolder
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

typealias BindScope<I> = BindHolder<I>.(view: View) -> Unit

class LayoutIdCreator<I>(
    private val layoutId: Int,
    private val bindScope: BindScope<I>
) : ViewCreator<BindHolder<I>> {

    override fun createViewHolder(
        parent: ViewGroup,
        binder: ItemBinder<BindHolder<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<BindHolder<I>> {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)

        val binds = BindHolder<I>()
        binds.itemProvider = itemProvider

        bindScope(binds, view)

        return LazyViewHolder(view, binds, binder).also {
            binds.viewHolder = it
        }
    }
}
