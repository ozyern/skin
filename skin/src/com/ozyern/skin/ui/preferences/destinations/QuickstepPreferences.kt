package com.ozyern.skin.ui.preferences.destinations

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ozyern.skin.SkinApp
import com.ozyern.skin.preferences.getAdapter
import com.ozyern.skin.preferences.observeAsState
import com.ozyern.skin.preferences.preferenceManager
import com.ozyern.skin.preferences2.preferenceManager2
import com.ozyern.skin.ui.preferences.components.QuickActionsPreferences
import com.ozyern.skin.ui.preferences.components.RecentsQuickAction
import com.ozyern.skin.ui.preferences.components.controls.SliderPreference
import com.ozyern.skin.ui.preferences.components.controls.SwitchPreference
import com.ozyern.skin.ui.preferences.components.controls.WarningPreference
import com.ozyern.skin.ui.preferences.components.layout.ExpandAndShrink
import com.ozyern.skin.ui.preferences.components.layout.PreferenceGroup
import com.ozyern.skin.ui.preferences.components.layout.PreferenceLayout
import com.ozyern.skin.ui.theme.SkinTheme
import com.ozyern.skin.ui.util.preview.PreferenceGroupPreviewContainer
import com.ozyern.skin.ui.util.preview.PreviewSkin
import com.ozyern.skin.util.isOnePlusStock
import com.android.launcher3.R
import com.android.launcher3.Utilities

@Composable
fun QuickstepPreferences(
    modifier: Modifier = Modifier,
) {
    val prefs = preferenceManager()
    val prefs2 = preferenceManager2()
    val context = LocalContext.current
    val lensAvailable = remember {
        context.packageManager.getLaunchIntentForPackage("com.google.ar.lens") != null
    }

    val recentActionsPreferences: List<RecentsQuickAction> = listOfNotNull(
        RecentsQuickAction(
            id = 0,
            adapter = prefs.recentsActionScreenshot.getAdapter(),
            label = stringResource(id = R.string.action_screenshot),
        ).takeIf { !isOnePlusStock },
        RecentsQuickAction(
            id = 1,
            adapter = prefs.recentsActionShare.getAdapter(),
            label = stringResource(id = R.string.action_share),
        ),
        RecentsQuickAction(
            id = 2,
            adapter = prefs.recentsActionLens.getAdapter(),
            label = stringResource(id = R.string.action_lens),
        ).takeIf { lensAvailable },
        RecentsQuickAction(
            id = 3,
            adapter = prefs.recentsActionLocked.getAdapter(),
            label = stringResource(id = R.string.recents_lock_unlock),
            description = stringResource(id = R.string.recents_lock_unlock_description),
        ),
        RecentsQuickAction(
            id = 4,
            adapter = prefs.recentsActionClearAll.getAdapter(),
            label = stringResource(id = R.string.recents_clear_all),
        ),
    )

    PreferenceLayout(
        label = stringResource(id = R.string.quickstep_label),
        modifier = modifier,
    ) {
        if (!SkinApp.isRecentsEnabled) QuickSwitchIgnoredWarning()
        val recentsTranslucentBackground by prefs.recentsTranslucentBackground.observeAsState()
        PreferenceGroup(heading = stringResource(id = R.string.general_label)) {
            SwitchPreference(
                adapter = prefs.recentsTranslucentBackground.getAdapter(),
                label = stringResource(id = R.string.translucent_background),
            )
            ExpandAndShrink(visible = recentsTranslucentBackground) {
                SliderPreference(
                    adapter = prefs.recentsTranslucentBackgroundAlpha.getAdapter(),
                    label = stringResource(id = R.string.translucent_background_alpha),
                    step = 0.05f,
                    valueRange = 0f..0.95f,
                    showAsPercentage = true,
                )
            }
        }

        QuickActionsPreferences(
            items = recentActionsPreferences,
            adapter = prefs.recentActionOrder.getAdapter(),
        )

        val overrideWindowCornerRadius by prefs.overrideWindowCornerRadius.observeAsState()
        PreferenceGroup(
            heading = stringResource(id = R.string.window_corner_radius_label),
            description = stringResource(id = (R.string.window_corner_radius_description)),
            showDescription = overrideWindowCornerRadius,
        ) {
            SwitchPreference(
                adapter = prefs.overrideWindowCornerRadius.getAdapter(),
                label = stringResource(id = R.string.override_window_corner_radius_label),
            )
            ExpandAndShrink(visible = overrideWindowCornerRadius) {
                SliderPreference(
                    label = stringResource(id = R.string.window_corner_radius_label),
                    adapter = prefs.windowCornerRadius.getAdapter(),
                    step = 0,
                    valueRange = 70..150,
                )
            }
        }

        if (Utilities.ATLEAST_S_V2) {
            PreferenceGroup(
                heading = stringResource(id = R.string.taskbar_label),
            ) {
                SwitchPreference(
                    adapter = prefs2.enableTaskbarOnPhone.getAdapter(),
                    label = stringResource(id = R.string.enable_taskbar_experimental),
                )
            }
        }
    }
}

@PreviewSkin
@Composable
private fun QuickSwitchIgnoredWarning(
    modifier: Modifier = Modifier,
) {
    SkinTheme {
        WarningPreference(
            text = stringResource(id = R.string.quickswitch_ignored_warning),
            modifier = modifier.padding(horizontal = 16.dp),
            standalone = true,
            colors = ListItemDefaults.segmentedColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            ),
        )
    }
}
