package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

typealias ViewBindingInflate<V> = (
    inflater: LayoutInflater,
    parent: ViewGroup,
    attachToParent: Boolean
) -> V

class ViewBindingCreator2<V : ViewBinding>(
    private val inflate: ViewBindingInflate<V>
) : ViewCreator<V> {
    override fun createViewHolder(
        parent: ViewGroup,
        binder: ItemBinder<V, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<V> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflate(inflater, parent, false)
        return LazyViewHolder(binding.root, binding, binder)
    }
}