package com.ozyern.skin.smartspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.ozyern.skin.ui.preferences.PreferenceActivity
import com.ozyern.skin.ui.preferences.navigation.SmartspaceWidget

class SmartspacePreferencesShortcut : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(PreferenceActivity.createIntent(this, SmartspaceWidget))
        finish()
    }
}
