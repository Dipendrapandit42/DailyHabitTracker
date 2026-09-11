package com.example.dailyhabittracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserProfileDao {

    @Insert
    suspend fun insertProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): UserProfile?
}