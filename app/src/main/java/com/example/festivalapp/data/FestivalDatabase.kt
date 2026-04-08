package com.example.festivalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserDAO
import com.example.festivalapp.data.festival.room.FestivalEntity
import com.example.festivalapp.data.festival.room.FestivalGameEntity
import com.example.festivalapp.data.festival.room.FestivalDAO
import com.example.festivalapp.data.publisher.room.PublisherEntity
import com.example.festivalapp.data.publisher.room.GameEntity
import com.example.festivalapp.data.publisher.room.PublisherDAO
import com.example.festivalapp.data.publisher.room.GameDAO


@Database(
    entities = [
        User::class, 
        FestivalEntity::class, 
        PublisherEntity::class, 
        GameEntity::class, 
        FestivalGameEntity::class
    ], 
    version = 2, 
    exportSchema = false
)
abstract class FestivalDatabase: RoomDatabase() {

    abstract fun userDAO(): UserDAO
    abstract fun festivalDAO(): FestivalDAO
    abstract fun publisherDAO(): PublisherDAO
    abstract fun gameDAO(): GameDAO
    companion object {
        @Volatile
        private var Instance: FestivalDatabase? = null

        fun getDatabase(context: Context): FestivalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FestivalDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration() // On réinitialise pour ce passage v1 à v2
                    .build()
                    .also { Instance = it }
            }
        }
    }

}