package com.android.purebilibili.feature.settings.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.android.purebilibili.feature.settings.shouldUseSettingsSplitLayout
import com.android.purebilibili.feature.settings.ui.SettingsOpaqueSurfaceHost
import com.android.purebilibili.navigation3.BiliPaiNavKey
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import com.android.purebilibili.core.util.AppFoldPosture
import com.android.purebilibili.core.util.LocalAppWindowAdaptiveInfo

/** 与设置脚手架使用相同的窗口和铰链条件，避免单栏退化时误禁用页面导航。 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun settingsHasPersistentPanes(): Boolean {
    val directive = calculatePaneScaffoldDirective(currentWindowAdaptiveInfoV2())
    return shouldUseSettingsSplitLayout(LocalConfiguration.current.screenWidthDp) &&
        directive.maxHorizontalPartitions > 1 &&
        LocalAppWindowAdaptiveInfo.current.posture != AppFoldPosture.Tabletop
}

@Composable
internal fun SettingsTabletNavEntryShell(
    key: BiliPaiNavKey,
    onSystemBack: () -> Unit,
    onPushKey: (BiliPaiNavKey) -> Unit,
    onSelectPaneKey: (BiliPaiNavKey) -> Unit,
    sceneActive: Boolean,
    sceneContent: @Composable (BiliPaiNavKey) -> Unit,
    content: @Composable () -> Unit,
) {
    if (LocalSettingsSceneContent.current || !sceneActive || !settingsHasPersistentPanes()) {
        content()
    } else {
        // The settings bottom tab has no outer route entry until a detail is selected.
        SettingsOpaqueSurfaceHost {
            SettingsAdaptiveSceneHost(
                segment = listOf(key),
                onSelectKey = onSelectPaneKey,
                onPushKey = onPushKey,
                onPopDestination = onSystemBack,
                onExit = onSystemBack,
                content = sceneContent,
            )
        }
    }
}
