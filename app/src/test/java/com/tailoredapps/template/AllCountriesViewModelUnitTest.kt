package com.tailoredapps.template

import android.support.v7.widget.RecyclerView
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.data.model.Country
import com.tailoredapps.template.data.remote.CountryApi
import com.tailoredapps.template.ui.main.recyclerview.CountryAdapter
import com.tailoredapps.template.ui.main.viewpager.CountriesView
import com.tailoredapps.template.ui.main.viewpager.all.AllCountriesViewModel
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.times
import org.mockito.Mockito.verifyZeroInteractions
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.util.*

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

@RunWith(PowerMockRunner::class)
@PrepareOnlyThisForTest(CountryAdapter::class, RecyclerView.Adapter::class)
class AllCountriesViewModelUnitTest {

    @Rule val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    val countryApi = mock<CountryApi>()
    val countryRepo = mock<CountryRepo> {
        on { favoriteChangeObservable } doReturn Observable.never<String>()
    }
    val countriesView = mock<CountriesView>()

    lateinit var allCountriesViewModel :AllCountriesViewModel
    lateinit var adapter: CountryAdapter

    @Before
    fun setup() {
        adapter = PowerMockito.mock(CountryAdapter::class.java)
        PowerMockito.doAnswer { null }.`when`(adapter as RecyclerView.Adapter<*>).notifyDataSetChanged()
        allCountriesViewModel = AllCountriesViewModel(adapter, countryApi, countryRepo)
        allCountriesViewModel.attachView(countriesView, null)
    }

    @Test
    fun onRefresh_success() {
        val countryList = ArrayList<Country>()
        countryList.add(Country())

        whenever(countryApi.getAllCountries()) doReturn Single.just<List<Country>>(countryList)

        allCountriesViewModel.reloadData()

        verify(adapter, times(1)).countryList = countryList
        verify(adapter, times(1)).notifyDataSetChanged()
        verify(countriesView, times(1)).onRefresh(true)
    }

    @Test
    fun onRefresh_error() {
        whenever(countryApi.getAllCountries()) doReturn Single.error<List<Country>>(RuntimeException("Error getting countries"))

        allCountriesViewModel.reloadData()

        verifyZeroInteractions(adapter)
        verify(countriesView, times(1)).onRefresh(false)
    }
}
