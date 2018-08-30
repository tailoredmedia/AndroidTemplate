package com.tailoredapps.template.ui.base

import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import com.tailoredapps.template.ui.base.view.MvvmViewHolder


/**
 * Base class for [RecyclerView.Adapter] when caring about the view holder's lifecycle.
 *
 * Using this base adapter, the view models `attachView` and `detachView` methods will be invoked,
 * as well as the `ViewHolderDisposable` will be cleared.
 */
abstract class BaseRecyclerViewAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    @CallSuper
    override fun onBindViewHolder(holder: VH, position: Int) {
        (holder as? MvvmViewHolder)?.onBindViewHolder()
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        (holder as? MvvmViewHolder)?.onViewHolderRecycled()
    }

}