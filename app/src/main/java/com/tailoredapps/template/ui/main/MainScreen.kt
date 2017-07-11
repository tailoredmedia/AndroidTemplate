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
 * --------------
 *
 * FILE MODIFIED 2017 Tailored Media GmbH
 *
 */

package com.tailoredapps.template.ui.main

import android.os.Bundle
import com.jakewharton.rxrelay2.PublishRelay
import com.tailoredapps.template.R
import com.tailoredapps.template.databinding.ActivityMainBinding
import com.tailoredapps.template.injection.scopes.PerActivity
import com.tailoredapps.template.ui.base.BaseActivity
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.ui.base.viewmodel.BaseParcelableViewModel
import com.tailoredapps.template.ui.base.viewmodel.MvvmViewModel
import io.reactivex.Observable
import javax.inject.Inject


interface MainScreen {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View, MainState> {
        val incrementClickRelay : PublishRelay<Any>
        val resetClickRelay : PublishRelay<Any>
    }
}


class MainActivity : BaseActivity<ActivityMainBinding, MainScreen.ViewModel>(), MainScreen.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)
        setAndBindContentView(savedInstanceState, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
    }

}


@PerActivity
class MainViewModel
@Inject
constructor(stateReducer: MainStateReducer) : BaseParcelableViewModel<MainScreen.View, MainState, MainPartialState>(stateReducer), MainScreen.ViewModel {
    override val resetClickRelay: PublishRelay<Any> = PublishRelay.create()
    override val incrementClickRelay: PublishRelay<Any> = PublishRelay.create()

    override val initialState: MainState
        get() = MainState()

    override val partialStateObservable: Observable<MainPartialState>
        get() = Observable.merge(
                incrementClickRelay.map { MainPartialState.Increment },
                resetClickRelay.map { MainPartialState.Reset }
        )
}

