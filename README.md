# TA Android Template

This is the Tailored Apps Android app template which can be used for new projects. A small example app is available at the example branch.

The basic app structure is taken from the [Countries Example app](https://github.com/patloew/countries), which is Apache 2.0 licensed.

## Technologies used

* Kotlin
* Dagger 2 for dependency injection
* Retrofit/OkHttp/Gson for networking
* Realm for local data storage
* MVI as architectural pattern

## App structure

* Dagger components, modules, scopes and qualifiers for dependency injection are located in the `injection` package.
* Model classes are located in the `data.model` package.
* Network related classes and interfaces (e.g. Retrofit services) are located in the `data.remote` package.
* Local storage related classes (e.g. repositories) are located in the `data.local` package.
* UI related classes (Activities, Fragments, Views, ...) are located in the `ui` package. For each aspect of an app create a new subpackage. Use the interfaces and abstract classes from the `base` subpackage.
* Util classes are located in the `util` package.

## Dev hints

* Your bindings must have a variable named `vm`, with type of your ViewModel class. Do not put logic in your layouts, instead create a property in your view model.
** The view model exposes the State via the `state` variable.
* Your view classes (Activities, Fragments, ViewHolders) must implement `MvvmView` (usually, a new subinterface of `MvvmView`, specifying the callbacks for the specific view).
* Your ViewModel class should extend `BaseViewModel<MvvmView, State, PartialState>`.
    * The base classes provide basic view attach/detach methods. 
    * You can extend the `BaseValidator` class to validate the state.
    * Provide a state class that contains all data that the view needs (this is the Model in MVVM). This class should extend BaseObservable and provide variables with custom setters that call `notifyPropertyChanged()` for data binding.
    * Provide a partial state sealed class that implements `PartialState<State>`, which contains all the state changes that can occur. Each item in the sealed class must implement the `reduce(state)` function that adapts the state accordingly. 
* Base classes for views
    * These classes provide a binding and viewModel field. The viewModel gets injected, the binding has to be set manually.
    * Detaching of the view model is handled by the base classes.
    * Activities should extend `BaseActivity<Binding, ViewModel>`. You should use `setAndBindContentView(savedInstanceState, layoutResId)`, which sets the content view, creates the binding and attaches the view. Don’t forget to inject the viewModel via `activityComponent.inject(this)` before setting the content view.
    * Fragments should extend `BaseFragment<Binding, ViewModel>`. You should use `setAndBindContentView(inflater, container, savedInstanceState, layoutResId)`, which sets the content view, creates the binding and attaches the view. Don’t forget to inject the viewModel via `fragmentComponent.inject(this)` before setting the content view.
    * ViewHolders should extend BaseViewHolder<Binding, ViewModel>. You should use `bindContentView(view)`, which creates the binding and attaches the view. Don't forget to inject the viewModel via `viewHolderComponent.inject(this)` before binding the view.

## Testing

* Write unit tests for each view model. You can use Mockito/PowerMock to mock objects and verify correct behaviour. Add the `RxSchedulersOverrideRule` for preventing errors when using RxJava.
* Write UI tests for common actions in your app. Use JUnit 4 Tests with Espresso. Some helper methods are available in `EspressoUtils`.

## Recommended Reading

* [Reactive Apps with Model-View-Intent](http://hannesdorfmann.com/android/mosby3-mvi-1)
* [Using Retrofit with Realm and Parceler](https://nullpointer.wtf/android/using-retrofit-realm-parceler/)

# License

	Copyright 2017 Tailored Media GmbH

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	    http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.