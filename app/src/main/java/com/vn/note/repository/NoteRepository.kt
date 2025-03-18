package com.vn.note.repository

import com.vn.note.model.NoteUIModel
import com.vn.note.storage.room.dao.NoteDao
import com.vn.note.storage.room.entity.NoteEntity
import com.vn.note.storage.room.entity.NoteEntity.Companion.toNoteUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepository(private val dao: NoteDao) {

    fun getNotesPaged(page: Int, pageSize: Int): Flow<List<NoteUIModel>> {
        val offset = page * pageSize
        return dao.getPagedNotes(pageSize, offset)
            .map { list -> list.map { it.toNoteUiModel() } }
    }

    suspend fun insert(note: NoteEntity) = dao.insert(note)
    suspend fun deleteById(noteId: Int) = dao.deleteById(noteId)
    suspend fun updateById(noteId: Int, title: String?, content: String?) {
        dao.updateById(noteId, title, content, System.currentTimeMillis())
    }
}