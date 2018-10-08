package com.tailoredapps.core.injection

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.tailoredapps.core.injection.qualifier.ActivityContext
import com.tailoredapps.core.injection.qualifier.ActivityDisposable
import com.tailoredapps.core.injection.qualifier.ActivityFragmentManager
import io.reactivex.disposables.CompositeDisposable

interface ActivityComponentProvides : AppComponentProvides {
    @ActivityContext fun activityContext(): Context
    @ActivityFragmentManager fun defaultFragmentManager(): FragmentManager
    @ActivityDisposable fun activityDisposable(): CompositeDisposable
}