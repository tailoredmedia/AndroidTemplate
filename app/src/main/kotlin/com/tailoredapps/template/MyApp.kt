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
import android.content.res.Resources
import com.squareup.leakcanary.LeakCanary
import com.tailoredapps.template.injection.components.AppComponent
import com.tailoredapps.template.injection.components.DaggerAppComponent
import com.tailoredapps.template.injection.modules.AppModule
import io.reactivex.plugins.RxJavaPlugins
import paperparcel.ProcessorConfig
import timber.log.Timber

@ProcessorConfig(adapters = [])
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return

        Timber.plant(Timber.DebugTree())

        instance = this
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()

        RxJavaPlugins.setErrorHandler({ Timber.e(it) })
    }

    companion object {

        lateinit var instance: MyApp
            private set

        lateinit var appComponent: AppComponent
            private set

        val res: Resources
            get() = instance.resources
    }
}
