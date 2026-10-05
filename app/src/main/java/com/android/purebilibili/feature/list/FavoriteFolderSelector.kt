package com.android.purebilibili.feature.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.android.purebilibili.core.ui.AppChromeSizeTokens
import com.android.purebilibili.core.ui.AppShapes
import com.android.purebilibili.core.ui.AppSpacingTokens
import com.android.purebilibili.core.ui.ContainerLevel
import com.android.purebilibili.core.ui.components.AppDropdownMenu
import com.android.purebilibili.core.ui.components.AppDropdownMenuItem
import com.android.purebilibili.core.ui.components.AppIcon
import com.android.purebilibili.core.ui.components.AppSurface
import com.android.purebilibili.core.ui.components.AppText
import com.android.purebilibili.core.ui.rememberAppBookmarkIcon
import com.android.purebilibili.core.ui.rememberAppChevronDownIcon
import com.android.purebilibili.core.ui.rememberAppFolderIcon
import com.android.purebilibili.core.util.FormatUtils
import com.android.purebilibili.feature.home.components.BottomBarMatchedReusableLiquidDock

@Composable
internal fun FavoriteFolderSelector(
    folders: List<com.android.purebilibili.data.model.response.FavFolder>,
    selectedFolderIndex: Int,
    selectedFolderItems: List<com.android.purebilibili.data.model.response.VideoItem>,
    subscribedSelected: Boolean,
    layout: CommonListFavoriteHeaderLayout,
    onFolderSelected: (Int) -> Unit,
    onSubscribedSelected: () -> Unit,
    modifier: Modifier = Modifier,
    backdrop: top.yukonga.miuix.kmp.blur.Backdrop? = null,
) {
    val selectedFolder = folders.getOrNull(selectedFolderIndex)
    if (selectedFolder == null && !subscribedSelected) return
    var expanded by remember { androidx.compose.runtime.mutableStateOf(false) }
    val selectedPreviewCover = remember(selectedFolder?.cover, selectedFolderItems, subscribedSelected) {
        selectedFolder?.takeUnless { subscribedSelected }?.let { folder ->
            resolveFavoriteFolderPreviewCover(
                folder = folder,
                loadedItems = selectedFolderItems,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = layout.folderChipRowHorizontalPaddingDp.dp,
                end = layout.folderChipRowHorizontalPaddingDp.dp,
                top = layout.folderChipRowTopPaddingDp.dp,
            ),
    ) {
        BottomBarMatchedReusableLiquidDock(
            shape = AppShapes.container(ContainerLevel.Pill),
            modifier = Modifier.fillMaxWidth(),
            backdrop = backdrop,
            reuseEnabled = true,
            useNeutralLiquidContainer = true,
        ) { liquidChromeActive ->
            AppSurface(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = AppShapes.container(ContainerLevel.Pill),
                color = if (liquidChromeActive) Color.Transparent else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = AppChromeSizeTokens.MinimumTouchTarget)
                        .padding(horizontal = layout.folderChipHorizontalPaddingDp.dp),
                    horizontalArrangement = Arrangement.spacedBy(layout.folderChipSpacingDp.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FavoriteFolderChipPreview(
                        coverUrl = selectedPreviewCover,
                        selected = true,
                    )
                    AppText(
                        text = if (subscribedSelected) "追更（订阅）" else selectedFolder?.title.orEmpty(),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    AppText(
                        text = if (subscribedSelected) "1/${folders.size + 1}" else
                            "${selectedFolderIndex + 2}/${folders.size + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    AppIcon(
                        imageVector = rememberAppChevronDownIcon(),
                        contentDescription = "切换收藏夹",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        AppDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.widthIn(min = 280.dp, max = 420.dp),
        ) {
            AppDropdownMenuItem(
                text = {
                    AppText(
                        text = "追更（订阅）",
                        fontWeight = if (subscribedSelected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                leadingIcon = {
                    AppIcon(
                        imageVector = rememberAppBookmarkIcon(),
                        contentDescription = null,
                        tint = if (subscribedSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
                trailingIcon = if (subscribedSelected) {
                    {
                        AppIcon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "当前为追更",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                } else {
                    null
                },
                onClick = {
                    expanded = false
                    onSubscribedSelected()
                },
            )
            folders.forEachIndexed { index, folder ->
                val isSelected = !subscribedSelected && index == selectedFolderIndex
                AppDropdownMenuItem(
                    text = {
                        AppText(
                            text = folder.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    leadingIcon = {
                        FavoriteFolderChipPreview(
                            coverUrl = resolveFavoriteFolderPreviewCover(
                                folder = folder,
                                loadedItems = if (isSelected) selectedFolderItems else emptyList(),
                            ),
                            selected = isSelected,
                        )
                    },
                    trailingIcon = if (isSelected) {
                        {
                            AppIcon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "当前收藏夹",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        null
                    },
                    onClick = {
                        expanded = false
                        onFolderSelected(index)
                    },
                )
            }
        }
    }
}

@Composable
private fun FavoriteFolderChipPreview(
    coverUrl: String?,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .size(AppSpacingTokens.ExtraLarge)
            .clip(AppShapes.container(ContainerLevel.Chip))
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.46f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (coverUrl != null) {
            AsyncImage(
                model = FormatUtils.fixImageUrl(coverUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            AppIcon(
                imageVector = rememberAppFolderIcon(),
                contentDescription = null,
                modifier = Modifier.size(AppSpacingTokens.Large - AppSpacingTokens.Micro / 2),
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
