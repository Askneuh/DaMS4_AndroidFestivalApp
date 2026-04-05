package com.example.festivalapp.data.user.room

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUserStream(): Flow<List<User>>
    fun getUserStream(id: Int): Flow<User?>
    suspend fun refreshUsers()
    suspend fun deleteUser(userId: Int)
    suspend fun updateUserRole(userId: Int, newRole: String)
}
