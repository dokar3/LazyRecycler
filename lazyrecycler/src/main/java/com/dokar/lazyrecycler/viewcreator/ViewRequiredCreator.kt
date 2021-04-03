package com.dokar.lazyrecycler.viewcreator

import android.view.View
import android.view.ViewGroup
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.BindHolder
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

typealias ViewRequiredBindScope<I> = BindHolder<I>.(parent: ViewGroup) -> View

class ViewRequiredCreator<I>(
    private val bindScope: ViewRequiredBindScope<I>
) : ViewCreator<BindHolder<I>> {

    override fun createViewHolder(
        parent: ViewGroup,
        binder: ItemBinder<BindHolder<I>, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<BindHolder<I>> {
        val binds = BindHolder<I>()
        binds.itemProvider = itemProvider

        val view = bindScope(binds, parent)

        return LazyViewHolder(view, binds, binder).also {
            binds.viewHolder = it
        }
    }
}
