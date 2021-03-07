package com.dokar.lazyrecycler.rxjava3

import com.dokar.lazyrecycler.SectionConfigurator

fun <I> SectionConfigurator<I>.showWhile(showWhile: ShowWhile): SectionConfigurator<I> {
    section().applyExtra { this.showWhile = showWhile }
    return this
}