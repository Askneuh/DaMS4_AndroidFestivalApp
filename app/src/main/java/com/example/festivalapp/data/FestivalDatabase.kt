package com.example.festivalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.festivalapp.data.festival.FestivalDao
import com.example.festivalapp.data.festival.FestivalEntity
import com.example.festivalapp.data.festival.TariffZoneDao
import com.example.festivalapp.data.festival.TariffZoneEntity
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserDAO

@Database(
    entities = [
        FestivalEntity::class,
        TariffZoneEntity::class,
        User::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FestivalDatabase : RoomDatabase() {
    abstract fun festivalDao(): FestivalDao
    abstract fun tariffZoneDao(): TariffZoneDao
    abstract fun userDAO(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: FestivalDatabase? = null

        fun getDatabase(context: Context): FestivalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FestivalDatabase::class.java,
                    "festival_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}