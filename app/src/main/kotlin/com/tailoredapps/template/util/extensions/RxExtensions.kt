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

package com.tailoredapps.template.util.extensions

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject

typealias TriggerProcessor = PublishProcessor<Unit>
typealias TriggerSubject = PublishSubject<Unit>
typealias TriggerFlowable = Flowable<Unit>
typealias TriggerObservable = Observable<Unit>
inline fun <T> Flowable<T>.subscribeIgnoreElements(crossinline subscribeFun: () -> Unit) = subscribe { subscribeFun() }
inline fun <T> Observable<T>.subscribeIgnoreElements(crossinline subscribeFun: () -> Unit) = subscribe { subscribeFun() }
inline fun <T> Flowable<T>.subscribeIgnoreElements(crossinline subscribeFun: () -> Unit, crossinline errorFun: (Throwable) -> Unit) = subscribe({ subscribeFun() }, { errorFun(it) })
inline fun <T> Observable<T>.subscribeIgnoreElements(crossinline subscribeFun: () -> Unit, crossinline errorFun: (Throwable) -> Unit) = subscribe({ subscribeFun() }, { errorFun(it) })
inline fun <T> Flowable<T>.subscribeIgnoreElements(crossinline subscribeFun: () -> Unit, crossinline errorFun: (Throwable) -> Unit, crossinline completeFun: () -> Unit) = subscribe({ subscribeFun() }, { errorFun(it) }, { completeFun() })
inline fun <T> Observable<T>.subscribeIgnoreElements(crossinline subscribeFun: () -> Unit, crossinline errorFun: (Throwable) -> Unit, crossinline completeFun: () -> Unit) = subscribe({ subscribeFun() }, { errorFun(it) }, { completeFun() })
fun PublishSubject<Unit>.onNext() = onNext(Unit)
fun PublishProcessor<Unit>.onNext() = onNext(Unit)
