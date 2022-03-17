package com.dokar.lazyrecycler.viewbinder

import android.view.View
import android.view.ViewGroup

typealias ViewInstantiationBindScope<I> = BindViewScope<I>.(parent: ViewGroup) -> View
