package com.tailoredapps.template.ui.base.navigator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.annotation.IdRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import android.view.View

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
 * ------
 *
 * FILE CHANGED 2017 Tailored Media GmbH
 *
 * */
interface Navigator {

    companion object {
        const val EXTRA_ARG = "_args"
    }

    fun finishActivity()
    fun finishActivityAfterTransition()
    fun finishActivityWithResult(resultCode: Int, resultIntentFun: (Intent.() -> Unit)? = null)
    fun finishAffinity()

    fun startActivity(intent: Intent)
    fun startActivity(action: String, uri: Uri? = null)
    fun startActivity(activityClass: Class<out Activity>, adaptIntentFun: (Intent.() -> Unit)? = null)

    fun startActivityWithTransition(activityClass: Class<out Activity>, vararg transitionViews: Pair<View, String>, adaptIntentFun: (Intent.() -> Unit)? = null)
    fun startActivityWithTransition(activityClass: Class<out Activity>, vararg transitionViews: View, adaptIntentFun: (Intent.() -> Unit)? = null)

    fun startActivityForResult(activityClass: Class<out Activity>, requestCode: Int, adaptIntentFun: (Intent.() -> Unit)? = null)

    fun <T : DialogFragment> showDialogFragment(dialog: T, fragmentTag: String = dialog::class.java.name)

    fun replaceFragment(@IdRes containerId: Int, fragment: Fragment, fragmentTag: String? = null)
    fun replaceFragmentAndAddToBackStack(@IdRes containerId: Int, fragment: Fragment, fragmentTag: String? = null, backstackTag: String? = null)
    fun popFragmentBackStackImmediate()

}
