package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.ozyern.skin.ui.glass.GlassSurface
import com.ozyern.skin.ui.glass.LiquidGlass
import com.ozyern.skin.ui.theme.SkinTheme
import com.android.launcher3.R

/**
 * The liquid glass panel behind the drawer's search pill and colour button.
 *
 * Deliberately more subdued than the All/Categories pill: the reference is a dark, near-neutral
 * capsule lifted by a bright rim rather than a colourful refraction, so the backdrop is tinted
 * with surface colours and the highlight carries most of the effect.
 */
class SkinSearchGlassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    @Composable
    override fun Content() {
        SkinTheme {
            val scheme = MaterialTheme.colorScheme
            GlassSurface(
                backdrop = LiquidGlass.rememberBackdrop(
                    tints = listOf(
                        scheme.surfaceVariant,
                        scheme.surfaceContainerHighest,
                        scheme.outlineVariant,
                    ),
                    base = colorResource(R.color.skin_search_capsule),
                ),
                shape = CircleShape,
                fallbackFill = scheme.surfaceVariant.copy(alpha = 0.72f),
                modifier = Modifier.fillMaxSize(),
                blurRadius = 12.dp,
                refractionHeight = 10.dp,
                refractionAmount = (-10).dp,
                highlightAlpha = 0.18f,
            )
        }
    }
}
