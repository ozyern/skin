package com.ozyern.skin.allapps.views

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import com.ozyern.skin.ui.glass.GlassSurface
import com.ozyern.skin.ui.glass.LiquidGlass

/**
 * A liquid glass panel used as the background of the drawer's search pill and colour button. It is
 * a plain background layer, so the existing search views stay as they are on top of it.
 */
class SkinSearchGlassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    @Composable
    override fun Content() {
        com.ozyern.skin.ui.theme.SkinTheme {
            val scheme = MaterialTheme.colorScheme
            GlassSurface(
                backdrop = LiquidGlass.rememberBackdrop(
                    tints = listOf(scheme.primary, scheme.secondary, scheme.tertiary),
                    base = scheme.surface.copy(alpha = 0.25f),
                ),
                shape = CircleShape,
                fallbackFill = scheme.onSurface.copy(alpha = 0.14f),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
