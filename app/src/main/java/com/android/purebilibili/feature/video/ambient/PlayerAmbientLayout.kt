package com.android.purebilibili.feature.video.ambient

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.clipRect
import com.android.purebilibili.core.ui.AppSurfaceTokens
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp

/** Owns the glow outside player clipping/shared bounds, with a real gutter before body content.
 * playerModifier retains the original video sizing; modifier places the entire host.
 */
@Composable
internal fun PlayerAmbientLayout(
    playerModifier: Modifier,
    modifier: Modifier = Modifier,
    fullscreen: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    // Reuse a surrounding host when the tablet player delegates to the phone player slot.
    if (LocalAmbientPresentation.current != null) {
        Box(modifier = modifier.then(playerModifier), content = content)
        return
    }
    val pageBackground = AppSurfaceTokens.background()
    var originInWindow by remember { mutableStateOf(Offset.Zero) }
    val presentation = remember { AmbientPresentation() }
    val controller = remember(presentation) { AmbientFrameController(presentation) }
    CompositionLocalProvider(
        LocalAmbientPresentation provides presentation,
        LocalAmbientController provides controller,
    ) {
        Box(
            modifier = modifier
                .onGloballyPositioned { originInWindow = it.positionInWindow() }
                .drawWithCache {
                    val video = presentation.inlineBoundsInWindow?.translate(-originInWindow)
                    val height = video?.let { (size.height - it.bottom).coerceIn(0f, 48.dp.toPx()) } ?: 0f
                    val brush = if (video != null && height > 0f) Brush.verticalGradient(
                        0f to Color.Black,
                        0.24f to Color.Black,
                        0.85f to pageBackground,
                        1f to pageBackground,
                        startY = video.bottom,
                        endY = video.bottom + height,
                    ) else null
                    onDrawBehind {
                        if (!fullscreen && presentation.layoutEnabled && presentation.visibilityGate() &&
                            video != null && brush != null) {
                            // The base is beneath the glow canvas, never over the image or body.
                            // Keep the top dark, and finish the transition before the tabs.
                            clipRect(0f, 0f, size.width, size.height) {
                                drawRect(brush, Offset(video.left, video.bottom), Size(video.width, height))
                            }
                        }
                    }
                },
            contentAlignment = Alignment.TopCenter,
        ) {
            if (!fullscreen) PlayerAmbientGlow(presentation, fullscreen = false, modifier = Modifier.matchParentSize())
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = playerModifier.onGloballyPositioned {
                        val origin = it.positionInWindow()
                        presentation.inlineBoundsInWindow = Rect(
                            origin.x, origin.y,
                            origin.x + it.size.width, origin.y + it.size.height,
                        )
                    },
                    content = content,
                )
                // Reserve only for a supported effect. Navigation hides drawing without
                // resizing the player/card bounds; fullscreen never manufactures a margin.
                if (presentation.layoutEnabled && !fullscreen) Spacer(Modifier.height(48.dp))
            }
        }
    }
}
