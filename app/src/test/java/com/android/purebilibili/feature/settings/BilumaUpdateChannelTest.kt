package com.android.purebilibili.feature.settings

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BilumaUpdateChannelTest {

    @Test
    fun projectLinksSeparateBilumaFromUpstream() {
        assertEquals("https://github.com/1290000/Biluma", OFFICIAL_GITHUB_URL)
        assertEquals("$OFFICIAL_GITHUB_URL/releases", OFFICIAL_RELEASES_URL)
        assertEquals("$OFFICIAL_GITHUB_URL/issues", OFFICIAL_ISSUES_URL)
        assertEquals("https://api.github.com/repos/1290000/Biluma/releases", APP_RELEASES_API_URL)
        assertEquals("https://github.com/jay3-yy/BiliPai", UPSTREAM_GITHUB_URL)
    }

    @Test
    fun emptyReleasesAreSuccessfulWithoutUpdateOrChangelog() {
        listOf(false, true).forEach { includePrerelease ->
            val requestedUrls = mutableListOf<String>()
            val result = AppUpdateChecker.checkReleases(
                currentVersion = "v0.2.8",
                currentVersionCode = 435,
                includePrerelease = includePrerelease,
                fetchText = { url, required ->
                    assertTrue(required)
                    requestedUrls += url
                    "[]"
                }
            ).getOrThrow()

            assertEquals(listOf(APP_RELEASES_API_URL), requestedUrls)
            assertFalse(result.isUpdateAvailable)
            assertFalse(result.hasAvailableRelease)
            assertEquals("0.2.8", result.currentVersion)
            assertEquals("", result.latestVersion)
            assertEquals(OFFICIAL_RELEASES_URL, result.releaseUrl)
            assertTrue(result.assets.isEmpty())
            assertTrue(result.message.contains("暂无"))
            assertFalse(result.message.contains("已是最新"))
            assertEquals(
                AppUpdateDialogMode.NONE,
                resolveAppUpdateDialogMode(
                    result.isUpdateAvailable,
                    shouldOpenReleaseNotes = true,
                    hasAvailableRelease = result.hasAvailableRelease
                )
            )
        }
    }

    @Test
    fun releasesWithoutApksDoNotOfferAnUpdate() {
        val result = AppUpdateChecker.checkReleases("0.2.8", 435, false) { _, _ ->
            """[{"tag_name":"v9.0.0","draft":false,"assets":[]}]"""
        }.getOrThrow()

        assertFalse(result.hasAvailableRelease)
        assertFalse(result.isUpdateAvailable)
    }

    @Test
    fun missingReleaseUrlFallsBackToBilumaOnly() {
        val result = AppUpdateChecker.checkReleases("0.2.8", 435, false) { _, _ ->
            """[{
                "tag_name":"v0.2.9",
                "assets":[{
                    "name":"Biluma-0.2.9.apk",
                    "browser_download_url":"$OFFICIAL_RELEASES_URL/download/v0.2.9/Biluma-0.2.9.apk"
                }]
            }]"""
        }.getOrThrow()

        assertTrue(result.hasAvailableRelease)
        assertTrue(result.isUpdateAvailable)
        assertEquals(OFFICIAL_RELEASES_URL, result.releaseUrl)
        assertEquals(1, result.assets.size)
    }

    @Test
    fun prereleaseOnlyRepositoryRespectsTheSelectedChannel() {
        val response = """[{
            "tag_name":"v0.2.9-beta.1",
            "prerelease":true,
            "assets":[{
                "name":"Biluma-0.2.9-beta.1.apk",
                "browser_download_url":"$OFFICIAL_RELEASES_URL/download/v0.2.9-beta.1/Biluma-0.2.9-beta.1.apk"
            }]
        }]"""
        val stable = AppUpdateChecker.checkReleases("0.2.8", 435, false) { _, _ -> response }.getOrThrow()
        val beta = AppUpdateChecker.checkReleases("0.2.8", 435, true) { _, _ -> response }.getOrThrow()

        assertFalse(stable.hasAvailableRelease)
        assertFalse(stable.isUpdateAvailable)
        assertTrue(beta.hasAvailableRelease)
        assertTrue(beta.isUpdateAvailable)
    }

    @Test
    fun apiFailuresDoNotMasqueradeAsNoReleasesOrRetryUpstream() {
        val requestedUrls = mutableListOf<String>()
        val result = AppUpdateChecker.checkReleases("0.2.8", 435, false) { url, required ->
            assertTrue(required)
            requestedUrls += url
            error("更新接口异常: HTTP 403")
        }

        assertTrue(result.isFailure)
        assertEquals(listOf(APP_RELEASES_API_URL), requestedUrls)
        assertEquals("更新接口异常: HTTP 403", result.exceptionOrNull()?.message)
    }

    @Test
    fun malformedOrMissingResponsesAreFailures() {
        listOf(null, "not json", "{}").forEach { response ->
            val result = AppUpdateChecker.checkReleases("0.2.8", 435, false) { _, _ -> response }
            assertTrue(result.isFailure)
        }
    }
}
