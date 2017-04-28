package com.tailoredapps.template.ui.main

import com.jakewharton.rxrelay2.PublishRelay
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.ui.base.viewmodel.MvvmViewModel

/* Copyright 2017 Tailored Media GmbH
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

interface MainMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View, MainState> {
        val incrementClickRelay : PublishRelay<Any>
        val resetClickRelay : PublishRelay<Any>
    }
}
