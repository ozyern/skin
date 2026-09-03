package com.ozyern.skin.ui.preferences.components.colorpreference

import android.content.Context
import androidx.compose.runtime.Composable
import com.ozyern.skin.ui.theme.lightenColor

class ColorPreferenceEntry<T>(
    val value: T,
    val label: @Composable () -> String,
    val lightColor: (Context) -> Int,
    val darkColor: (Context) -> Int = { context -> lightenColor(lightColor(context)) },
)
