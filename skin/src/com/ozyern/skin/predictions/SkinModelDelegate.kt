package com.ozyern.skin.predictions

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.ozyern.skin.preferences2.PreferenceManager2
import com.ozyern.skin.preferences2.firstCached
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.Utilities
import com.android.launcher3.dagger.ApplicationContext
import com.android.launcher3.model.PredictedItemFactory
import com.android.launcher3.model.QuickstepModelDelegate
import com.android.launcher3.pm.UserCache
import com.android.launcher3.util.Executors.MODEL_EXECUTOR
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Suppress("VisibleForTests")
class SkinModelDelegate @Inject constructor(
    @ApplicationContext context: Context,
    private val idp: InvariantDeviceProfile,
    userCache: UserCache,
    itemParserFactory: PredictedItemFactory.Factory,
    @Named("ICONS_DB") dbFileName: String?,
) : QuickstepModelDelegate(context, idp, userCache, itemParserFactory, dbFileName) {

    private val prefs2: PreferenceManager2 by lazy { PreferenceManager2.getInstance(context) }
    private val skinPredictor: SkinAppPredictor by lazy {
        SkinAppPredictor(context)
    }

    private var prefObserverScope: CoroutineScope? = null

    @WorkerThread
    override fun workspaceLoadComplete() {
        super.workspaceLoadComplete()
        registerPredictionModeChanged()
    }

    @WorkerThread
    override fun modelLoadComplete() {
        super.modelLoadComplete()
        syncPredictor()
    }

    @WorkerThread
    override fun destroy() {
        unregisterPredictionModeChanged()
        skinPredictor.unregister()
        super.destroy()
    }

    @WorkerThread
    override fun recreatePredictors() {
        skinPredictor.unregister()
        when (currentPredictionMode()) {
            SystemPredictor -> super.recreatePredictors()

            SkinPredictor -> if (Utilities.ATLEAST_Q) {
                activateSkinPredictor()
            } else {
                clearPredictions()
            }

            NoPredictor -> clearPredictions()
        }
    }

    @WorkerThread
    override fun validateData() {
        super.validateData()
        syncPredictor()
    }

    @WorkerThread
    private fun syncPredictor() {
        when (currentPredictionMode()) {
            SystemPredictor -> {
                mAllPredictionAppsState.requestPredictionUpdate()
                mWidgetsRecommendationState.requestPredictionUpdate()
            }

            SkinPredictor -> if (Utilities.ATLEAST_Q) {
                updateSkinPredictions()
            } else {
                clearPredictions()
            }

            NoPredictor -> clearPredictions()
        }
    }

    private fun currentPredictionMode(): PredictionMode {
        // All predictor targets can't be run on device older than Q
        if (!Utilities.ATLEAST_Q || !prefs2.enableGlobalPrediction.firstCached()) {
            return NoPredictor
        }
        return prefs2.predictionMode.firstCached()
    }

    private fun destroyPredictors() {
        mAllPredictionAppsState.destroyPredictor()
        mHotseatPredictionState.destroyPredictor()
        mWidgetsRecommendationState.destroyPredictor()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun activateSkinPredictor() {
        destroyPredictors()
        if (!mActive) return

        skinPredictor.register(
            model = mModel,
            dataModel = mDataModel,
            allAppsState = mAllPredictionAppsState,
            hotseatState = mHotseatPredictionState,
            widgetsState = mWidgetsRecommendationState,
            idp = idp,
        )
        updateSkinPredictions()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun updateSkinPredictions() {
        skinPredictor.updates(
            model = mModel,
            dataModel = mDataModel,
            allAppsState = mAllPredictionAppsState,
            hotseatState = mHotseatPredictionState,
            widgetsState = mWidgetsRecommendationState,
            idp = idp,
        )
    }

    private fun clearPredictions() {
        destroyPredictors()
        skinPredictor.empty(
            model = mModel,
            allAppsState = mAllPredictionAppsState,
            hotseatState = mHotseatPredictionState,
            widgetsState = mWidgetsRecommendationState,
        )
    }

    private fun registerPredictionModeChanged() {
        prefObserverScope?.cancel()
        val observerScope = CoroutineScope(
            SupervisorJob() +
                Dispatchers.Default +
                CoroutineName("SkinModelDelegate.predictionModeObserver"),
        )
        prefObserverScope = observerScope
        prefs2.enableGlobalPrediction.get()
            .combine(prefs2.predictionMode.get()) { enabled, mode ->
                if (enabled) mode else NoPredictor
            }
            .distinctUntilChanged()
            .drop(1) // Skip
            .onEach { MODEL_EXECUTOR.execute { recreatePredictors() } }
            .launchIn(observerScope)
    }

    private fun unregisterPredictionModeChanged() {
        prefObserverScope?.cancel()
        prefObserverScope = null
    }
}
