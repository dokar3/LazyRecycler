package com.dokar.lazyrecycler.rxjava3

import com.dokar.lazyrecycler.SectionConfigurator

fun <I> SectionConfigurator<I>.showWhile(showWhile: ShowWhile): SectionConfigurator<I> {
    section().computeExtra { it.showWhile = showWhile }
    return this
}