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
 * limitations under the License. */

package com.tailoredapps.template.ui.main

import android.os.Bundle
import com.tailoredapps.template.ui.main.databinding.ActivityMainBinding
import com.tailoredapps.uibase.BaseActivity
import com.tailoredapps.uibase.view.MvvmView
import com.tailoredapps.uibase.viewmodel.BaseViewModel
import com.tailoredapps.uibase.viewmodel.MvvmViewModel
import javax.inject.Inject


interface MainMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val text: String
    }
}


class MainActivity : BaseActivity<ActivityMainBinding, MainMvvm.ViewModel>(), MainMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)
    }

}


//@PerActivity
class MainViewModel
@Inject
constructor() : BaseViewModel<MainMvvm.View>(), MainMvvm.ViewModel {

    override val text: String get() = "Nothing to see here!"

}

