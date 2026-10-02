@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.android.bilipai.tv.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.android.bilipai.tv.TvCatalogState
import com.android.bilipai.tv.tvId
import com.android.purebilibili.data.model.response.VideoItem
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first

@Composable
fun TvVideoGrid(
    state: TvCatalogState, contentFocus: FocusRequester, navigationFocus: FocusRequester,
    onOpen: (VideoItem) -> Unit, onFocused: (String) -> Unit,
    onScroll: (Int, Int) -> Unit, modifier: Modifier = Modifier,
) {
    val ids = state.items.map { it.tvId() }
    val restoreIndex = remember(ids) { resolveTvFocusIndex(ids, state.focusedId, state.focusedIndex) }
    val requesters = remember { mutableMapOf<String, FocusRequester>() }
    ids.forEach { requesters.getOrPut(it) { FocusRequester() } }
    val gridState = remember { LazyGridState(state.firstVisibleIndex, state.firstVisibleOffset) }
    val preferredEntry = resolveTvFocusIndex(ids, state.focusedId, state.focusedIndex)
    val visible = gridState.layoutInfo.visibleItemsInfo
    val entryIndex = if (visible.isEmpty() || visible.any { it.index == preferredEntry }) preferredEntry
        else visible.firstOrNull()?.index

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.firstVisibleItemIndex to gridState.firstVisibleItemScrollOffset }
            .distinctUntilChanged().collect { (index, offset) -> onScroll(index, offset) }
    }
    // Request only once per mounted list. Appending pages must not steal focus from the user.
    LaunchedEffect(gridState) {
        val index = restoreIndex ?: return@LaunchedEffect
        snapshotFlow { gridState.layoutInfo.totalItemsCount > 0 }.first { it }
        if (gridState.layoutInfo.visibleItemsInfo.none { it.index == index }) gridState.scrollToItem(index)
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.any { it.index == index } }.first { it }
        requesters[ids[index]]?.requestFocus()
    }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val columns = (maxWidth.value / 240f).toInt().coerceIn(2, 6)
        LazyVerticalGrid(columns = GridCells.Fixed(columns), state = gridState,
            contentPadding = PaddingValues(12.dp), horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxSize().testTag("tv-grid")) {
            itemsIndexed(state.items, key = { _, item -> item.tvId() }) { index, item ->
                val requester = requesters.getValue(item.tvId())
                Card(onClick = { onOpen(item) }, modifier = Modifier
                    .focusRequester(if (index == entryIndex) contentFocus else requester)
                    .then(if (index == entryIndex) Modifier.focusRequester(requester) else Modifier)
                    .focusProperties { if (index % columns == 0) left = navigationFocus }
                    .onFocusChanged { if (it.isFocused) onFocused(item.tvId()) }
                    .testTag("video:${item.tvId()}")) {
                    Column {
                        AsyncImage(model = item.pic.let { if (it.startsWith("//")) "https:$it" else it },
                            contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f))
                        Text(item.title, style = MaterialTheme.typography.titleMedium, maxLines = 2,
                            overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(12.dp))
                        Text(item.owner.name, style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp))
                    }
                }
            }
        }
    }
}
