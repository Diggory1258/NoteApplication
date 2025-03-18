package com.vn.note.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vn.note.R
import com.vn.note.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DetailScreen(navController: NavController, viewModel: NoteViewModel, noteId: Int?) {
    val note = viewModel.getNoteById(noteId)
    var title by rememberSaveable { mutableStateOf(note?.title ?: "") }
    var content by rememberSaveable { mutableStateOf(note?.content ?: "") }

    val focusRequester = remember { FocusRequester() }

    fun saveNoteAndExit() {
        viewModel.action(noteId, title, content)
        navController.popBackStack()
    }

    BackHandler {
        saveNoteAndExit()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextFieldCommon(
                        value = title,
                        textPlaceHolder = stringResource(R.string.str_hint_content),
                        textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .background(
                                Color.Transparent
                            ),
                        maxLine = 1,
                        onValueChange = { title = it },
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        saveNoteAndExit()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (noteId != null) {
                        IconButton(onClick = {
                            viewModel.deleteNoteById(note)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TextFieldCommon(
                value = content,
                textPlaceHolder = stringResource(R.string.str_hint_content),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                singleLine = false,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .focusRequester(focusRequester),
                maxLine = Int.MAX_VALUE,
                onValueChange = { content = it },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TextFieldCommon(
    value: String,
    textPlaceHolder: String,
    modifier: Modifier,
    maxLine: Int,
    textStyle: TextStyle,
    singleLine: Boolean,
    onValueChange: (updatedSearchText: String) -> Unit = {},
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle= textStyle,
        placeholder = { Text(textPlaceHolder) },
        singleLine = singleLine,
        modifier = modifier,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        maxLines = maxLine
    )
}







