package com.dokar.lazyrecycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.viewbinder.ItemBinder

open class LazyViewHolder<V>(
    root: View,
    private val view: V,
    private val viewBinder: ItemBinder<V, Any>
) : RecyclerView.ViewHolder(root) {

    open fun bind(item: Any, position: Int) {
        viewBinder.bind(view, item, position)
    }
}