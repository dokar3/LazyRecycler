package com.dokar.lazyrecyclersample.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter(value = ["imageUrl"])
fun imageUrl(imageView: ImageView, url: String) {
    imageView.load(url)
}
