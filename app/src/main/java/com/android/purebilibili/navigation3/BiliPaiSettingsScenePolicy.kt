package com.android.purebilibili.navigation3

import com.android.purebilibili.feature.settings.isSettingsSubtreeNavKey

/** Settings is the stable outer identity; category/detail changes stay inside its scene. */
internal fun resolveSettingsSceneDisplayStack(stack: List<BiliPaiNavKey>): List<BiliPaiNavKey> {
    val result = mutableListOf<BiliPaiNavKey>()
    var insideSettings = false
    var hasSettingsScene = false
    for (key in stack) {
        if (isSettingsSubtreeNavKey(key)) {
            if (!insideSettings) {
                result += if (!hasSettingsScene) BiliPaiNavKey.Settings else key
                hasSettingsScene = true
            }
            insideSettings = true
        } else {
            result += key
            insideSettings = false
        }
    }
    return result
}

internal fun resolveSettingsSceneSegment(
    stack: List<BiliPaiNavKey>,
    anchor: BiliPaiNavKey,
): List<BiliPaiNavKey> {
    val index = if (anchor == BiliPaiNavKey.Settings) {
        stack.indexOfFirst(::isSettingsSubtreeNavKey)
    } else {
        stack.indexOf(anchor)
    }
    if (index < 0) return emptyList()
    return stack.drop(index).takeWhile(::isSettingsSubtreeNavKey)
}

internal fun resolveSettingsDetailStack(segment: List<BiliPaiNavKey>): List<BiliPaiNavKey> =
    segment.dropWhile { it == BiliPaiNavKey.Settings }

internal fun exitSettingsSceneStack(stack: List<BiliPaiNavKey>): List<BiliPaiNavKey> {
    val retained = stack.dropLastWhile(::isSettingsSubtreeNavKey)
    return retained.ifEmpty { listOf(BiliPaiNavKey.MainHost) }
}

/** Single pane returns to the category list, including a settings tab's virtual list root. */
internal fun popSettingsSceneStack(stack: List<BiliPaiNavKey>): List<BiliPaiNavKey> {
    if (stack.lastOrNull()?.let(::isSettingsSubtreeNavKey) != true) return stack
    val popped = popBiliPaiNavKey(stack)
    return if (stack.last() != BiliPaiNavKey.Settings &&
        popped.lastOrNull()?.let(::isSettingsSubtreeNavKey) != true
    ) {
        pushBiliPaiNavKey(popped, BiliPaiNavKey.Settings)
    } else {
        popped
    }
}

internal fun shouldExitSettingsSceneOnBack(
    stack: List<BiliPaiNavKey>,
    persistentPanes: Boolean,
): Boolean {
    val segment = stack.takeLastWhile(::isSettingsSubtreeNavKey)
    if (segment.isEmpty()) return false
    return segment.last() == BiliPaiNavKey.Settings ||
        (persistentPanes && resolveSettingsDetailStack(segment).size <= 1)
}
