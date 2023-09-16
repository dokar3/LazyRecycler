package com.dokar.lazyrecycler

import androidx.recyclerview.widget.DiffUtil

/**
 * DSL function to create a [DiffUtil.ItemCallback].
 *
 * ### Example:
 *
 * ```kotlin
 * val diffCallback = diffCallback {
 *     areItemsTheSame { oldItem, newItem -> oldItem.id == newItem.id }
 *     areContentsTheSame { oldItem, newItem -> oldItem == newItem }
 * }
 * ```
 */
inline fun <I : Any> differCallback(
    block: DiffCallback<I>.() -> Unit
): DiffUtil.ItemCallback<I> = DiffCallback<I>().also(block)

open class DiffCallback<I : Any> : DiffUtil.ItemCallback<I>() {
    private var areItemsTheSame: AreItemsTheSame<I>? = null

    private var areContentsTheSame: AreContentsTheSame<I>? = null

    override fun areItemsTheSame(oldItem: I, newItem: I): Boolean {
        return areItemsTheSame?.invoke(oldItem, newItem) ?: false
    }

    override fun areContentsTheSame(oldItem: I, newItem: I): Boolean {
        return areContentsTheSame?.invoke(oldItem, newItem) ?: false
    }

    fun areItemsTheSame(expression: AreItemsTheSame<I>) {
        areItemsTheSame = expression
    }

    fun areContentsTheSame(expression: AreContentsTheSame<I>) {
        areContentsTheSame = expression
    }
}
