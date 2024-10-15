package com.kissspace.util

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
val FragmentActivity.windowInsetsCompat: WindowInsetsCompat?
    get() = ViewCompat.getRootWindowInsets(findViewById(android.R.id.content))

fun FragmentActivity.getNavigationBarsHeight(): Int {
    val windowInsetsCompat = windowInsetsCompat ?: return 0
    return windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
}