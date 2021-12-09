package com.arsenosov.notebook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arsenosov.notebook.database.dao.ContactDao
import com.arsenosov.notebook.database.dao.GroupDao
import com.arsenosov.notebook.database.dao.ScheduledDao
import com.arsenosov.notebook.database.entitites.Contact
import com.arsenosov.notebook.database.entitites.Group
import com.arsenosov.notebook.database.entitites.ScheduledCall

@Database(
    entities = [
        Contact::class,
        Group::class,
        ScheduledCall::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class Database: RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun groupDao(): GroupDao
    abstract fun scheduledDao(): ScheduledDao
}