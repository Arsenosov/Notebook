package com.arsenosov.notebook.database.entitites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScheduledCall(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val contactId: Int?,
    val groupId: Int,
    val time: Long,
)
