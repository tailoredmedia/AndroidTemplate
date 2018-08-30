package com.tailoredapps.template.ui.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.View
import com.tailoredapps.template.BR
import com.tailoredapps.template.injection.components.DaggerFragmentViewHolderComponent
import com.tailoredapps.template.injection.components.FragmentViewHolderComponent
import com.tailoredapps.template.injection.qualifier.ViewHolderDisposable
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.ui.base.view.MvvmViewHolder
import com.tailoredapps.template.ui.base.viewmodel.MvvmViewModel
import com.tailoredapps.template.util.extensions.attachViewOrThrowRuntimeException
import com.tailoredapps.template.util.extensions.castWithUnwrap
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/* Base class for ViewHolders when using a view model in a Fragment with data binding.
 * This class provides the binding and the view model to the subclass. The
 * view model is injected and the binding is created when the content view is bound.
 * The binding and the view model are initialized in the initialisation of the class.
 *
 * saveInstanceState() and restoreInstanceState() are not called/used for ViewHolder
 * view models.
 *
 * Note, that the `BaseRecyclerViewAdapter` needs to be used if the attachView and detachView
 * methods should be invoked respectively.
 *
 * Your subclass must implement the MvvmView implementation that you use in your
 * view model. */
abstract class BaseFragmentViewHolder<B : ViewDataBinding, VM : MvvmViewModel<*>>(itemView: View) : RecyclerView.ViewHolder(itemView), MvvmView, MvvmViewHolder {

    protected val binding: B
    @Inject lateinit var viewModel: VM
        protected set

    protected abstract val fragmentContainerId: Int

    @field:[Inject ViewHolderDisposable]
    internal lateinit var disposable: CompositeDisposable

    protected val viewHolderComponent: FragmentViewHolderComponent by lazy {
        DaggerFragmentViewHolderComponent.builder()
                .fragmentComponent(itemView.context.getFragment<BaseFragment<*, *>>(fragmentContainerId)!!.fragmentComponent)
                .build()
    }

    init {
        try {
            FragmentViewHolderComponent::class.java.getDeclaredMethod("inject", this::class.java).invoke(viewHolderComponent, this)
        } catch (e: NoSuchMethodException) {
            throw RtfmException("You forgot to add \"fun inject(viewHolder: ${this::class.java.simpleName})\" in FragmentViewHolderComponent")
        }

        binding = DataBindingUtil.bind(itemView)!!
        binding.setVariable(BR.vm, viewModel)
    }

    override fun onBindViewHolder() {
        viewModel.attachViewOrThrowRuntimeException(this, null)
    }

    override fun onViewHolderRecycled() {
        disposable.clear()
        viewModel.detachView()
    }

    fun executePendingBindings() {
        binding.executePendingBindings()
    }

    private inline fun <reified T : Fragment> Context.getFragment(containerId: Int) =
            castWithUnwrap<FragmentActivity>()?.run { supportFragmentManager.findFragmentById(containerId) as? T }

}