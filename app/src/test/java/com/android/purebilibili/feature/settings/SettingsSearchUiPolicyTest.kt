package com.android.purebilibili.feature.settings

import com.android.purebilibili.R
import com.android.purebilibili.core.ui.AppIcons
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class SettingsSearchUiPolicyTest {
    @Test
    fun feedbackSearchResult_reusesSettingsSectionIconResource() {
        assertEquals(R.drawable.ms_bug_report_24, resolveSettingsEntryVisual(SettingsSearchTarget.FEEDBACK).iconResId)
    }

    @Test
    fun twitterSearchResult_usesTwitterIcon() {
        assertSame(AppIcons.Twitter, resolveSettingsEntryVisual(SettingsSearchTarget.TWITTER).icon)
    }

    @Test
    fun materialSearchResults_useMaterialSymbolResources() {
        assertEquals(R.drawable.ms_play_circle_24, resolveSettingsEntryVisual(SettingsSearchTarget.PLAYBACK).iconResId)
        assertEquals(R.drawable.ms_cloud_upload_24, resolveSettingsEntryVisual(SettingsSearchTarget.WEBDAV_BACKUP).iconResId)
        assertEquals(R.drawable.ms_home_24, resolveSettingsEntryVisual(SettingsSearchTarget.HOME_FEED).iconResId)
    }
}
