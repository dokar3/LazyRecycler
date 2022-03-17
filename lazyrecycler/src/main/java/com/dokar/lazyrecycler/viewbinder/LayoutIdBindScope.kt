package com.dokar.lazyrecycler.viewbinder

import android.view.View

typealias LayoutIdBindScope<I> = BindViewScope<I>.(view: View) -> Unit
