package com.tailoredapps.core.injection

import android.content.Context
import android.content.res.Resources
import com.squareup.leakcanary.RefWatcher
import com.tailoredapps.core.feedback.Toaster
import com.tailoredapps.core.injection.qualifier.AppContext

interface AppComponentProvides {
    @AppContext fun appContext(): Context
    fun resources(): Resources
    fun refWatcher(): RefWatcher

    /*fun encryptionKeyManager(): EncryptionKeyManager

    fun prefRepo(): PrefRepo
    fun myApi(): MyApi*/

    fun toaster(): Toaster
}