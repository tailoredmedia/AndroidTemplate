package com.tailoredapps.injection.components

import android.content.Context
import android.content.res.Resources
import com.squareup.leakcanary.RefWatcher
import com.tailoredapps.core.feedback.Toaster
import com.tailoredapps.core.injection.qualifier.AppContext
import com.tailoredapps.core.injection.scopes.PerApplication
import com.tailoredapps.injection.modules.AppModule
import com.tailoredapps.injection.modules.DataModule
import com.tailoredapps.injection.modules.NetModule
import com.tailoredapps.injection.modules.ViewModelModule
import com.tailoredapps.template.ui.main.MainActivity
import dagger.Component

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
 * FILE MODIFIED 2017 Tailored Media GmbH */
@PerApplication
@Component(modules = [AppModule::class, NetModule::class, DataModule::class, ViewModelModule::class])
interface AppComponent : AppComponentProvides {
    fun inject(activity: MainActivity)
}


interface AppComponentProvides {
    @AppContext fun appContext(): Context
    fun resources(): Resources
    fun refWatcher(): RefWatcher

    /*fun encryptionKeyManager(): EncryptionKeyManager

    fun prefRepo(): PrefRepo
    fun myApi(): MyApi*/

    fun toaster(): Toaster
}


interface HasAppComponent {
    val appComponent: AppComponent
}