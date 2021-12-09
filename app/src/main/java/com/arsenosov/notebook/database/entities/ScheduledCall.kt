package com.arsenosov.notebook.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity
data class ScheduledCall(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val contactId: Int?,
    val groupId: Int,
    val time: Long,
)
