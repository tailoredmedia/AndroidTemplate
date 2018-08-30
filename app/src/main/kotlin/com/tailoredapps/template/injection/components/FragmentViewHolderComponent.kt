package com.tailoredapps.template.injection.components

import com.tailoredapps.template.injection.modules.ViewHolderModule
import com.tailoredapps.template.injection.modules.ViewModelModule
import com.tailoredapps.template.injection.qualifier.ViewHolderDisposable
import com.tailoredapps.template.injection.scopes.PerViewHolder
import dagger.Component
import io.reactivex.disposables.CompositeDisposable

/* Copyright 2017 Tailored Media GmbH
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
@PerViewHolder
@Component(dependencies = [(FragmentComponent::class)], modules = [(ViewHolderModule::class), (ViewModelModule::class)])
interface FragmentViewHolderComponent {
    // create inject methods for your Fragment ViewHolder here

}

interface FragmentViewHolderComponentProvides {

    @ViewHolderDisposable fun viewHolderDisposable(): CompositeDisposable

}