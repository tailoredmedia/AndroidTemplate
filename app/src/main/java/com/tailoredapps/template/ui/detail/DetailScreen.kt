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

package com.tailoredapps.template.ui.detail

import android.content.Context
import android.databinding.Bindable
import android.databinding.Observable
import android.os.Build
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.tailoredapps.template.BR
import com.tailoredapps.template.R
import com.tailoredapps.template.data.local.CountryRepo
import com.tailoredapps.template.data.model.Country
import com.tailoredapps.template.data.remote.CountryApi
import com.tailoredapps.template.databinding.ActivityDetailBinding
import com.tailoredapps.template.injection.qualifier.AppContext
import com.tailoredapps.template.injection.scopes.PerActivity
import com.tailoredapps.template.ui.BaseCountryViewModel
import com.tailoredapps.template.ui.ICountryViewModel
import com.tailoredapps.template.ui.base.BaseActivity
import com.tailoredapps.template.ui.base.navigator.Navigator
import com.tailoredapps.template.ui.base.view.MvvmView
import com.tailoredapps.template.util.kotlin.NotifyPropertyChangedDelegate
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.reflect.KMutableProperty0

interface DetailMvvm {

    interface View : MvvmView

    interface ViewModel : ICountryViewModel<View> {
        // Properties

        @get:Bindable
        val isLoaded: Boolean
        @get:Bindable
        val borders: CharSequence?
        @get:Bindable
        val currencies: CharSequence?
        @get:Bindable
        val languages: CharSequence?
        @get:Bindable
        val nameTranslations: CharSequence?
    }
}


