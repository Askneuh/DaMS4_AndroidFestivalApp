package com.example.festivalapp.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserInfo(val id: Int, val role: String)