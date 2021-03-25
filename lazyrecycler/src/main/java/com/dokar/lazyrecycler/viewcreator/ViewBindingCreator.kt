package com.dokar.lazyrecycler.viewcreator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

class ViewBindingCreator<V : ViewBinding>(
    bindingClass: Class<V>
) : ViewCreator<V> {

    private var inflateFunc = bindingClass.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    ) ?: throw IllegalArgumentException("Cannot find inflate() in $bindingClass")

    init {
        inflateFunc.isAccessible = true
    }

    @Suppress("UNCHECKED_CAST")
    override fun create(parent: ViewGroup): Pair<View, V> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = inflateFunc.invoke(null, inflater, parent, false) as V
        return binding.root to binding
    }
}
