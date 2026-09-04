package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ozyern.skin.ui.glass.GlassSurface
import com.ozyern.skin.ui.glass.LiquidGlass
import com.ozyern.skin.ui.theme.SkinTheme
import com.android.launcher3.R

/**
 * The pinned bar at the top of the app drawer: an "All / Categories" segmented pill on a liquid
 * glass surface, plus an overflow button. Reports selection through [onTabSelected] and leaves the
 * data switching to the drawer.
 */
class SkinDrawerTopBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    /** Invoked with true when "Categories" is picked, false for "All". */
    var onTabSelected: ((categories: Boolean) -> Unit)? = null

    /** Invoked when the overflow button is tapped, with the button as the anchor. */
    var onOverflowClick: ((anchor: View) -> Unit)? = null

    private val composeView = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
    }

    var categoriesSelected: Boolean = false
        private set

    private var selectionState: ((Boolean) -> Unit)? = null

    init {
        addView(composeView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        composeView.setContent { SkinTheme { BarContent() } }
    }

    /** Moves the selection without firing [onTabSelected]; used to restore state. */
    fun setSelectedTab(categories: Boolean) {
        categoriesSelected = categories
        selectionState?.invoke(categories)
    }

    @Composable
    private fun BarContent() {
        var categories by remember { mutableStateOf(categoriesSelected) }
        selectionState = { categories = it }

        val scheme = MaterialTheme.colorScheme
        // Neutral surface tints, matching the search bar. Accent-coloured blooms refracted into
        // a milky gradient rather than the crisp dark track the design calls for.
        val backdrop = LiquidGlass.rememberBackdrop(
            tints = listOf(
                scheme.surfaceVariant,
                scheme.surfaceContainerHighest,
                scheme.outlineVariant,
            ),
            base = colorResource(R.color.skin_drawer_tab_track),
        )
        val trackShape = CircleShape

        // Icons scroll underneath this pinned bar. Launcher3's header protection cannot help
        // here: it returns early while allAppsBlur is on, and getHeaderBottom() reports a
        // zero-height strip when the search bar is floating. Fade the strip out ourselves.
        val scrim = Brush.verticalGradient(
            0f to scheme.surface.copy(alpha = 0.92f),
            0.65f to scheme.surface.copy(alpha = 0.55f),
            1f to Color.Transparent,
        )

        Box(Modifier.fillMaxSize().background(scrim)) {
            GlassSurface(
                backdrop = backdrop,
                shape = trackShape,
                fallbackFill = colorResource(R.color.skin_drawer_tab_track),
                highlightAlpha = 0.12f,
                modifier = Modifier
                    .align(Alignment.Center)
                    .height(TRACK_HEIGHT)
                    .width(TAB_WIDTH * 2 + TRACK_PADDING * 2)
                    .clip(trackShape),
            ) {
                Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    Tab(
                        label = stringResource(R.string.skin_drawer_tab_all),
                        selected = !categories,
                    ) {
                        categories = false
                        categoriesSelected = false
                        onTabSelected?.invoke(false)
                    }
                    Tab(
                        label = stringResource(R.string.skin_drawer_tab_categories),
                        selected = categories,
                    ) {
                        categories = true
                        categoriesSelected = true
                        onTabSelected?.invoke(true)
                    }
                }
            }

            Icon(
                painter = painterResource(R.drawable.ic_skin_drawer_overflow),
                contentDescription = stringResource(R.string.skin_drawer_overflow_description),
                tint = scheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onOverflowClick?.invoke(this@SkinDrawerTopBar) }
                    .padding(9.dp),
            )
        }
    }

    @Composable
    private fun Tab(label: String, selected: Boolean, onClick: () -> Unit) {
        val scheme = MaterialTheme.colorScheme
        Box(
            modifier = Modifier
                .width(TAB_WIDTH)
                .fillMaxHeight()
                .padding(TRACK_PADDING)
                .clip(CircleShape)
                .background(
                    if (selected) colorResource(R.color.skin_drawer_tab_selected) else Color.Transparent,
                    CircleShape,
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = colorResource(
                    if (selected) R.color.skin_drawer_tab_text_selected
                    else R.color.skin_drawer_tab_text,
                ),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }

    private companion object {
        val TRACK_HEIGHT = 44.dp
        val TAB_WIDTH = 108.dp
        val TRACK_PADDING = 3.dp
    }
}
