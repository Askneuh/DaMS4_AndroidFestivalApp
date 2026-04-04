package com.example.festivalapp.data.user.room
import kotlinx.coroutines.flow.Flow


interface UserRepository {
    fun getAllUserStream(): Flow<List<User>>
    fun getUserStream(id: Int): Flow<User?>
    suspend fun insertUser(user: User)
    suspend fun deleteUser(user: User)
    suspend fun updateUser(user: User)
}