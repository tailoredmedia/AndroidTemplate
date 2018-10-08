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

package com.tailoredapps.template

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.squareup.leakcanary.LeakCanary
import com.tailoredapps.core.injection.AppComponentProvides
import com.tailoredapps.core.injection.HasComponents
import com.tailoredapps.injection.components.AppComponent
import com.tailoredapps.injection.components.DaggerAppComponent
import com.tailoredapps.injection.components.DaggerActivityComponent
import com.tailoredapps.injection.modules.ActivityModule
import com.tailoredapps.injection.modules.AppModule
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber

class MyApp : Application(), HasComponents {

    override fun getActivityComponent(activity: AppCompatActivity) = DaggerActivityComponent.builder()
            .appComponent(appComponentProvides as AppComponent)
            .activityModule(ActivityModule(activity))
            .build()

    override val appComponentProvides: AppComponentProvides by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        Timber.plant(Timber.DebugTree())

        RxJavaPlugins.setErrorHandler { Timber.e(it) }
    }

}
