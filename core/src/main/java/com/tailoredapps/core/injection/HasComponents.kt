package com.tailoredapps.core.injection

import androidx.appcompat.app.AppCompatActivity

interface HasComponents {
    val appComponentProvides: AppComponentProvides

    fun getActivityComponent(activity: AppCompatActivity): ActivityComponentProvides
}