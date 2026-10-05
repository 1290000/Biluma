package com.android.purebilibili.feature.profile

import coil3.request.crossfade

import com.android.purebilibili.core.ui.components.AppSegmentOption
import com.android.purebilibili.core.ui.components.AppThemeAdaptiveTabRow
import com.android.purebilibili.core.ui.components.AppText

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.android.purebilibili.core.ui.wallpaper.ProfileWallpaperTransform
import com.android.purebilibili.core.ui.wallpaper.applyGestureToProfileWallpaperTransform
import com.android.purebilibili.core.ui.wallpaper.sanitizeProfileWallpaperTransform
import coil3.request.ImageRequest
import com.android.purebilibili.core.ui.AppModalBottomSheet
import com.android.purebilibili.core.ui.components.AppCard
import com.android.purebilibili.core.ui.components.AppCardShape
import com.android.purebilibili.core.ui.components.AppTextButton
import com.android.purebilibili.core.ui.AppShapes
import com.android.purebilibili.core.ui.ContainerLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileWallpaperAdjustmentSheet(
    imageUri: String,
    initialMobileTransform: ProfileWallpaperTransform = ProfileWallpaperTransform(),
    initialTabletTransform: ProfileWallpaperTransform = ProfileWallpaperTransform(),
    onSave: (mobileTransform: ProfileWallpaperTransform, tabletTransform: ProfileWallpaperTransform) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var mobileTransform by remember {
        mutableStateOf(sanitizeProfileWallpaperTransform(initialMobileTransform))
    }
    var tabletTransform by remember {
        mutableStateOf(sanitizeProfileWallpaperTransform(initialTabletTransform))
    }

    val currentTransform = if (selectedTab == 0) mobileTransform else tabletTransform
    val currentTransformState = rememberUpdatedState(currentTransform)
    fun updateCurrentTransform(transform: ProfileWallpaperTransform) {
        if (selectedTab == 0) {
            mobileTransform = sanitizeProfileWallpaperTransform(transform)
        } else {
            tabletTransform = sanitizeProfileWallpaperTransform(transform)
        }
    }

    AppModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                AppText(
                    text = "取消",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onDismiss() },
                    color = MaterialTheme.colorScheme.primary
                )

                AppText(
                    text = "调整壁纸位置",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )

                AppText(
                    text = "保存",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onSave(mobileTransform, tabletTransform) },
                    color = MaterialTheme.colorScheme.primary
                )
            }

            WallpaperDeviceTabRow(
                selectedTab = selectedTab,
                onSelectedTabChange = { selectedTab = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(328.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                val aspectRatio = if (selectedTab == 0) 9f / 18f else 16f / 10f
                val width = if (selectedTab == 0) 150.dp else 292.dp
                val height = width / aspectRatio
                val previewCornerRadius = if (selectedTab == 0) 16.dp else 12.dp

                AppCard(
                    shape = AppCardShape.Uniform(previewCornerRadius),
                    modifier = Modifier
                        .size(width = width, height = height)
                        .shadow(8.dp, RoundedCornerShape(previewCornerRadius)),
                ) {
                    var previewSize by remember(selectedTab) { mutableStateOf(IntSize.Zero) }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onSizeChanged { previewSize = it }
                            .pointerInput(selectedTab, previewSize) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    updateCurrentTransform(
                                        applyGestureToProfileWallpaperTransform(
                                            current = currentTransformState.value,
                                            panX = pan.x,
                                            panY = pan.y,
                                            zoomChange = zoom,
                                            containerWidthPx = previewSize.width.toFloat(),
                                            containerHeightPx = previewSize.height.toFloat()
                                        )
                                    )
                                }
                            }
                    ) {
                        com.android.purebilibili.core.ui.wallpaper.WallpaperMedia(
                            uri = imageUri,
                            imageModel = ImageRequest.Builder(LocalContext.current)
                                .data(imageUri)
                                .crossfade(true)
                                .build(),
                            alignment = androidx.compose.ui.BiasAlignment(
                                currentTransform.offsetX,
                                currentTransform.offsetY
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = currentTransform.scale,
                                    scaleY = currentTransform.scale
                                )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.42f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(88.dp)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.24f),
                                            Color.Black.copy(alpha = 0.46f)
                                        )
                                    )
                                )
                        )

                        AppText(
                            text = "双指缩放  单指拖动",
                            color = Color.White.copy(alpha = 0.86f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(Color.Black.copy(alpha = 0.3f), AppShapes.container(ContainerLevel.Tag))
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    AppText(
                        text = if (selectedTab == 0) "手机端参数" else "平板端参数",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AppText(
                        text = "缩放 ${"%.2f".format(currentTransform.scale)}x  横向 ${"%.2f".format(currentTransform.offsetX)}  纵向 ${"%.2f".format(currentTransform.offsetY)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                AppTextButton(
                    onClick = { updateCurrentTransform(ProfileWallpaperTransform()) }
                ) {
                    AppText("重置位置")
                }
            }

            AppText(
                text = "不同设备分别保存；首次设置会以居中参数作为默认值。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun WallpaperDeviceTabRow(
    selectedTab: Int,
    modifier: Modifier = Modifier,
    onSelectedTabChange: (Int) -> Unit,
) {
    val options = remember {
        listOf(
            AppSegmentOption(0, "手机端"),
            AppSegmentOption(1, "平板端"),
        )
    }
    AppThemeAdaptiveTabRow(
        options = options,
        selectedValue = selectedTab,
        onSelectionChange = onSelectedTabChange,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        compactMiuixWhenTwoOptions = true,
        height = 48.dp,
        indicatorHeight = com.android.purebilibili.core.ui
            .roundMatchedLiquidIndicatorHeightDp(48f).dp,
        labelFontSize = 14.sp,
        dragSelectionEnabled = true,
        tapPressRefractionEnabled = true,
    )
}
