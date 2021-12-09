package com.arsenosov.notebook.database.entitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String,
    val email: String,
    val address: String,
    val phone: String,
    val groupId: Int,
    val info: String,
)
