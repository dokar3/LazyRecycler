package com.dokar.lazyrecycler.viewcreator

import android.view.View
import android.view.ViewGroup

interface ViewCreator<V> {

    /**
     * Create item view
     * @param parent
     * @return A pair of itemView (for ViewHolder) and view (for bind)
     * */
    fun create(parent: ViewGroup): Pair<View, V>
}
