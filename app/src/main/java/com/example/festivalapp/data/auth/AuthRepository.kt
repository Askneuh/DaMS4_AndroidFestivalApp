package com.example.festivalapp.data.auth

import com.example.festivalapp.data.APIService
import com.example.festivalapp.data.session.SessionRepository

class AuthRepository(
    private val api: AuthApiService,
    private val tokenManager: SessionRepository
) {
    suspend fun login(login: String, password: String): Result<String> {
        return try {
            val response = api.loginUser(LoginRequest(login, password))
            if (response.isSuccessful && response.body() != null) {
                val role = response.body()!!.user.role
                tokenManager.saveRole(role) // on sauvegarde le rôle plutôt que le token
                Result.success(role)
            } else {
                Result.failure(Exception("Login Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clearSession()
    }

    suspend fun register(login: String, password: String): Result<String> {
        return try {
            val response = api.register(LoginRequest(login, password))
            if (response.isSuccessful && response.body() != null) {
                Result.success(value = "Register Succeded")
            } else {
                Result.failure(Exception("Register Failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}

