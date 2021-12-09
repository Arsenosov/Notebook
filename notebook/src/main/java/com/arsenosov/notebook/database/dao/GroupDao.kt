package com.arsenosov.notebook.database.dao

import androidx.room.*
import com.arsenosov.notebook.database.entitites.Group
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM `Group`")
    fun getAll(): Flow<List<Group>>

    @Query("SELECT * FROM `Group` WHERE id = :id")
    suspend fun getById(id: Int): Group

    @Insert
    suspend fun insert(group: Group)

    @Delete
    suspend fun delete(group: Group)

    @Update
    suspend fun update(group: Group)
}