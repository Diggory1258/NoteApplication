package com.vn.note.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vn.note.model.NoteUIModel
import com.vn.note.repository.NoteRepository
import com.vn.note.storage.room.AppDatabase
import com.vn.note.storage.room.entity.NoteEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    private val _notes = MutableStateFlow<List<NoteUIModel>>(emptyList())
    val notes: StateFlow<List<NoteUIModel>> = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 0
    private val pageSize = 30

    init {
        val dao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(dao)
        viewModelScope.launch {
            repository.observeChangeData().collectLatest {
                loadNotes(if (_notes.value.isEmpty()) pageSize else _notes.value.size)
            }
        }
    }

    private suspend fun loadNotes(limit: Int) {
        currentPage = 0
        _notes.value = repository.getNotesPaged(0, limit)
    }


    fun getNoteById(noteId: Int?): NoteUIModel? {
        return notes.value.find { it.id == noteId }
    }

    fun action(noteId: Int?, title: String?, content: String?) {
        when (validateAction(noteId, title, content)) {
            Action.ADD -> addNote(title, content)
            Action.DELETE -> deleteNoteById(getNoteById(noteId))
            Action.UPDATE -> updateNote(noteId, title, content)
            else -> {
                // Nothing
            }
        }
    }

    private fun validateAction(noteId: Int?, title: String?, content: String?): Action = when {
        noteId == null && isAddNote(title, content) -> Action.ADD
        noteId != null && isDelete(title, content) -> Action.DELETE
        noteId != null && isChangeNote(noteId, title, content) -> Action.UPDATE
        else -> Action.NONE
    }

    private fun isDelete(title: String?, content: String?): Boolean {
        return title.isNullOrEmpty() && content.isNullOrEmpty()
    }

    private fun isAddNote(title: String?, content: String?): Boolean {
        return title?.isNotEmpty() == true || content?.isNotEmpty() == true
    }

    private fun isChangeNote(noteId: Int?, title: String?, content: String?): Boolean {
        val note = getNoteById(noteId)
        return note?.title != title || note?.content != content
    }

    fun loadMore() {
        if (_isLoading.value) return
        viewModelScope.launch {
            currentPage += 1
            _isLoading.value = true
            val result = repository.getNotesPaged(currentPage * pageSize, pageSize)
            _notes.value = (_notes.value + result).distinctBy { it.id }
            _isLoading.value = false
        }
    }

    fun refreshData(isRefreshing: Boolean) {
        currentPage = 0
        _isRefreshing.value = isRefreshing
        viewModelScope.launch {
            _notes.value = repository.getNotesPaged(currentPage * pageSize, pageSize)
            _isRefreshing.value = false
        }
    }

    private suspend fun createDataScroll() {
        for (i in 1..100) {
            delay(200L)
            addNote(title = "Title Note $i", content = "Content Not $i")
        }
    }

    private fun addNote(title: String?, content: String?) {
        viewModelScope.launch {
            repository.insert(
                NoteEntity(
                    title = title,
                    content = content,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteNoteById(noteUI: NoteUIModel?) {
        noteUI?.let {
            viewModelScope.launch {
                repository.deleteById(it.id)
            }
        }
    }

    private fun updateNote(noteId: Int?, title: String?, content: String?) {
        noteId?.let {
            viewModelScope.launch {
                repository.updateById(noteId, title, content)
            }
        }
    }
}

enum class Action {
    DELETE, ADD, UPDATE, NONE
}