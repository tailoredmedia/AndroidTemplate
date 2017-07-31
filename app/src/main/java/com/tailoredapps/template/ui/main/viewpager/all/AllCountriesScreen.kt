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


package com.tailoredapps.template.ui.main.viewpager.all

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tailoredapps.template.R
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.data.remote.CountryApi
import com.tailoredapps.template.injection.scopes.PerFragment
import com.tailoredapps.template.ui.base.viewmodel.AdapterMvvmViewModel
import com.tailoredapps.template.ui.base.viewmodel.BaseViewModel
import com.tailoredapps.template.ui.main.recyclerview.CountryAdapter
import com.tailoredapps.template.ui.main.viewpager.CountriesFragment
import com.tailoredapps.template.ui.main.viewpager.CountriesView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.*
import javax.inject.Inject


interface IAllCountriesViewModel : AdapterMvvmViewModel<CountriesView> {
    fun reloadData()
}


class AllCountriesFragment : CountriesFragment<IAllCountriesViewModel>(), CountriesView {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentComponent.inject(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.reloadData() }
        if (savedInstanceState == null) { binding.swipeRefreshLayout.isRefreshing = true }
        viewModel.reloadData()
    }


    // View

    override fun onRefresh(success: Boolean) {
        super.onRefresh(success)

        if (!success) {
            Snackbar.make(binding.recyclerView, "Could not load countries", Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snackbar_action_retry) { _ -> viewModel.reloadData() }
                    .show()
        }
    }
}


@PerFragment
class AllCountriesViewModel
@Inject
constructor(override val adapter: CountryAdapter, private val countryApi: CountryApi, private val countryRepo: CountryRepo) : BaseViewModel<CountriesView>(), IAllCountriesViewModel {

    private val compositeDisposable = CompositeDisposable()

    override fun attachView(view: CountriesView, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)

        compositeDisposable.add(
                countryRepo.favoriteChangeObservable
                        .subscribe({ adapter.notifyDataSetChanged() }, { Timber.e(it) })
        )
    }

    override fun detachView() {
        super.detachView()
        compositeDisposable.clear()
    }

    override fun reloadData() {
        compositeDisposable.add(countryApi.getAllCountries()
                .doOnSuccess({ Collections.sort(it) })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    adapter.countryList = it
                    adapter.notifyDataSetChanged()
                    view?.onRefresh(true)
                }, {
                    Timber.e(it, "Could not load countries")
                    view?.onRefresh(false)
                })
        )
    }
}