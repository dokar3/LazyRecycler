package com.dokar.lazyrecycler.viewbinder

interface ItemProvider {

    /**
     * Get item form an adapter position
     */
    fun getItem(adapterPosition: Int): Any?

    /**
     * Get position in section from an adapter position
     */
    fun getPositionInSection(adapterPosition: Int): Int
}
