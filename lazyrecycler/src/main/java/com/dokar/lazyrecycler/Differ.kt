package com.dokar.lazyrecycler

import androidx.recyclerview.widget.DiffUtil

open class Differ<D> : DiffUtil.ItemCallback<D>() {
    private var areItemsTheSame: AreItemsTheSame<D>? = null

    private var areContentsTheSame: AreContentsTheSame<D>? = null

    override fun areItemsTheSame(oldItem: D, newItem: D): Boolean {
        return areItemsTheSame?.invoke(oldItem, newItem) ?: false
    }

    override fun areContentsTheSame(oldItem: D, newItem: D): Boolean {
        return areContentsTheSame?.invoke(oldItem, newItem) ?: false
    }

    fun areItemsTheSame(expression: AreItemsTheSame<D>) {
        areItemsTheSame = expression
    }

    fun areContentsTheSame(expression: AreContentsTheSame<D>) {
        areContentsTheSame = expression
    }
}
