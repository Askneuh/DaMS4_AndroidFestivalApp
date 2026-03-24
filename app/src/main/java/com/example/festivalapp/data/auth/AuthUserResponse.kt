package com.example.festivalapp.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserResponse(val message: String, val user: AuthUserInfo)