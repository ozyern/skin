package com.ozyern.skin.ui.glass

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberCanvasBackdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight

/**
 * Liquid glass surfaces for the app drawer chrome.
 *
 * The refraction is an AGSL shader, so it only renders on API 33+; below that the surfaces fall
 * back to a plain translucent fill. The backdrop is painted from the launcher's own theme colours
 * rather than the view content behind it -- the drawer's apps list is a RecyclerView, which a
 * Compose backdrop cannot sample, so there is nothing else for the lens to bend.
 */
object LiquidGlass {

    val isSupported: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    /** Colours the lens refracts. Derived from the wallpaper via the launcher theme. */
    @Composable
    fun rememberBackdrop(
        tints: List<Color>,
        base: Color,
    ): Backdrop {
        val colors = remember(tints, base) {
            if (tints.isEmpty()) listOf(base, base) else tints
        }
        return rememberCanvasBackdrop { drawGlassBackdrop(colors, base) }
    }

    private fun DrawScope.drawGlassBackdrop(colors: List<Color>, base: Color) {
        drawRect(base)
        // Soft overlapping blooms give the lens something with structure to bend; a flat fill
        // refracts to itself and reads as plain translucency.
        colors.forEachIndexed { index, color ->
            val fraction = (index + 1f) / (colors.size + 1f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.55f), Color.Transparent),
                    center = Offset(size.width * fraction, size.height * (0.35f + 0.3f * index)),
                    radius = size.minDimension * 1.1f,
                ),
                radius = size.minDimension * 1.1f,
                center = Offset(size.width * fraction, size.height * (0.35f + 0.3f * index)),
            )
        }
    }
}

/**
 * Draws [content] on a liquid glass surface of [shape].
 *
 * @param fallbackFill used verbatim when the AGSL shader is unavailable.
 */
@Composable
fun GlassSurface(
    backdrop: Backdrop,
    shape: Shape,
    fallbackFill: Color,
    modifier: Modifier = Modifier,
    blurRadius: Dp = 8.dp,
    refractionHeight: Dp = 12.dp,
    refractionAmount: Dp = (-16).dp,
    highlightAlpha: Float = 0.35f,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val surface =
        if (LiquidGlass.isSupported) {
            Modifier.drawBackdrop(
                backdrop = backdrop,
                shape = { shape },
                effects = {
                    blur(blurRadius.toPx())
                    lens(refractionHeight.toPx(), refractionAmount.toPx(), true, true)
                },
                highlight = { Highlight(alpha = highlightAlpha) },
            )
        } else {
            Modifier.background(fallbackFill, shape)
        }

    Box(modifier = modifier.then(surface)) {
        Box(Modifier.fillMaxSize(), content = content)
    }
}
