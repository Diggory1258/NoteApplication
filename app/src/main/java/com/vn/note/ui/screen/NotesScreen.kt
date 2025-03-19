package com.vn.note.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vn.note.ext.navigateRoute
import com.vn.note.model.NoteUIModel
import com.vn.note.ui.theme.Typography
import com.vn.note.utils.ArgumentNav
import com.vn.note.utils.RouteNav
import com.vn.note.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NoteViewModel) {
    var isFirstLoad by remember { mutableStateOf(true) }
    val notes by viewModel.notes.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    fun getRouter(id: Int? = null): String {
        return if (id != null) RouteNav.DETAIL_NOTE.replace(
            "{${ArgumentNav.NOTE_ID}}",
            id.toString()
        )
        else RouteNav.CREATE_NOTE
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount

                if (isFirstLoad) {
                    isFirstLoad = false
                    return@collect
                }

                if (lastVisibleItem >= totalItems - 10 && totalItems > 0) {
                    viewModel.loadMore()
                }
            }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigateRoute(getRouter()) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pullToRefresh(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    onRefresh = {
                        viewModel.refreshData(isRefreshing = true)
                    })
        ) {

            LazyColumn(state = listState) {
                items(notes) { note ->
                    NoteItem(
                        note = note,
                        onClick = { navController.navigateRoute(getRouter(note.id)) },
                        onDelete = { viewModel.deleteNoteById(note) }
                    )
                }
                item {
                    if (viewModel.isLoading.collectAsState().value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun NoteItem(note: NoteUIModel, onClick: () -> Unit, onDelete: () -> Unit) {
    fun displayTitle(): String {
        return if (note.title?.isEmpty() == true) note.content.orEmpty() else note.title.orEmpty()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(16.dp, 8.dp))
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayTitle(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.titleLarge
                )
                Text(
                    text = note.updatedAt,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = Typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
