package com.tailoredapps.template.ui.base

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.tailoredapps.template.BR
import com.tailoredapps.template.injection.components.DaggerFragmentViewHolderComponent
import com.tailoredapps.template.injection.components.FragmentViewHolderComponent
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.ui.base.viewmodel.MvvmViewModel
import com.tailoredapps.template.util.extensions.attachViewOrThrowRuntimeException
import com.tailoredapps.template.util.extensions.castWithUnwrap
import javax.inject.Inject

/* Base class for ViewHolders when using a view model in a Fragment with data binding.
 * This class provides the binding and the view model to the subclass. The
 * view model is injected and the binding is created when the content view is bound.
 * Each subclass therefore has to call the following code in the constructor:
 *    bindContentView(view)
 *
 * After calling this method, the binding and the view model is initialized.
 * saveInstanceState() and restoreInstanceState() are not called/used for ViewHolder
 * view models.
 *
 * Your subclass must implement the MvvmView implementation that you use in your
 * view model. */
abstract class BaseFragmentViewHolder<B : ViewDataBinding, VM : MvvmViewModel<*>>(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), MvvmView {

    protected lateinit var binding: B
    @Inject lateinit var viewModel: VM
        protected set

    protected abstract val fragmentContainerId : Int

    protected val viewHolderComponent: FragmentViewHolderComponent by lazy {
        DaggerFragmentViewHolderComponent.builder()
                .fragmentComponent(itemView.context.getFragment<BaseFragment<*,*>>(fragmentContainerId)!!.fragmentComponent)
                .build()
    }

    protected fun bindContentView(view: View) {
        try {
            FragmentViewHolderComponent::class.java.getDeclaredMethod("inject", this::class.java).invoke(viewHolderComponent, this)
        } catch(e: NoSuchMethodException) {
            throw RtfmException("You forgot to add \"fun inject(viewHolder: ${this::class.java.simpleName})\" in FragmentViewHolderComponent")
        }

        binding = DataBindingUtil.bind(view)!!
        binding.setVariable(BR.vm, viewModel)
        viewModel.attachViewOrThrowRuntimeException(this, null)
    }

    private inline fun <reified T : androidx.fragment.app.Fragment> Context.getFragment(containerId: Int) =
            castWithUnwrap<androidx.fragment.app.FragmentActivity>()?.run { supportFragmentManager.findFragmentById(containerId) as? T }

    fun executePendingBindings() {
        binding.executePendingBindings()
    }
}