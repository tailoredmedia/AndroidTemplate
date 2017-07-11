package com.tailoredapps.template

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import com.nhaarman.mockito_kotlin.*
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.data.model.Country
import com.tailoredapps.template.ui.base.navigator.Navigator
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.ui.main.recyclerview.CountryViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.reflect.Whitebox

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
 * FILE MODIFIED 2017 Tailored Media GmbH */

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(Uri::class)
class BaseCountryViewModelUnitTest {

    @Rule val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    val packageManager: PackageManager = mock {
        val nullPackageInfo: PackageInfo? = null
        on { getPackageInfo(any<String>(), any<Int>()) }.doReturn(nullPackageInfo)
    }

    val ctx: Context = mock {
        on { applicationContext } doReturn it
        on { packageManager } doReturn packageManager
    }

    val countryRepo = mock<CountryRepo>()
    val mvvmView = mock<MvvmView>()
    val navigator = mock<Navigator>()

    lateinit var countryViewModel: CountryViewModel

    val internalCountry = Country()

    @Before
    @Throws(PackageManager.NameNotFoundException::class)
    fun setup() {
        countryViewModel = CountryViewModel(ctx, navigator, countryRepo)
        countryViewModel.attachView(mvvmView, null)
        Whitebox.setInternalState(countryViewModel, "country", internalCountry)
    }

    @Test
    fun onMapClick_startActivity() {
        val uri = Mockito.mock(Uri::class.java)
        PowerMockito.mockStatic(Uri::class.java) { uri }
        countryViewModel.onMapClick()
        verify(navigator).startActivity(com.nhaarman.mockito_kotlin.eq(Intent.ACTION_VIEW), eq(uri))
    }

    @Test
    fun onBookmarkClick_wasBookmarked() {
        val country = Country()
        whenever(countryRepo.getByField(any<String>(), anyOrNull(), any<Boolean>())) doReturn country
        whenever(countryRepo.detach(country)) doReturn country

        countryViewModel.onBookmarkClick()
        verify(countryRepo).delete(country)
    }

    @Test
    fun onBookmarkClick_wasNotBookmarked() {
        val nullCountry: Country? = null
        whenever(countryRepo.getByField(any<String>(), any<String>(), any<Boolean>())).doReturn(nullCountry)

        countryViewModel.onBookmarkClick()
        verify(countryRepo).save(internalCountry)
    }
}
