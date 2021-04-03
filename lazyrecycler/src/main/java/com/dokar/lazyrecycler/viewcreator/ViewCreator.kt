package com.dokar.lazyrecycler.viewcreator

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

interface ViewCreator<V> {

    /**
     * Create item view
     * @param parent the parent ViewGroup, the same as parent at
     * [RecyclerView.Adapter.onCreateViewHolder]
     * @return A pair of itemView (for ViewHolder) and view (for bind)
     * */
    fun createViewHolder(
        parent: ViewGroup,
        binder: ItemBinder<V, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<V>
}
