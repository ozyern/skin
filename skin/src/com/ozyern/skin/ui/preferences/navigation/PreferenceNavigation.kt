package com.ozyern.skin.ui.preferences.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.ozyern.skin.backup.ui.CreateBackupScreen
import com.ozyern.skin.backup.ui.restoreBackupGraph
import com.ozyern.skin.backup.ui.restoreNovaBackupGraph
import com.ozyern.skin.preferences.BasePreferenceManager
import com.ozyern.skin.preferences.preferenceManager
import com.ozyern.skin.ui.preferences.LocalIsExpandedScreen
import com.ozyern.skin.ui.preferences.about.About
import com.ozyern.skin.ui.preferences.about.acknowledgements.Acknowledgements
import com.ozyern.skin.ui.preferences.components.colorpreference.ColorPreferenceModelList
import com.ozyern.skin.ui.preferences.components.colorpreference.ColorSelection
import com.ozyern.skin.ui.preferences.components.search.SearchProviderId
import com.ozyern.skin.ui.preferences.components.search.SearchProviderPreferenceScreen
import com.ozyern.skin.ui.preferences.destinations.AppDrawerFoldersPreference
import com.ozyern.skin.ui.preferences.destinations.AppDrawerPreferences
import com.ozyern.skin.ui.preferences.destinations.BackupAndRestorePreference
import com.ozyern.skin.ui.preferences.destinations.CustomIconShapePreference
import com.ozyern.skin.ui.preferences.destinations.DebugMenuPreferences
import com.ozyern.skin.ui.preferences.destinations.DismissedPredictionAppsPreferences
import com.ozyern.skin.ui.preferences.destinations.DockPreferences
import com.ozyern.skin.ui.preferences.destinations.DummyPreference
import com.ozyern.skin.ui.preferences.destinations.ExperimentalFeaturesPreferences
import com.ozyern.skin.ui.preferences.destinations.FeatureFlagsPreference
import com.ozyern.skin.ui.preferences.destinations.FolderPreferences
import com.ozyern.skin.ui.preferences.destinations.FontSelection
import com.ozyern.skin.ui.preferences.destinations.GeneralPreferences
import com.ozyern.skin.ui.preferences.destinations.GesturePreferences
import com.ozyern.skin.ui.preferences.destinations.HiddenAppsPreferences
import com.ozyern.skin.ui.preferences.destinations.HomeScreenGridPreferences
import com.ozyern.skin.ui.preferences.destinations.HomeScreenPreferences
import com.ozyern.skin.ui.preferences.destinations.IconPackPreferences
import com.ozyern.skin.ui.preferences.destinations.IconPickerPreference
import com.ozyern.skin.ui.preferences.destinations.LauncherPopupPreference
import com.ozyern.skin.ui.preferences.destinations.PickAppForGesture
import com.ozyern.skin.ui.preferences.destinations.PredictionsPreferences
import com.ozyern.skin.ui.preferences.destinations.PreferencesDashboard
import com.ozyern.skin.ui.preferences.destinations.QuickstepPreferences
import com.ozyern.skin.ui.preferences.destinations.SearchPreferences
import com.ozyern.skin.ui.preferences.destinations.SearchProviderPreferences
import com.ozyern.skin.ui.preferences.destinations.SelectAppsForDrawerFolder
import com.ozyern.skin.ui.preferences.destinations.SelectIconPreference
import com.ozyern.skin.ui.preferences.destinations.ShapePreference
import com.ozyern.skin.ui.preferences.destinations.SmartspacePreferences
import com.android.launcher3.util.ComponentKey
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

inline fun <reified T> getDeepLink(route: T) where T : PreferenceRoute, T : PreferenceDeepLink = listOf(navDeepLink<T>(basePath = route.deepLink))

