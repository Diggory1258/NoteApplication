package com.vn.note.storage.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vn.note.ext.convertMillisToFormattedString
import com.vn.note.model.NoteUIModel

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String? = null,
    val content: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
) {
    companion object {
        fun NoteEntity.toNoteUiModel(): NoteUIModel {
            return NoteUIModel(
                id = this.id,
                title = this.title,
                content = this.content,
                updatedAt = this.updatedAt?.convertMillisToFormattedString().orEmpty(),
                createdAt = this.updatedAt?.convertMillisToFormattedString().orEmpty()
            )
        }
    }
}
