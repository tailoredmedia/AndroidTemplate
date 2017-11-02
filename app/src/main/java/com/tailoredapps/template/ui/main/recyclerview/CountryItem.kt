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
 * -------
 *
 * FILE MODIFIED 2017 Tailored Media GmbH */


package com.tailoredapps.template.ui.main.recyclerview

import android.content.Context
import android.view.View
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.databinding.CardCountryBinding
import com.tailoredapps.template.injection.qualifier.AppContext
import com.tailoredapps.template.injection.scopes.PerViewHolder
import com.tailoredapps.template.ui.BaseCountryViewModel
import com.tailoredapps.template.ui.ICountryViewModel
import com.tailoredapps.template.ui.base.BaseActivityViewHolder
import com.tailoredapps.template.ui.base.navigator.Navigator
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.ui.detail.DetailActivity
import javax.inject.Inject

interface CountryMvvm {

    interface ViewModel : ICountryViewModel<MvvmView> {
        fun onCardClick()
    }
}


class CountryViewHolder(v: View) : BaseActivityViewHolder<CardCountryBinding, CountryMvvm.ViewModel>(v), MvvmView {

    init {
        viewHolderComponent.inject(this)
        bindContentView(v)
    }
}


@PerViewHolder
class CountryViewModel
@Inject
constructor(@AppContext context: Context, navigator: Navigator, countryRepo: CountryRepo) : BaseCountryViewModel<MvvmView>(context, countryRepo, navigator), CountryMvvm.ViewModel {

    override fun onCardClick() {
        navigator.startActivity(DetailActivity::class.java, countryRepo.detach(country))
    }

}
