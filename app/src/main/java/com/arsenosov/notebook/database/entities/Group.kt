package com.arsenosov.notebook.database.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String,
    val color: Int, //Argb
) {
    companion object {
        val NoGroup = Group(null, "Без группы", Color.White.toArgb())
    }
}
