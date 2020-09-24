package com.tinytools.files.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tinytools.files.data.db.AppDatabase.Companion.DB_VERSION
import com.tinytools.files.data.db.dao.PageStyleDao
import com.tinytools.files.data.db.model.PageStyle

@Database(
        entities = [PageStyle::class],
        version = DB_VERSION,
        exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getPageStyleDao(): PageStyleDao

    companion object {
        const val DB_VERSION = 1
        private const val DB_NAME = "files.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: build(context).also { INSTANCE = it }
                }

        private fun build(context: Context) =
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
    }
}
