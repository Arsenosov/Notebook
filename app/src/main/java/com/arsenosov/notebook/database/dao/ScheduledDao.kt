package com.arsenosov.notebook.database.dao

import androidx.room.*
import com.arsenosov.notebook.database.entities.Contact
import com.arsenosov.notebook.database.entities.Group
import com.arsenosov.notebook.database.entities.ScheduledCall
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledDao {

    @Query("SELECT * FROM ScheduledCall ORDER BY time ASC")
    fun getAll(): Flow<List<ScheduledCall>>

    @Query("SELECT * FROM ScheduledCall WHERE contactId = :contact")
    suspend fun getByContact(contact: Int): List<ScheduledCall>

    @Query("SELECT * FROM ScheduledCall WHERE groupId = :group")
    suspend fun getByGroup(group: Int): List<ScheduledCall>

    @Insert
    suspend fun insert(scheduledCall: ScheduledCall)

    @Update
    suspend fun update(scheduledCall: ScheduledCall)

    @Delete
    suspend fun delete(scheduledCall: ScheduledCall)
}