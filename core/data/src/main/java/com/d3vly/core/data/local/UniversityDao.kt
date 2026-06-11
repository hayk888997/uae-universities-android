package com.d3vly.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UniversityDao {
    @Query("SELECT * FROM universities ORDER BY name ASC")
    suspend fun getUniversities(): List<UniversityEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUniversities(universities: List<UniversityEntity>)

    @Query("DELETE FROM universities")
    suspend fun clearUniversities()

    @Transaction
    suspend fun replaceUniversities(universities: List<UniversityEntity>) {
        clearUniversities()
        insertUniversities(universities)
    }
}
