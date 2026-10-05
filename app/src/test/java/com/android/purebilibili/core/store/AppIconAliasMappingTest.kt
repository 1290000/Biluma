package com.android.purebilibili.core.store

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppIconAliasMappingTest {

    @Test
    fun resolveAppIconLauncherAlias_supportsCanonicalAndLegacyKeys() {
        val packageName = "com.android.purebilibili"

        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidFrontNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_front")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidAnnouncementNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_announcement")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_bilipai")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiNoIcon",
            resolveAppIconLauncherAlias(packageName, "BiliPai")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiPinkNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_bilipai_pink")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiWhiteNoIcon",
            resolveAppIconLauncherAlias(packageName, "BiliPai White")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiMonetNoIcon",
            resolveAppIconLauncherAlias(packageName, "BiliPai Monet")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_headphone")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidNoIcon",
            resolveAppIconLauncherAlias(packageName, "unknown")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_bilipai")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidNoIcon",
            resolveAppIconLauncherAlias(packageName, "unknown")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidFrontNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_front")
        )
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBlueSnowMaidAnnouncementNoIcon",
            resolveAppIconLauncherAlias(packageName, "icon_blue_snow_maid_announcement")
        )
    }

    @Test
    fun resolveAppIconLauncherAlias_supportsFixedMaidAppearances() {
        val packageName = "com.android.purebilibili"

        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidLightNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid",
                appearance = AppIconAppearance.LIGHT
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidDarkNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid",
                appearance = AppIconAppearance.DARK
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidFrontDarkNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_front",
                appearance = AppIconAppearance.DARK
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidFrontLightNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_front",
                appearance = AppIconAppearance.LIGHT
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidAnnouncementLightNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_announcement",
                appearance = AppIconAppearance.LIGHT
            )
        )
        assertEquals(
            "$packageName.MainActivityAliasBlueSnowMaidAnnouncementDarkNoIcon",
            resolveAppIconLauncherAlias(
                packageName,
                "icon_blue_snow_maid_announcement",
                appearance = AppIconAppearance.DARK
            )
        )
    }

    @Test
    fun resolveAppIconLauncherAlias_ignoresAppearanceForNonMaidIcons() {
        assertEquals(
            "com.android.purebilibili.MainActivityAliasBiliPaiNoIcon",
            resolveAppIconLauncherAlias(
                "com.android.purebilibili",
                "icon_bilipai",
                appearance = AppIconAppearance.DARK
            )
        )
    }

    @Test
    fun resolveAppIconLauncherAlias_keepsStableComponentNamespaceForDebugBuilds() {
        assertEquals(
            "com.android.purebilibili.MainActivityAlias3DNoIcon",
            resolveAppIconLauncherAlias("com.android.purebilibili.debug", "icon_3d")
        )
    }

    @Test
    fun bilumaVariantsKeepTheOriginalLauncherComponentNamespace() {
        listOf("com.biluma.app", "com.biluma.app.dev").forEach { applicationId ->
            assertEquals(
                "com.android.purebilibili.MainActivityAlias3DNoIcon",
                resolveAppIconLauncherAlias(applicationId, "icon_3d")
            )
            assertTrue(
                allManagedAppIconLauncherAliases(applicationId).all { alias ->
                    alias.startsWith("com.android.purebilibili.")
                }
            )
        }
    }

    @Test
    fun allManagedAppIconLauncherAliases_containsBiliPaiAndHeadphone_withoutRemovedAliases() {
        val aliases = allManagedAppIconLauncherAliases("com.android.purebilibili")
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaid"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidAnnouncement"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidAnnouncementNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidAnnouncementLight"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidAnnouncementDarkNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidFront"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidFrontNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidLight"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidDarkNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidFrontLightNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBlueSnowMaidFrontDark"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBiliPai"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBiliPaiPink"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBiliPaiWhite"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBiliPaiMonet"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasHeadphone"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAlias3D"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAliasBiliPaiNoIcon"))
        assertTrue(aliases.contains("com.android.purebilibili.MainActivityAlias3DNoIcon"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasBlue"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasNeon"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasTelegramBlueCoin"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasPink"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasPurple"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasGreen"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasFlatMaterial"))
        kotlin.test.assertFalse(aliases.contains("com.android.purebilibili.MainActivityAliasRetro"))
    }
}