@Composable
fun PreferenceNavigation(
    navController: NavHostController,
    startDestination: PreferenceRoute,
    intent: Intent? = null,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()

    LaunchedEffect(intent) {
        intent?.let { navController.handleDeepLink(it) }
    }

    // TODO: navigate to nav3: https://developer.android.com/guide/navigation/navigation-3
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
        exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
        popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
        popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
        predictivePopEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
        predictivePopExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
    ) {
        composable<Root> {
            val isExpandedScreen = LocalIsExpandedScreen.current

            PreferencesDashboard(
                currentRoute = Root,
                onNavigate = {
                    navController.navigate(it)
                },
            )

            LaunchedEffect(isExpandedScreen) {
                if (isExpandedScreen) {
                    navController.navigate(General) {
                        launchSingleTop = true
                        popUpTo(navController.graph.id)
                    }
                }
            }
        }
        composable<Dummy> {
            DummyPreference()
        }

        composable<General>(
            deepLinks = getDeepLink(General),
        ) { GeneralPreferences() }
        composable<GeneralFontSelection> { backStackEntry ->
            val route: GeneralFontSelection = backStackEntry.toRoute()
            val pref = preferenceManager().prefsMap[route.prefKey]
                as? BasePreferenceManager.FontPref ?: return@composable
            FontSelection(pref)
        }
        composable<GeneralIconPack>(
            deepLinks = getDeepLink(GeneralIconPack),
        ) { IconPackPreferences() }
        composable<GeneralIconShape> { backStackEntry ->
            val route: GeneralIconShape = backStackEntry.toRoute()
            ShapePreference(currentTab = route.selectedId)
        }
        composable<GeneralCustomIconShapeCreator>(
            deepLinks = getDeepLink(GeneralCustomIconShapeCreator()),
        ) { backStackEntry ->
            val route: GeneralCustomIconShapeCreator = backStackEntry.toRoute()
            CustomIconShapePreference(currentTab = route.selectedId)
        }

        composable<HomeScreen>(
            deepLinks = getDeepLink(HomeScreen),
        ) { HomeScreenPreferences() }
        composable<HomeScreenGrid>(
            deepLinks = getDeepLink(HomeScreenGrid),
        ) { HomeScreenGridPreferences() }
        composable<HomeScreenPopupEditor>(
            deepLinks = getDeepLink(HomeScreenPopupEditor),
        ) { LauncherPopupPreference() }

        composable<Dock>(
            deepLinks = getDeepLink(Dock),
        ) { DockPreferences() }
        composable<DockSearchProvider>(
            deepLinks = getDeepLink(DockSearchProvider),
        ) { SearchProviderPreferences() }

        composable<Smartspace>(
            deepLinks = getDeepLink(Smartspace),
        ) { SmartspacePreferences(fromWidget = false) }
        composable<SmartspaceWidget> { SmartspacePreferences(fromWidget = true) }

        composable<AppDrawer>(
            deepLinks = getDeepLink(AppDrawer),
        ) { AppDrawerPreferences() }
        composable<AppDrawerHiddenApps>(
            deepLinks = getDeepLink(AppDrawerHiddenApps),
        ) { HiddenAppsPreferences() }
        composable<AppDrawerAppListToFolder> { backStackEntry ->
            val args = backStackEntry.arguments!!
            val folderInfoId = args.getInt("id")
            SelectAppsForDrawerFolder(folderInfoId)
        }
        composable<AppDrawerFolder>(
            deepLinks = getDeepLink(AppDrawerFolder),
        ) { AppDrawerFoldersPreference() }

        composable<Search>(
            deepLinks = getDeepLink(Search()),
        ) { backStackEntry ->
            val route: Search = backStackEntry.toRoute()
            SearchPreferences(currentTab = route.selectedId)
        }
        composable<SearchProviderPreference>(
            deepLinks = getDeepLink(SearchProviderPreference(SearchProviderId.entries.first())),
        ) { backStackEntry ->
            val route: SearchProviderPreference = backStackEntry.toRoute()
            SearchProviderPreferenceScreen(route.id)
        }

        composable<Folders>(
            deepLinks = getDeepLink(Folders),
        ) { FolderPreferences() }

        composable<Gestures>(
            deepLinks = getDeepLink(Gestures),
        ) { GesturePreferences() }
        composable<GesturesPickApp> { PickAppForGesture() }

        composable<Quickstep>(
            deepLinks = getDeepLink(Quickstep),
        ) { QuickstepPreferences() }
        composable<BackupAndRestore>(
            deepLinks = getDeepLink(BackupAndRestore),
        ) { BackupAndRestorePreference() }

        composable<About>(
            deepLinks = getDeepLink(About),
        ) { About() }
        composable<AboutLicenses>(
            deepLinks = getDeepLink(AboutLicenses),
        ) { Acknowledgements() }

        composable<DebugMenu> { DebugMenuPreferences() }
        composable<FeatureFlags> { FeatureFlagsPreference() }

        composable<SelectIcon> { backStackEntry ->
            val args: SelectIcon = backStackEntry.toRoute()
            val componentKey = args.componentKey
            val key = ComponentKey.fromString(componentKey)!!
            SelectIconPreference(key)
        }
        composable<IconPicker> { backStackEntry ->
            val args: IconPicker = backStackEntry.toRoute()
            IconPickerPreference(packageName = args.packageName)
        }

        composable<ExperimentalFeatures>(
            deepLinks = getDeepLink(ExperimentalFeatures),
        ) { ExperimentalFeaturesPreferences() }
        composable<Predictions>(
            deepLinks = getDeepLink(Predictions),
        ) { PredictionsPreferences() }
        composable<DismissedPredictionApps> { DismissedPredictionAppsPreferences() }
        composable<ColorSelection> { backStackEntry ->
            val screen: ColorSelection = backStackEntry.toRoute()
            val modelList = ColorPreferenceModelList.INSTANCE.get(LocalContext.current)
            val model = modelList[screen.prefKey]
            ColorSelection(
                label = stringResource(id = model.labelRes),
                preference = model.prefObject,
                dynamicEntries = model.dynamicEntries,
            )
        }

        composable<CreateBackup>(
            deepLinks = getDeepLink(CreateBackup),
        ) { CreateBackupScreen(viewModel()) }

        restoreBackupGraph()
        restoreNovaBackupGraph()
    }
}
