package com.ozyern.skin.ui.preferences.destinations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ozyern.skin.backup.ui.restoreBackupOpener
import com.ozyern.skin.backup.ui.restoreNovaBackupOpener
import com.ozyern.skin.ui.preferences.LocalIsExpandedScreen
import com.ozyern.skin.ui.preferences.components.NavigationActionPreference
import com.ozyern.skin.ui.preferences.components.controls.ClickablePreference
import com.ozyern.skin.ui.preferences.components.layout.PreferenceGroup
import com.ozyern.skin.ui.preferences.components.layout.PreferenceLayout
import com.ozyern.skin.ui.preferences.navigation.CreateBackup
import com.android.launcher3.R

@Composable
fun BackupAndRestorePreference(
    modifier: Modifier = Modifier,
) {
    PreferenceLayout(
        label = stringResource(R.string.backup_and_restore_label),
        backArrowVisible = !LocalIsExpandedScreen.current,
        modifier = modifier,
    ) {
        PreferenceGroup {
            NavigationActionPreference(
                label = stringResource(R.string.create_backup),
                subtitle = stringResource(R.string.create_backup_description),
                destination = CreateBackup,
            )
            ClickablePreference(
                label = stringResource(R.string.restore_backup),
                subtitle = stringResource(R.string.restore_backup_description),
                onClick = restoreBackupOpener(),
            )
        }
        PreferenceGroup {
            ClickablePreference(
                label = stringResource(R.string.restore_nova_backup),
                subtitle = stringResource(R.string.restore_nova_backup_description),
                onClick = restoreNovaBackupOpener(),
            )
        }
    }
}
