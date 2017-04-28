package com.tailoredapps.template.util

import android.databinding.BindingAdapter
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxrelay2.PublishRelay

object BindingAdapters {

    private val NOTIFICATION = Any()

    @BindingAdapter("android:visibility")
    @JvmStatic
    fun setVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @BindingAdapter("android:layout_marginBottom")
    @JvmStatic
    fun setLayoutMarginBottom(v: View, bottomMargin: Int) {
        val layoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.bottomMargin = bottomMargin
    }

    @BindingAdapter("android:onClick")
    @JvmStatic
    fun setPublishRelayOnClick(v: View, relay: PublishRelay<Any>) {
        v.setOnClickListener { relay.accept(NOTIFICATION) }
    }
}
