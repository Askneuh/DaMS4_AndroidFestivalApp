package com.example.festivalapp.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserInfo(val login: String, val role: String)
@Serializable
data class LoginResponse(val message: String, val user: LoginUserInfo)