package com.android.purebilibili.feature.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.android.purebilibili.core.ui.AppSurfaceTokens
import com.android.purebilibili.core.util.AppFoldPosture
import com.android.purebilibili.core.util.LocalAppWindowAdaptiveInfo
import com.android.purebilibili.feature.settings.resolveSettingsCategoryNavKey
import com.android.purebilibili.feature.settings.resolveSettingsTabletLayoutPolicy
import com.android.purebilibili.feature.settings.resolveSettingsTabletShellCategory
import com.android.purebilibili.feature.settings.shouldUseSettingsSplitLayout
import com.android.purebilibili.navigation3.BiliPaiNavKey
import com.android.purebilibili.navigation3.resolveSettingsDetailStack

internal val LocalSettingsSceneContent = staticCompositionLocalOf { false }
internal val LocalSettingsDetailPaneRoot = staticCompositionLocalOf { false }

/** The app owns the saved route stack; official scenes own pane layout and predictive back. */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun SettingsAdaptiveSceneHost(
    segment: List<BiliPaiNavKey>,
    onSelectKey: (BiliPaiNavKey) -> Unit,
    onPushKey: (BiliPaiNavKey) -> Unit,
    onPopDestination: () -> Unit,
    onExit: () -> Unit,
    content: @Composable (BiliPaiNavKey) -> Unit,
) {
    val widthDp = LocalConfiguration.current.screenWidthDp
    val layout = resolveSettingsTabletLayoutPolicy(widthDp)
    val defaultDirective = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    val singlePane = !shouldUseSettingsSplitLayout(widthDp) ||
        LocalAppWindowAdaptiveInfo.current.posture == AppFoldPosture.Tabletop
    val directive = defaultDirective.copy(
        maxHorizontalPartitions = if (singlePane) 1 else defaultDirective.maxHorizontalPartitions.coerceAtMost(2),
        maxVerticalPartitions = 1,
    )
    val persistentPanes = directive.maxHorizontalPartitions > 1
    val detailStack = resolveSettingsDetailStack(segment)
    // The full settings segment drives one NavDisplay: Settings is the list pane root and
    // every deeper key is a detail-pane entry. Collapsing to [Settings, detailRoot] and
    // delegating deeper levels to a nested NavDisplay breaks single-pane pushes, because
    // the outer back stack never changes and the nested display misses the update.
    val sceneStack = segment.ifEmpty { listOf(BiliPaiNavKey.Settings) }
    val detailRoot = detailStack.firstOrNull()
    val strategy = rememberListDetailSceneStrategy<BiliPaiNavKey>(
        shouldHandleSinglePaneLayout = true,
        backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
        directive = directive,
    )
    Box(Modifier.fillMaxSize().background(AppSurfaceTokens.groupedListContainer())) {
        NavDisplay(
            backStack = sceneStack,
            sceneStrategies = listOf(strategy),
            onBack = onPopDestination,
            entryProvider = { sceneKey ->
                NavEntry(
                    key = sceneKey,
                    metadata = if (sceneKey == BiliPaiNavKey.Settings) {
                        ListDetailSceneStrategy.listPane(detailPlaceholder = { SettingsEmptyDetailPane() }) +
                            ListDetailSceneStrategy.preferredPaneSize(width = layout.primaryRatio)
                    } else {
                        ListDetailSceneStrategy.detailPane()
                    },
                ) {
                    if (sceneKey == BiliPaiNavKey.Settings) {
                        if (persistentPanes) {
                            SettingsCategoryListPane(
                                selectedCategory = segment.lastOrNull()?.let(::resolveSettingsTabletShellCategory),
                                onCategoryClick = { onSelectKey(resolveSettingsCategoryNavKey(it)) },
                                onBack = onExit,
                                onSearchOpen = { onPushKey(BiliPaiNavKey.SettingsSearch) },
                            )
                        } else {
                            CompositionLocalProvider(LocalSettingsSceneContent provides true) {
                                content(BiliPaiNavKey.Settings)
                            }
                        }
                    } else {
                        CompositionLocalProvider(
                            LocalSettingsSceneContent provides true,
                            LocalSettingsDetailPaneRoot provides (persistentPanes && detailRoot == sceneKey),
                        ) {
                            content(sceneKey)
                        }
                    }
                }
            },
        )
    }
}
