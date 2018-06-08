/* Copyright 2017 Patrick Löwenstein
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
 * FILE MODIFIED 2017 Tailored Media GmbH */

package com.tailoredapps.template.ui.base.navigator

import android.content.Intent
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class ChildFragmentNavigator(private val fragment: Fragment) : ActivityNavigator(fragment.activity!!), FragmentNavigator {

    override val fragmentManager: FragmentManager? get() = fragment.fragmentManager
    private val childFragmentManager get() = fragment.childFragmentManager

    override fun startActivityInternal(intent: Intent, requestCode: Int?, adaptIntentFun: (Intent.() -> Unit)?) {
        adaptIntentFun?.invoke(intent)

        if (requestCode != null) {
            fragment.startActivityForResult(intent, requestCode)
        } else {
            fragment.startActivity(intent)
        }
    }

    override fun replaceChildFragment(@IdRes containerId: Int, fragment: Fragment, fragmentTag: String?) {
        replaceFragmentInternal(childFragmentManager, containerId, fragment, fragmentTag, false, null)
    }

    override fun replaceChildFragmentAndAddToBackStack(@IdRes containerId: Int, fragment: Fragment, fragmentTag: String?, backstackTag: String?) {
        replaceFragmentInternal(childFragmentManager, containerId, fragment, fragmentTag, true, backstackTag)
    }

    override fun popChildFragmentBackstackImmediate() {
        childFragmentManager.popBackStackImmediate()
    }

}

