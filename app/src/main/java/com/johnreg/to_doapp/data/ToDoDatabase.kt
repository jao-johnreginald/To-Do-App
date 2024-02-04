package com.johnreg.to_doapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.johnreg.to_doapp.data.models.ToDoData

@Database(entities = [ToDoData::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase: RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

    companion object {

        // Volatile - writes to this field are immediately made visible to other threads
        @Volatile
        private var INSTANCE: ToDoDatabase? = null

        /*
        We want to have only one instance of our database class
        If INSTANCE is not null, then return that same INSTANCE
        If INSTANCE is null, then inside a synchronized block create an instance of our database class

        We are using synchronized because we don't want multiple threads to create multiple instances
        With the synchronized block, we make sure that only 1 thread is able to access this code
        inside our synchronized block, and only 1 thread will be able to create an instance of this class
         */

        fun getDatabase(context: Context): ToDoDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            /*
            When a thread calls synchronized, it acquires the lock of that synchronized block
            Other threads don't have permission to call that same synchronized block
            as long as previous thread which had acquired the lock does not release the lock
             */
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }

    }

}