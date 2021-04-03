package com.dokar.lazyrecycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.viewbinder.ItemBinder

open class LazyViewHolder<V>(
    itemView: View,
    private val view: V,
    private val viewBinder: ItemBinder<V, Any>
) : RecyclerView.ViewHolder(itemView) {

    open fun bind(item: Any?, position: Int) {
        if (item == null) return
        viewBinder.bind(view, item, position)
    }
}
