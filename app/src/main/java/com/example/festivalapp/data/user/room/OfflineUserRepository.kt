package com.example.festivalapp.data.user.room

import kotlinx.coroutines.flow.Flow

class OfflineUserRepository(private val userDAO: UserDAO) : UserRepository {
    override fun getAllUserStream(): Flow<List<User>> = userDAO.getAllUsers()

    override fun getUserStream(id: Int): Flow<User?> = userDAO.getUser(id)

    override suspend fun insertUser(user: User) = userDAO.insert(user)

    override suspend fun deleteUser(user: User) = userDAO.delete(user)

    override suspend fun updateUser(user: User) = userDAO.update(user)
}