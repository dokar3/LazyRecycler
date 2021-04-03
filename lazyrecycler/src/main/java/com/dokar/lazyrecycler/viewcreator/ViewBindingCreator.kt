package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

class ViewBindingCreator<V : ViewBinding>(
    bindingClass: Class<V>
) : ViewCreator<V> {

    private val inflateFunc = bindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    ) ?: throw IllegalArgumentException("Cannot find inflate() in $bindingClass")

    init {
        inflateFunc.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolder(
        parent: ViewGroup,
        binder: ItemBinder<V, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<V> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflateFunc.invoke(null, inflater, parent, false) as V
        return LazyViewHolder(binding.root, binding, binder)
    }
}
