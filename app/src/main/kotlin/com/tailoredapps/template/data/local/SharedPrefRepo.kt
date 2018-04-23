package com.tailoredapps.template.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Base64

import com.tailoredapps.template.injection.qualifier.AppContext
import com.tailoredapps.template.injection.scopes.PerApplication

import javax.inject.Inject

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
@PerApplication
class SharedPrefRepo
@Inject
constructor(@AppContext context: Context) : PrefRepo {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override var encryptionKey: ByteArray?
        get() {
            if (prefs.contains(ENCRYPTION_KEY)) {
                return Base64.decode(prefs.getString(ENCRYPTION_KEY, null), Base64.DEFAULT)
            } else {
                return null
            }
        }
        set(key) {
            if(key == null) {
                prefs.edit().remove(ENCRYPTION_KEY).apply()
            } else {
                prefs.edit().putString(ENCRYPTION_KEY, Base64.encodeToString(key, Base64.DEFAULT)).apply()
            }
        }

    private var encryptionKeySuffix: Int
        get() = prefs.getInt(ENCRYPTION_KEY_SUFFIX, 0)
        set(value) = prefs.edit().putInt(ENCRYPTION_KEY_SUFFIX, value).apply()


    override val encryptionKeyAlias: String get() = ENCRYPTION_KEY_ALIAS + encryptionKeySuffix

    override fun changeEncryptionKeySuffix() {
        encryptionKeySuffix += 1
    }

    companion object {
        private const val ENCRYPTION_KEY_ALIAS = "mainKey"

        private const val ENCRYPTION_KEY = "encryption_key"
        private const val ENCRYPTION_KEY_SUFFIX = "encryption_key_suffix"
    }

}