package com.tailoredapps.template

import android.support.v7.widget.RecyclerView
import com.nhaarman.mockito_kotlin.*
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.data.model.Country
import com.tailoredapps.template.ui.main.recyclerview.CountryAdapter
import com.tailoredapps.template.ui.main.viewpager.CountriesView
import com.tailoredapps.template.ui.main.viewpager.favorites.FavoriteCountriesViewModel
import io.reactivex.Observable
import io.realm.RealmResults
import io.realm.Sort
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
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
@PrepareForTest(RealmResults::class, CountryAdapter::class, RecyclerView.Adapter::class)
class FavoriteCountriesViewModelUnitTest {

    @Rule val rxSchedulersOverrideRule = RxSchedulersOverrideRule()

    val countryRepo = mock<CountryRepo>()
    val adapter = PowerMockito.mock(CountryAdapter::class.java)

    val mainActivityView = mock<CountriesView>()
    val favoriteCountriesViewModel = FavoriteCountriesViewModel(adapter, countryRepo)

    val countryList = ArrayList<Country>(0)

    init {
        PowerMockito.doAnswer { null }.`when`(adapter as RecyclerView.Adapter<*>).notifyDataSetChanged()
    }

    @Test
    fun onRealmChangeListener_threeTimes() {
        whenever(countryRepo.findAllSortedWithChanges(any<String>(), any<Sort>())) doReturn
                Observable.just<List<Country>>(countryList, countryList, countryList)

        favoriteCountriesViewModel.attachView(mainActivityView, null)

        verify(adapter, times(3)).countryList = countryList
        verify(adapter, times(3)).notifyDataSetChanged()
        verify(mainActivityView, times(3)).onRefresh(true)

        favoriteCountriesViewModel.detachView()
    }

    @Test
    fun onRealmChangeListener_never() {
        whenever(countryRepo.findAllSortedWithChanges(any<String>(), any<Sort>())) doReturn
                Observable.empty<List<Country>>()

        favoriteCountriesViewModel.attachView(mainActivityView, null)

        verify(adapter, never()).countryList = countryList
        verify(adapter, never()).notifyDataSetChanged()
        verify(mainActivityView, never()).onRefresh(true)

        favoriteCountriesViewModel.detachView()
    }
}
