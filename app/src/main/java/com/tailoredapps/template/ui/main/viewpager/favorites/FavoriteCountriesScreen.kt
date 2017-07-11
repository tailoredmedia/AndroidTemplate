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

package com.tailoredapps.template.ui.main.viewpager.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.data.model.Country
import com.tailoredapps.template.injection.scopes.PerFragment
import com.tailoredapps.template.ui.base.viewmodel.AdapterMvvmViewModel
import com.tailoredapps.template.ui.base.viewmodel.BaseViewModel
import com.tailoredapps.template.ui.main.recyclerview.CountryAdapter
import com.tailoredapps.template.ui.main.viewpager.CountriesFragment
import com.tailoredapps.template.ui.main.viewpager.CountriesView
import io.reactivex.disposables.Disposable
import io.realm.Sort
import timber.log.Timber
import javax.inject.Inject

interface IFavoriteCountriesViewModel : AdapterMvvmViewModel<CountriesView>



class FavoriteCountriesFragment : CountriesFragment<IFavoriteCountriesViewModel>() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentComponent.inject(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeRefreshLayout.setOnRefreshListener { binding.swipeRefreshLayout.isRefreshing = false }
    }
}



@PerFragment
class FavoriteCountriesViewModel
@Inject
constructor(override val adapter: CountryAdapter, private val countryRepo: CountryRepo) : BaseViewModel<CountriesView>(), IFavoriteCountriesViewModel {
    private var disposable: Disposable? = null

    override fun attachView(view: CountriesView, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)

        disposable = countryRepo.findAllSortedWithChanges("name", Sort.ASCENDING)
                .subscribe({ refreshView(it) }, { Timber.e(it) })
    }

    private fun refreshView(countryList: List<Country>) {
        adapter.countryList = countryList
        adapter.notifyDataSetChanged()
        view?.onRefresh(true)
    }

    override fun detachView() {
        super.detachView()

        if (disposable != null) {
            disposable!!.dispose()
            disposable = null
        }
    }
}

