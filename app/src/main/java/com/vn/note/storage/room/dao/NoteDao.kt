package com.vn.note.storage.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vn.note.storage.room.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun observeChange(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getPagedNotes(limit: Int, offset: Int): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteById(noteId: Int)

    @Query("UPDATE notes SET title =:title, content = :content, updatedAt =:updatedAt WHERE id = :noteId")
    suspend fun updateById(noteId: Int, title: String?, content: String?, updatedAt: Long)

    @Update
    suspend fun update(note: NoteEntity)
}