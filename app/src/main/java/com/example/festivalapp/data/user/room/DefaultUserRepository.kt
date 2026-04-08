package com.example.festivalapp.data.user.room

import com.example.festivalapp.data.user.retrofit.UserApiService
import com.example.festivalapp.data.user.retrofit.UpdateRoleRequest
import com.example.festivalapp.data.user.retrofit.toRoomEntity
import kotlinx.coroutines.flow.Flow

class DefaultUserRepository(
    private val userDAO: UserDAO,
    private val api: UserApiService
) : UserRepository {

    override fun getAllUserStream(): Flow<List<User>> = userDAO.getAllUsers()
    override fun getUserStream(id: Int): Flow<User?> = userDAO.getUser(id)

    override suspend fun refreshUsers() {
        val remoteUsers = api.getUsers()
        userDAO.deleteAll()
        remoteUsers.map { it.toRoomEntity() }.forEach { userDAO.insert(it) }
    }

    override suspend fun deleteUser(userId: Int) {
        api.deleteUser(userId)
        refreshUsers()
    }

    override suspend fun updateUserRole(userId: Int, newRole: String) {
        api.updateUserRole(userId, UpdateRoleRequest(role = newRole))
        refreshUsers()
    }
}
