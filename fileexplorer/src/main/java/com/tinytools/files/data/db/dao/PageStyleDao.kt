package com.tinytools.files.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tinytools.files.data.db.model.PageStyle

@Dao
interface PageStyleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PageStyle)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: PageStyle)

    @Query("SELECT * FROM pageStyle WHERE path=:path LIMIT 1")
    suspend fun getPageStyle(path: String): PageStyle?
}
