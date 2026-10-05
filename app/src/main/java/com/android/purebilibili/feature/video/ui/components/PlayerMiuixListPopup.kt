package com.android.purebilibili.feature.video.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.window.WindowListPopup

enum class PlayerListPopupPlacement {
    CENTER,
    START,
    END,
    END_BOTTOM,
}

private val PlayerCenterPopupPositionProvider = object : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowBounds: IntRect,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
        popupMargin: IntRect,
        alignment: PopupPositionProvider.Align,
    ): IntOffset = IntOffset(
        x = windowBounds.left + (windowBounds.width - popupContentSize.width) / 2,
        y = windowBounds.top + (windowBounds.height - popupContentSize.height) / 2,
    )

    override fun getMargins(): PaddingValues = PaddingValues(0.dp)
}

private val PlayerEndBottomPopupPositionProvider = object : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowBounds: IntRect,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
        popupMargin: IntRect,
        alignment: PopupPositionProvider.Align,
    ): IntOffset = IntOffset(
        x = windowBounds.right - popupContentSize.width - popupMargin.right,
        y = windowBounds.bottom - popupContentSize.height - popupMargin.bottom,
    )

    override fun getMargins(): PaddingValues = PaddingValues(24.dp)
}

/** Shared native Miuix popup shell for player option lists. */
@Composable
fun PlayerMiuixListPopup(
    title: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placement: PlayerListPopupPlacement = PlayerListPopupPlacement.CENTER,
    maxHeight: Dp? = 440.dp,
    minWidth: Dp = ListPopupDefaults.MinWidth,
    content: @Composable () -> Unit,
) {
    val alignment = when (placement) {
        PlayerListPopupPlacement.CENTER,
        PlayerListPopupPlacement.START -> PopupPositionProvider.Align.Start
        PlayerListPopupPlacement.END,
        PlayerListPopupPlacement.END_BOTTOM -> PopupPositionProvider.Align.End
    }
    val horizontalMargin = when (placement) {
        PlayerListPopupPlacement.CENTER -> 0.dp
        PlayerListPopupPlacement.START,
        PlayerListPopupPlacement.END,
        PlayerListPopupPlacement.END_BOTTOM -> 24.dp
    }

    val popupContent: @Composable () -> Unit = {
        ListPopupColumn {
            SmallTitle(text = title)
            content()
        }
    }

    WindowListPopup(
        show = true,
        popupModifier = modifier,
        popupPositionProvider = when (placement) {
            PlayerListPopupPlacement.CENTER -> PlayerCenterPopupPositionProvider
            PlayerListPopupPlacement.END_BOTTOM -> PlayerEndBottomPopupPositionProvider
            else -> ListPopupDefaults.dropdownPositionProvider(
                verticalMargin = 8.dp,
                horizontalMargin = horizontalMargin,
            )
        },
        alignment = alignment,
        enableWindowDim = placement == PlayerListPopupPlacement.CENTER,
        onDismissRequest = onDismissRequest,
        maxHeight = maxHeight,
        minWidth = minWidth,
        content = popupContent,
    )
}
