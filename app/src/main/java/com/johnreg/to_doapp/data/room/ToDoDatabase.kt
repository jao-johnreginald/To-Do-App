package com.johnreg.to_doapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.johnreg.to_doapp.data.models.ToDoData

@Database(entities = [ToDoData::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase: RoomDatabase() {

    /*
    The method has no body or argument because Room database is going to be handling it
    The database exposes DAOs through an abstract getter method for each DAO operation
     */
    abstract fun getToDoDao(): ToDoDao

    // Singleton
    companion object {

        /*
        Volatile - writes to this field are immediately made visible to other threads
        No matter what thread is running this code, don't store this variable in any thread cache
        I want you to store this centrally in main memory
         */
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        /*
        We want to have only one instance of our database class
        If INSTANCE is not null, then return that same INSTANCE
        If INSTANCE is null, then inside a synchronized block create an instance of our database class

        synchronized - do not allow this block of code to run on multiple threads
        If more than one thread tries to create an instance of the database at the same time,
        it will be blocked, it allows creation of only one instance at a time

        fallbackToDestructiveMigration() - whenever you change the Entity and increase the database version,
        this function will delete all data, call this if you know you may be changing your database schema
         */

        fun getDatabase(context: Context): ToDoDatabase {
            val tempInstance = INSTANCE
            return when {
                tempInstance != null -> tempInstance
                else -> synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ToDoDatabase::class.java,
                        "todo_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                    instance
                }
            }
        }

    }

}