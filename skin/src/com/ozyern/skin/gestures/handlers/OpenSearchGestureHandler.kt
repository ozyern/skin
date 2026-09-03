package com.ozyern.skin.gestures.handlers

import android.content.Context
import com.ozyern.skin.SkinLauncher
import com.ozyern.skin.preferences2.PreferenceManager2
import com.ozyern.skin.qsb.SkinQsbLayout

class OpenSearchGestureHandler(context: Context) : GestureHandler(context) {

    override suspend fun onTrigger(launcher: SkinLauncher) {
        val prefs = PreferenceManager2.getInstance(launcher)
        val searchProvider = SkinQsbLayout.getSearchProvider(launcher, prefs)
        searchProvider.launch(launcher)
    }
}
