package com.example.festivalapp.data

import android.content.Context
import androidx.room.*
import com.example.festivalapp.data.editor.room.*
import com.example.festivalapp.data.festival.*
import com.example.festivalapp.data.reservation.room.*
import com.example.festivalapp.data.user.room.*
import com.example.festivalapp.data.publisher.room.*
import com.example.festivalapp.data.festival.FestivalEntity
import com.example.festivalapp.data.festival.TariffZoneDao
import com.example.festivalapp.data.festival.TariffZoneEntity
import com.example.festivalapp.data.festival.PlanZoneDao
import com.example.festivalapp.data.festival.PlanZoneEntity
import com.example.festivalapp.data.reservation.room.Reservation
import com.example.festivalapp.data.reservation.room.ReservationDAO
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserDAO

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
        PlanZoneEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class FestivalDatabase: RoomDatabase() {

    // DAOs
    abstract fun festivalDao(): FestivalDao
    abstract fun tariffZoneDao(): TariffZoneDao
    abstract fun planZoneDao(): PlanZoneDao

    // === Les DAO de l'équipe ===
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