package com.example.festivalapp.data.user.retrofit

import com.example.festivalapp.data.RetrofitInstance
import com.example.festivalapp.data.user.room.UserDto

class UserRepository {
    suspend fun getUsers() : List<UserDto> {
        return RetrofitInstance.api.getUsers()
    }


}



