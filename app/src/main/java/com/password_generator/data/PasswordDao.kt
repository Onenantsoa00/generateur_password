package com.password_generator.data

import androidx.room.*

@Dao
interface PasswordDao {
    @Insert
    suspend fun insert(password: Password)

    @Query("SELECT * FROM passwords")
    suspend fun getAllPasswords(): List<Password>

    @Delete
    suspend fun delete(password: Password)

    @Update
    suspend fun update(password: Password)
}