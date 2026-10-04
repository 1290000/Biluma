package com.android.purebilibili.feature.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserAgreementPolicyTest {

    @Test
    fun requiresAgreementWhenNotAcknowledged_forOldAndNewUsers() {
        assertTrue(isUserAgreementRequired(hasAcknowledgedUserAgreement = false))
        assertFalse(isUserAgreementRequired(hasAcknowledgedUserAgreement = true))
    }

    @Test
    fun cannotAcknowledgeUntilAllClausesChecked() {
        assertFalse(
            canAcknowledgeUserAgreement(
                openSourceFreeChecked = false,
                noDomesticPromoChecked = false,
                feedbackWithLogsChecked = false,
            )
        )
        assertFalse(
            canAcknowledgeUserAgreement(
                openSourceFreeChecked = true,
                noDomesticPromoChecked = true,
                feedbackWithLogsChecked = false,
            )
        )
        assertTrue(
            canAcknowledgeUserAgreement(
                openSourceFreeChecked = true,
                noDomesticPromoChecked = true,
                feedbackWithLogsChecked = true,
            )
        )
    }

    @Test
    fun mapFormRequiresEveryClauseTrue() {
        val partial = UserAgreementClause.entries.associateWith { it != UserAgreementClause.FEEDBACK_WITH_LOGS }
        assertFalse(canAcknowledgeUserAgreement(partial))

        val full = UserAgreementClause.entries.associateWith { true }
        assertTrue(canAcknowledgeUserAgreement(full))
    }

    @Test
    fun clauseCopyCoversRequiredTopics() {
        val titles = userAgreementClauseList().map { it.title }
        assertTrue(titles.any { it.contains("开源") })
        assertTrue(titles.any { it.contains("宣传") })
        assertTrue(titles.any { it.contains("日志") || it.contains("截图") })
        assertTrue(userAgreementIntroText().isNotBlank())
    }

    @Test
    fun channelLinksIncludeBilumaReleasesIssuesAndSource_withoutDisplayingUrls() {
        val links = userAgreementChannelLinks(
            releasesUrl = "https://github.com/1290000/Biluma/releases",
            issuesUrl = "https://github.com/1290000/Biluma/issues",
            githubUrl = "https://github.com/1290000/Biluma",
        )
        assertEquals(3, links.size)
        assertEquals(
            listOf("Biluma 版本发布", "问题反馈", "Biluma 开源地址"),
            links.map { it.label },
        )
        assertEquals("https://github.com/1290000/Biluma/releases", links[0].url)
        assertEquals("https://github.com/1290000/Biluma/issues", links[1].url)
        assertEquals("https://github.com/1290000/Biluma", links[2].url)
        // UI only shows labels; urls stay internal for openUri.
        assertEquals("user_agreement_ack_v1", USER_AGREEMENT_ACK_KEY)
    }
}
