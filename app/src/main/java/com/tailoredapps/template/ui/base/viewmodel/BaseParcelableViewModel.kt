package com.tailoredapps.template.ui.base.viewmodel

import android.os.Bundle
import android.os.Parcelable
import com.tailoredapps.template.ui.base.view.MvvmView

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
 * limitations under the License.
 */

/**
 * Base class that implements the ViewModel interface and provides a base implementation for
 * attachView() and detachView(). It also handles keeping a reference to the mvvmView that
 * can be accessed from the children classes by calling getMvpView().

 * When saving state is required, restoring is handled automatically when calling attachView().
 * However, saveInstanceState() must still be called in the corresponding lifecycle callback. */
abstract class BaseParcelableViewModel<V : MvvmView, S : Parcelable, PS, SR : StateReducer<S, PS>>
    constructor(stateReducer: SR): BaseViewModel<V, S, PS, SR>(stateReducer) {

    override fun initState(savedInstanceState: Bundle?) {
        if(savedInstanceState == null) {
            state = initialState
        } else {
            state = savedInstanceState.getParcelable(KEY_STATE)
        }
    }

    override fun saveInstanceState(outState: Bundle?) {
        outState?.putParcelable(KEY_STATE, state)
    }

    companion object {
        val KEY_STATE = "_state";
    }
}
