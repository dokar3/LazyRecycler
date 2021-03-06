package com.dokar.lazyrecycler

class Differ<D> {

    internal var areItemsTheSame: AreItemsTheSame<D>? = null

    internal var areContentsTheSame: AreContentsTheSame<D>? = null

    fun areItemsTheSame(expression: AreItemsTheSame<D>) {
        areItemsTheSame = expression
    }

    fun areContentsTheSame(expression: AreContentsTheSame<D>) {
        areContentsTheSame = expression
    }

}