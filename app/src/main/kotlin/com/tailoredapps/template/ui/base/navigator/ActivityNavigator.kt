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
 *
 * ------
 *
 * FILE CHANGED 2017 Tailored Media GmbH
 *
 */

package com.tailoredapps.template.ui.base.navigator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager


open class ActivityNavigator(protected val activity: FragmentActivity) : Navigator {

    protected open val fragmentManager
        get() = activity.supportFragmentManager

    override fun finishActivity() {
        activity.finish()
    }

    override fun finishActivityAfterTransition() {
        activity.setResult(Activity.RESULT_OK)
        activity.supportFinishAfterTransition()
    }

    override fun finishActivityWithResult(resultCode: Int, resultIntentFun: (Intent.() -> Unit)?) {
        val intent = resultIntentFun?.let { Intent().apply(it) }
        activity.setResult(resultCode, intent)
        activity.finish()
    }

    override fun finishAffinity() {
        activity.finishAffinity()
    }

    override fun startActivity(intent: Intent) {
        startActivityInternal(intent)
    }

    override fun startActivity(action: String, uri: Uri?) {
        startActivityInternal(Intent(action, uri))
    }

    override fun startActivity(activityClass: Class<out Activity>) {
        startActivityInternal(activityClass, null)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        startActivityInternal(intent, requestCode)
    }

    override fun startActivityForResult(activityClass: Class<out Activity>, requestCode: Int) {
        startActivityInternal(activityClass, requestCode)
    }

    override fun startActivityWithTransition(activityClass: Class<out Activity>, vararg transitionViews: Pair<View, String>) {
        startActivityWithTransitionInternal(Intent(activity, activityClass), transitionViews)
    }

    override fun startActivityWithTransition(activityClass: Class<out Activity>, vararg transitionViews: View) {
        startActivityWithTransition(Intent(activity, activityClass), *transitionViews)
    }

    override fun startActivityWithTransition(intent: Intent, vararg transitionViews: Pair<View, String>) {
        startActivityWithTransitionInternal(intent, transitionViews)
    }

    override fun startActivityWithTransition(intent: Intent, vararg transitionViews: View) {
        val mapped = transitionViews.map {
            val transitionName = ViewCompat.getTransitionName(it) ?: throw IllegalArgumentException("View with ID \"${it.resources.getResourceEntryName(it.id)}\" must have a transitionName")
            Pair(it, transitionName)
        }.toTypedArray()

        startActivityWithTransitionInternal(intent, mapped)
    }

    private fun startActivityWithTransitionInternal(intent: Intent, transitionViews: Array<out Pair<View, String>>) {
        val mapped = transitionViews
                .map { androidx.core.util.Pair(it.first, it.second) }
                .toTypedArray()
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *mapped).toBundle()

        startActivityInternal(intent, null, options)
    }

    private fun startActivityInternal(activityClass: Class<out Activity>, requestCode: Int?) {
        val intent = Intent(activity, activityClass)
        startActivityInternal(intent, requestCode)
    }

    protected open fun startActivityInternal(intent: Intent, requestCode: Int? = null, options: Bundle? = null) {
        if (requestCode != null) {
            ActivityCompat.startActivityForResult(activity, intent, requestCode, options)
        } else {
            activity.startActivity(intent, options)
        }
    }

    override fun replaceFragment(@IdRes containerId: Int, fragment: Fragment, fragmentTag: String?) {
        replaceFragmentInternal(fragmentManager, containerId, fragment, fragmentTag, false, null)
    }

    override fun replaceFragmentAndAddToBackStack(@IdRes containerId: Int, fragment: Fragment, fragmentTag: String?, backstackTag: String?) {
        replaceFragmentInternal(fragmentManager, containerId, fragment, fragmentTag, true, backstackTag)
    }

    protected fun replaceFragmentInternal(fm: FragmentManager, @IdRes containerId: Int, fragment: Fragment, fragmentTag: String?, addToBackstack: Boolean, backstackTag: String?) {
        val ft = fm.beginTransaction().replace(containerId, fragment, fragmentTag)
        if (addToBackstack) {
            ft.addToBackStack(backstackTag).commit()
            fm.executePendingTransactions()
        } else {
            ft.commitNow()
        }
    }

    override fun <T : DialogFragment> showDialogFragment(dialog: T, fragmentTag: String) {
        dialog.show(fragmentManager, fragmentTag)
    }

    override fun popFragmentBackStackImmediate() {
        fragmentManager.popBackStackImmediate()
    }
}
