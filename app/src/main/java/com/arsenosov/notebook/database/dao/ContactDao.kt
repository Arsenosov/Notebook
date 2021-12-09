package com.arsenosov.notebook.database.dao

import androidx.room.*
import com.arsenosov.notebook.database.entities.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM Contact GROUP BY groupId")
    fun getAll(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact WHERE id = :id")
    suspend fun getById(id: Int): Contact

    @Query("SELECT * FROM Contact WHERE groupId = :id")
    suspend fun getByGroupId(id: Int): List<Contact>

    @Query("SELECT * FROM Contact WHERE phone = :phone")
    suspend fun getAllByPhone(phone: String): List<Contact>

    @Insert
    suspend fun insert(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    @Update
    suspend fun update(contact: Contact)
}