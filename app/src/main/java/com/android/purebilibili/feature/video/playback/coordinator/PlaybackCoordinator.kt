package com.android.purebilibili.feature.video.playback.coordinator

import com.android.purebilibili.core.store.PlaybackCompletionBehavior
import com.android.purebilibili.feature.video.player.ExternalPlaylistSource
import com.android.purebilibili.feature.video.player.PlayMode
import com.android.purebilibili.feature.video.playback.session.PlaybackSessionStore
import com.android.purebilibili.feature.video.viewmodel.PlaybackEndAction
import com.android.purebilibili.feature.video.viewmodel.resolvePlaybackEndActionForSession

internal class PlaybackCoordinator(
    private val sessionStore: PlaybackSessionStore
) {

    data class PlaybackEndExecutionOutcome(
        val shouldHidePlaybackEndedDialog: Boolean = false
    )

    fun resolvePlaybackEnded(
        behavior: PlaybackCompletionBehavior,
        autoPlayEnabled: Boolean,
        isExternalPlaylist: Boolean,
        externalPlaylistAutoContinueEnabled: Boolean,
        externalPlaylistSource: ExternalPlaylistSource,
        playMode: PlayMode,
        hasNextPageOrSeasonTarget: Boolean = false
    ): PlaybackEndAction {
        val action = resolvePlaybackEndActionForSession(
            behavior = behavior,
            autoPlayEnabled = autoPlayEnabled,
            isExternalPlaylist = isExternalPlaylist,
            externalPlaylistAutoContinueEnabled = externalPlaylistAutoContinueEnabled,
            externalPlaylistSource = externalPlaylistSource,
            playMode = playMode,
            hasNextPageOrSeasonTarget = hasNextPageOrSeasonTarget
        )
        sessionStore.recordCompletionAction(action)
        return action
    }

    fun executePlaybackEndAction(
        action: PlaybackEndAction,
        repeatCurrent: () -> Unit,
        playNextInOrder: (Boolean) -> Boolean,
        playNextFromPlaylistLoop: (Boolean) -> Boolean,
        autoContinue: (Boolean) -> Unit
    ): PlaybackEndExecutionOutcome {
        return when (action) {
            PlaybackEndAction.STOP -> PlaybackEndExecutionOutcome(
                shouldHidePlaybackEndedDialog = true
            )
            PlaybackEndAction.REPEAT_CURRENT -> {
                repeatCurrent()
                PlaybackEndExecutionOutcome()
            }
            PlaybackEndAction.PLAY_NEXT_IN_PLAYLIST -> PlaybackEndExecutionOutcome(
                shouldHidePlaybackEndedDialog = !playNextInOrder(true)
            )
            PlaybackEndAction.PLAY_NEXT_IN_PLAYLIST_LOOP -> PlaybackEndExecutionOutcome(
                shouldHidePlaybackEndedDialog = !playNextFromPlaylistLoop(true)
            )
            PlaybackEndAction.AUTO_CONTINUE -> {
                autoContinue(true)
                PlaybackEndExecutionOutcome()
            }
        }
    }
}
