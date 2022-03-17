package com.dokar.lazyrecycler.viewcreator

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dokar.lazyrecycler.LazyViewHolder
import com.dokar.lazyrecycler.viewbinder.ItemBinder
import com.dokar.lazyrecycler.viewbinder.ItemProvider

interface ViewHolderCreator<V : Any?> {
    /**
     * Create a LazyViewHolder.
     */
    fun create(
        parent: ViewGroup,
        binder: ItemBinder<V, Any>,
        itemProvider: ItemProvider
    ): LazyViewHolder<V>
}
