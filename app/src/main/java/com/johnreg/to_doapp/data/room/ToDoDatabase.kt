package com.johnreg.to_doapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.johnreg.to_doapp.data.models.Priority
import com.johnreg.to_doapp.data.models.ToDoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [ToDoData::class], version = 2, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase : RoomDatabase() {

    // The method has no body or argument because Room database is going to be handling it
    // The database exposes DAOs through an abstract getter method for each DAO operation
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

        Elvis operator - if it's not null, return left, otherwise, return right

        synchronized - do not allow this block of code to run on multiple threads
        If more than one thread tries to create an instance of the database at the same time,
        it will be blocked, it allows creation of only one instance at a time

        addCallback() - add data entries to the database by default

        fallbackToDestructiveMigration() - whenever you change the Entity and increase the database version,
        this function will delete all data, call this if you know you may be changing your database schema
         */
        fun getDatabase(context: Context, scope: CoroutineScope): ToDoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database"
                ).addCallback(TodoDatabaseCallback(scope)).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class TodoDatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Check if INSTANCE is not null, add data to database
            INSTANCE?.let { toDoDatabase ->
                // Can't use main thread for database operations, must use CoroutineScope
                scope.launch {
                    val toDoDao = toDoDatabase.getToDoDao()
                    val toDoList = getToDoList()
                    for (i in toDoList.indices) toDoDao.createItem(toDoList[i])
                }
            }
        }
        private fun getToDoList() = listOf(
            ToDoData(
                title = "Homework",
                priority = Priority.HIGH,
                description = """
                                Math
                                Geometry
                                Statistics
                                Biology
                                Chemistry
                                Physics
                            """.trimIndent()
            ),
            ToDoData(
                title = "Grocery Shop",
                priority = Priority.MEDIUM,
                description = """
                                Meat - Chicken or Turkey
                                Pasta - Italian
                                Rice - 1kg
                                Bread - For sandwiches
                                Breakfast cereal - For my morning routine
                                Butter
                                Milk - 1L
                                10 Eggs
                                Cheese
                            """.trimIndent()
            ),
            ToDoData(
                title = "House work",
                priority = Priority.LOW,
                description = "Moving the furniture with my brother."
            ),
            ToDoData(
                title = "Room cleaning",
                priority = Priority.MEDIUM,
                description = "Clean my room, and wash the carpet"
            ),
            ToDoData(
                title = "Birthday Gift",
                priority = Priority.HIGH,
                description = "Buy a gift for my girlfriend"
            ),
            ToDoData(
                title = "Dishes",
                priority = Priority.HIGH,
                description = "Wash the dishes before she comes back!"
            ),
            ToDoData(
                title = "Feed the dogs",
                priority = Priority.MEDIUM,
                description = """
                                1. Go to market
                                2. Search for dog food
                                3. Buy it and take it to shelters
                                4. Adopt one?
                            """.trimIndent()
            ),
            ToDoData(
                title = "Study",
                priority = Priority.MEDIUM,
                description = """
                                Study for the upcoming exams.
                                Pay a tutor.
                                Learn at least 4 hours a day.
                                Read two books a month.
                            """.trimIndent()
            ),
            ToDoData(
                title = "Gym",
                priority = Priority.LOW,
                description = """
                                Exercise every day.
                                At least 30 minutes a day.
                            """.trimIndent()
            ),
            ToDoData(
                title = "Bicycle",
                priority = Priority.LOW,
                description = "Buy one!"
            ),
            ToDoData(
                title = "Football",
                priority = Priority.MEDIUM,
                description = "Play football with friends."
            ),
            ToDoData(
                title = "Vacation",
                priority = Priority.HIGH,
                description = "Vacation with my family"
            ),
            ToDoData(
                title = "Visit grandma",
                priority = Priority.HIGH,
                description = "Don't forget this one!"
            ),
            ToDoData(
                title = "Wash the car",
                priority = Priority.LOW,
                description = "Wash and clean the car"
            ),
        )
    }

}