class DetailActivity : BaseActivity<ActivityDetailBinding, DetailMvvm.ViewModel>(), DetailMvvm.View {

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.inject(this)
        setAndBindContentView(savedInstanceState, R.layout.activity_detail)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setTitle(R.string.toolbar_title_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.update(intent.getParcelableExtra(Navigator.EXTRA_ARG), false)

        viewModel.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(observable: Observable, propertyId: Int) {
                if (propertyId == BR.bookmarkDrawable && menu != null) {
                    val favoriteItem = menu!!.findItem(R.id.menu_item_favorite)
                    favoriteItem.icon = viewModel.bookmarkDrawable
                    tintMenuIcon(favoriteItem)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        this.menu = menu
        val favoriteItem = menu.findItem(R.id.menu_item_favorite)
        val mapItem = menu.findItem(R.id.menu_item_maps)
        favoriteItem.icon = viewModel.bookmarkDrawable
        tintMenuIcon(favoriteItem)
        tintMenuIcon(mapItem)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_item_favorite -> viewModel.onBookmarkClick()
            R.id.menu_item_maps -> viewModel.onMapClick()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun tintMenuIcon(menuItem: MenuItem) {
        val favoriteIcon = DrawableCompat.wrap(menuItem.icon.mutate())
        DrawableCompat.setTint(favoriteIcon, 0xFFFFFFFF.toInt())
        menuItem.icon = favoriteIcon
    }
}



@PerActivity
class DetailViewModel
@Inject
constructor(@AppContext context: Context, countryRepo: CountryRepo, private val countryApi: CountryApi, navigator: Navigator) : BaseCountryViewModel<DetailMvvm.View>(context, countryRepo, navigator), DetailMvvm.ViewModel {

    companion object {
        private const val KEY_BORDER_LIST = "borderList"
    }

    private val compositeDisposable = CompositeDisposable()

    @get:Bindable
    override var borders : CharSequence? by NotifyPropertyChangedDelegate(null, BR.borders)
        private set
    @get:Bindable
    override var currencies : CharSequence? by NotifyPropertyChangedDelegate(null, BR.currencies)
        private set
    @get:Bindable
    override var languages : CharSequence? by NotifyPropertyChangedDelegate(null, BR.languages)
        private set
    @get:Bindable
    override var nameTranslations : CharSequence? by NotifyPropertyChangedDelegate(null, BR.nameTranslations)
        private set
    @get:Bindable
    override var isLoaded : Boolean by NotifyPropertyChangedDelegate(false, BR.loaded)
        private set

    override fun saveInstanceState(outState: Bundle?) {
        outState?.putCharSequence(KEY_BORDER_LIST, borders)
    }

    public override fun restoreInstanceState(savedInstanceState: Bundle) {
        if (savedInstanceState.containsKey(KEY_BORDER_LIST)) {
            borders = savedInstanceState.getCharSequence(KEY_BORDER_LIST)
        }
    }

    override fun detachView() {
        super.detachView()
        compositeDisposable.clear()
    }

    override fun update(country: Country, isLast: Boolean) {
        super.update(country, isLast)

        compositeDisposable.clear()
        loadBorders()
        loadDataForField(this::nameTranslations, { this.calculateNameTranslations() })
        loadDataForField(this::currencies, { this.calculateCurrencies() })
        loadDataForField(this::languages, { this.calculateLanguages() })
    }

    private fun <T> loadDataForField(setter: KMutableProperty0<T?>, producer: () -> T) {
        compositeDisposable.add(
                Single.fromCallable(producer)
                        .subscribeOn(Schedulers.computation())
                        .subscribe({ setter.set(it) }, { throwable ->
                            Timber.e(throwable, "Could not load data for field")
                            setter.set(null)
                        })
        )
    }

    private fun calculateLanguages(): CharSequence {
        val languageList = ArrayList<String>(country.languages!!.size)

        for (language in country.languages!!) {
            languageList.add(Locale(language.value).getDisplayLanguage(BaseCountryViewModel.Companion.DISPLAY_LOCALE))
        }

        Collections.sort(languageList)

        return SpannableStringBuilder(ctx.getText(R.string.country_languages)).append(TextUtils.join(", ", languageList))
    }

    private fun calculateNameTranslations(): CharSequence {
        val nameList = ArrayList<String>(country.translations!!.size)

        for (entry in country.translations!!) {
            nameList.add(entry.value + " <i>(" + Locale(entry.key).getDisplayLanguage(BaseCountryViewModel.Companion.DISPLAY_LOCALE) + ")</i>")
        }

        Collections.sort(nameList)

        return SpannableStringBuilder(ctx.getText(R.string.country_name_translations)).append(Html.fromHtml(TextUtils.join(", ", nameList)))
    }

    private fun calculateCurrencies(): CharSequence {
        val currenciesList = ArrayList<String>(country.currencies!!.size)

        for (currencyRealmString in country.currencies!!) {
            val currencyString = currencyRealmString.value!!
            if (Build.VERSION.SDK_INT >= 19) {
                try {
                    val currency = Currency.getInstance(currencyString)
                    var currencySymbol = currency.symbol
                    if (currencyString != currencySymbol) {
                        currencySymbol = currencyString + ", " + currencySymbol
                    }
                    currenciesList.add(currency.getDisplayName(BaseCountryViewModel.Companion.DISPLAY_LOCALE) + " (" + currencySymbol + ")")
                } catch (ignore: IllegalArgumentException) {
                    currenciesList.add(currencyString)
                }

            } else {
                currenciesList.add(currencyString)
            }
        }

        Collections.sort(currenciesList)

        return SpannableStringBuilder(ctx.getText(R.string.country_currencies)).append(TextUtils.join(", ", currenciesList))
    }

    private fun loadBorders() {
        borders = null

        if (country.borders!!.size > 0) {
            compositeDisposable.add(
                    countryApi.getAllCountries()
                            .subscribe({ this.calculateBorders(it) },{ this.onLoadCountriesError(it) })
            )

        } else {
            isLoaded = true
        }
    }

    private fun calculateBorders(countryList: List<Country>?) {
        if (countryList != null) {
            val borderList = ArrayList<String?>(country.borders!!.size)
            val alpha3List = ArrayList<String?>(country.borders!!.size)

            for (borderAlpha3CodeString in country.borders!!) {
                alpha3List.add(borderAlpha3CodeString.value)
            }

            for (c in countryList) {
                if (alpha3List.contains(c.alpha3Code)) {
                    borderList.add(c.name)
                    alpha3List.remove(c.alpha3Code)
                    if (alpha3List.isEmpty()) {
                        break
                    }
                }
            }

            borders = SpannableStringBuilder(ctx.getText(R.string.country_borders)).append(TextUtils.join(", ", borderList))
        }

        isLoaded = true
    }

    private fun onLoadCountriesError(throwable: Throwable) {
        Timber.e(throwable, "Could not load countries")
        isLoaded = true
    }

}
