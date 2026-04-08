package com.example.festivalapp.data

import android.content.Context
import androidx.room.*
import com.example.festivalapp.data.editor.room.*
import com.example.festivalapp.data.festival.*
import com.example.festivalapp.data.reservation.room.*
import com.example.festivalapp.data.user.room.*
import com.example.festivalapp.data.publisher.room.*
import com.example.festivalapp.data.festival.room.FestivalGameEntity

@Database(
    entities = [
        User::class, 
        FestivalEntity::class, 
        TariffZoneEntity::class,
        PublisherEntity::class, 
        GameEntity::class, 
        FestivalGameEntity::class,
        Reservation::class,
        Editor::class
    ], 
    version = 2, 
    exportSchema = false
)
abstract class FestivalDatabase: RoomDatabase() {

    // DAOs
    abstract fun festivalDao(): FestivalDao
    abstract fun tariffZoneDao(): TariffZoneDao
    abstract fun userDAO(): UserDAO
    abstract fun publisherDAO(): PublisherDAO
    abstract fun gameDAO(): GameDAO
    abstract fun reservationDAO(): ReservationDAO
    abstract fun editorDAO(): EditorDAO

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