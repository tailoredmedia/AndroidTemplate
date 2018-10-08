package com.tailoredapps.injection.modules

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.tailoredapps.core.feedback.ActivitySnacker
import com.tailoredapps.core.feedback.Snacker
import com.tailoredapps.core.injection.qualifier.ActivityContext
import com.tailoredapps.core.injection.qualifier.ActivityDisposable
import com.tailoredapps.core.injection.qualifier.ActivityFragmentManager
import com.tailoredapps.core.injection.scopes.PerActivity
import com.tailoredapps.uibase.navigator.ActivityNavigator
import com.tailoredapps.uibase.navigator.Navigator
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

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
@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @Provides
    @PerActivity
    @ActivityContext
    internal fun provideActivityContext(): Context = activity

    @Provides
    @PerActivity
    @ActivityFragmentManager
    internal fun provideFragmentManager(): FragmentManager = activity.supportFragmentManager

    @Provides
    @PerActivity
    @ActivityDisposable
    internal fun provideActivityCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @PerActivity
    internal fun provideNavigator(): Navigator = ActivityNavigator(activity)

    @Provides
    @PerActivity
    internal fun provideSnacker(): Snacker = ActivitySnacker(activity)

    @Provides
    @PerActivity
    internal fun prrovideActivity(): Activity = activity

}
