/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * -------
 *
 * FILE MODIFIED 2017 Tailored Media GmbH
 */


package com.tailoredapps.uibase

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.squareup.leakcanary.RefWatcher
import com.tailoredapps.uibase.view.MvvmView
import com.tailoredapps.uibase.viewmodel.MvvmViewModel
import com.tailoredapps.uibase.viewmodel.attachViewOrThrowRuntimeException
import java.lang.reflect.ParameterizedType
import javax.inject.Inject


/* Base class for Activities when using a view model with data binding.
 * This class provides the binding and the view model to the subclass. The
 * view model is injected and the binding is created when the content view is set.
 * Each subclass therefore has to call the following code in onCreate():
 *    setAndBindContentView(savedInstanceState, R.layout.my_activity_layout)
 *
 * After calling this method, the binding and the view model is initialized.
 * saveInstanceState() and restoreInstanceState() methods of the view model
 * are automatically called in the appropriate lifecycle events when above calls
 * are made.
 *
 * Your subclass must implement the MvvmView implementation that you use in your
 * view model. */
abstract class BaseActivity<B : ViewDataBinding, VM : MvvmViewModel<*>> : AppCompatActivity(), MvvmView {

    companion object {
        internal var appComponent: Any? = null
    }

    protected lateinit var binding: B
    @Inject protected lateinit var viewModel: VM

    @Inject
    protected lateinit var refWatcher: RefWatcher

    //@field:[Inject ActivityDisposable]
    //internal lateinit var disposable: CompositeDisposable

    /*internal val activityComponent: ActivityComponent by lazy {
        DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .appComponent(MyApp.appComponent)
                .build()
    }*/


    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveInstanceState(outState)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            if(appComponent == null) { appComponent = applicationContext::class.java.getField("appComponent").get(applicationContext) }
            appComponent!!::class.java.getDeclaredMethod("inject", this::class.java).invoke(appComponent, this)
        } catch(e: NoSuchMethodException) {
            throw RtfmException("You forgot to add \"fun inject(activity: ${this::class.java.simpleName})\" in ActivityComponent")
        }
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        //disposable.clear()
        viewModel.detachView()
        //refWatcher.watch(activityComponent)
        refWatcher.watch(viewModel)
    }

    /* Sets the content view, creates the binding and attaches the view to the view model */
    protected fun setAndBindContentView(savedInstanceState: Bundle?, @LayoutRes layoutResID: Int) {
        binding = DataBindingUtil.setContentView(this, layoutResID)

        try {
            var baseActivityClass : Class<*> = this::class.java
            while(baseActivityClass.superclass !== BaseActivity::class.java) { baseActivityClass = baseActivityClass.superclass!! }
            val viewModelClass = (baseActivityClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*>
            binding::class.java.getDeclaredMethod("setVm", viewModelClass).invoke(binding, viewModel)
        } catch(e: Exception) {
            throw RtfmException("You forgot to add a vm variable in your layout file for ${this::class.java.simpleName}")
        }

        viewModel.attachViewOrThrowRuntimeException(this, savedInstanceState)
    }

}
