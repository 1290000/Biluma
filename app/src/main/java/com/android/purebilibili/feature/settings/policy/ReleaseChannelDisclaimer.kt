package com.android.purebilibili.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import com.android.purebilibili.core.ui.components.AppText
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.android.purebilibili.core.ui.AppAlertDialog
import com.android.purebilibili.core.ui.components.AppTextButton

const val RELEASE_DISCLAIMER_ACK_KEY = "release_disclaimer_ack_v1"

@Composable
fun ReleaseChannelDisclaimerDialog(
    onDismiss: () -> Unit,
    onOpenGithub: () -> Unit,
    onOpenReleases: () -> Unit,
    title: String = "免责声明"
) {
    AppAlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AppText(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            AppText(
                text = "本应用仅用于学习与交流。\n\n" +
                    "Biluma 的源码、版本发布与问题反馈以本项目 GitHub 仓库为准。\n" +
                    "尚无发行版时，发布页可能为空；请勿使用 BiliPai 安装包更新 Biluma。\n\n" +
                    "本项目基于 BiliPai 独立维护，不是原作者的官方发行版。\n\n" +
                    "请勿安装来源不明的安装包，以避免账号与设备安全风险。"
            )
        },
        confirmButton = {
            AppTextButton(onClick = onDismiss) {
                AppText("我已知晓")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                AppTextButton(onClick = onOpenGithub) { AppText("GitHub") }
                AppTextButton(onClick = onOpenReleases) { AppText("版本发布") }
            }
        }
    )
}
