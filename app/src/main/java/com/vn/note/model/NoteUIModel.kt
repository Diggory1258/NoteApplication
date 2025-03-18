package com.vn.note.model

data class NoteUIModel(
    val id: Int,
    val title: String? = null,
    val content: String? = null,
    val updatedAt: String,
    val createdAt: String,
)