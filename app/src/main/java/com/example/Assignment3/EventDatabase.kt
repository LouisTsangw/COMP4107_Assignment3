//package com.example.Assignment3
//
//import android.content.Context
//import androidx.room.*
//import kotlinx.coroutines.flow.Flow
//
//@Dao
//interface EventDatabaseDao {
//    @Query("SELECT * from event")
//    fun getAll(): Flow<List<Event>>
//
//    @Query("SELECT * from event where deptId = :id")
//    fun getByDeptId(id: String): Flow<List<Event>>
//
//    @Query("SELECT * from event WHERE saved = 1") // Query for saved events
//    fun getSavedEvents(): Flow<List<Event>>
//
//    @Update
//    suspend fun update(event: Event)
//}
//
//@Database(entities = [Event::class], version = 1, exportSchema = false)
//abstract class EventDatabase : RoomDatabase() {
//    abstract fun eventDao(): EventDatabaseDao
//
//    companion object {
//        private var INSTANCE: EventDatabase? = null
//        fun getInstance(context: Context): EventDatabase {
//            synchronized(this) {
//                var instance = INSTANCE
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        EventDatabase::class.java,
//                        "event_database"
//                    )
//                        .createFromAsset("database/events.db")
//                        .fallbackToDestructiveMigration()
//                        .build()
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
//    }
//}