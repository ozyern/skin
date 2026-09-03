package com.ozyern.skin.search.algorithms.engine

import android.content.Context

interface SearchPermission {
    fun checkPermission(context: Context): Any
}
