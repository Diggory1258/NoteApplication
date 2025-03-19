package com.vn.note.repository

import com.vn.note.model.NoteUIModel
import com.vn.note.storage.room.dao.NoteDao
import com.vn.note.storage.room.entity.NoteEntity
import com.vn.note.storage.room.entity.NoteEntity.Companion.toNoteUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val dao: NoteDao) {

    suspend fun getNotesPaged(offset: Int, pageSize: Int): List<NoteUIModel> {
        return dao.getPagedNotes(pageSize, offset).map { it.toNoteUiModel() }
    }

    fun observeChangeData():Flow<List<NoteUIModel>>{
        return dao.observeChange().map { it.map { it.toNoteUiModel() } }
    }

    suspend fun insert(note: NoteEntity) = dao.insert(note)
    suspend fun deleteById(noteId: Int) = dao.deleteById(noteId)
    suspend fun updateById(noteId: Int, title: String?, content: String?) {
        dao.updateById(noteId, title, content, System.currentTimeMillis())
    }
}