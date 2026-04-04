package com.example.festivalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserDAO


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class FestivalDatabase: RoomDatabase() {

    abstract fun userDAO(): UserDAO
    companion object {
        @Volatile
        private var Instance: FestivalDatabase? = null

        fun getDatabase(context: Context): FestivalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FestivalDatabase::class.java, "item_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}