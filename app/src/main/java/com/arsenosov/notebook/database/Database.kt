package com.arsenosov.notebook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arsenosov.notebook.database.dao.ContactDao
import com.arsenosov.notebook.database.dao.GroupDao
import com.arsenosov.notebook.database.dao.ScheduledDao
import com.arsenosov.notebook.database.entities.Contact
import com.arsenosov.notebook.database.entities.Group
import com.arsenosov.notebook.database.entities.ScheduledCall

@Database(
    entities = [
        Contact::class,
        Group::class,
        ScheduledCall::class],
    version = 5,
)
abstract class Database: RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun groupDao(): GroupDao
    abstract fun scheduledDao(): ScheduledDao
}