package com.tailoredapps.template.ui.base.navigator

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

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
interface FragmentNavigator : Navigator {

    fun replaceChildFragment(@IdRes containerId: Int, fragment: androidx.fragment.app.Fragment, fragmentTag: String? = null)
    fun replaceChildFragmentAndAddToBackStack(@IdRes containerId: Int, fragment: androidx.fragment.app.Fragment, fragmentTag: String?, backstackTag: String?)
    fun popChildFragmentBackstackImmediate()

}
