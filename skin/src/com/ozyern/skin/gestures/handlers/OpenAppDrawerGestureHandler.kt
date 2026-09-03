package com.ozyern.skin.gestures.handlers

import android.content.Context
import com.ozyern.skin.SkinLauncher
import com.ozyern.skin.animateToAllApps

class OpenAppDrawerGestureHandler(context: Context) : GestureHandler(context) {

    override suspend fun onTrigger(launcher: SkinLauncher) {
        launcher.animateToAllApps()
    }
}
