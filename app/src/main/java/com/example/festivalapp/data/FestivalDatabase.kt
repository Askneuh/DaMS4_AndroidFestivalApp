package com.example.festivalapp.data

import android.content.Context
<<<<<<< HEAD
import androidx.room.*
import com.example.festivalapp.data.editor.room.*
import com.example.festivalapp.data.festival.*
import com.example.festivalapp.data.reservation.room.*
import com.example.festivalapp.data.user.room.*
import com.example.festivalapp.data.game.room.*
import com.example.festivalapp.data.contact.room.*
import com.example.festivalapp.data.festival.FestivalEntity
import com.example.festivalapp.data.festival.TariffZoneDao
import com.example.festivalapp.data.festival.TariffZoneEntity
import com.example.festivalapp.data.festival.PlanZoneDao
import com.example.festivalapp.data.festival.PlanZoneEntity
=======
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.editor.room.EditorDAO
import com.example.festivalapp.data.game.room.Game
import com.example.festivalapp.data.game.room.GameDAO
import com.example.festivalapp.data.reservation.room.*
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserDAO

@Database(
    entities = [
        User::class,
        FestivalEntity::class,
        TariffZoneEntity::class,
        Editor::class,
        Reservation::class,
        PlanZoneEntity::class,
        Game::class,
        Contact::class,
        com.example.festivalapp.data.festival.room.FestivalGameEntity::class,
        ReservationGame::class,
        SuiviReservation::class
    ],
    version = 5, 
    exportSchema = false
)
abstract class FestivalDatabase: RoomDatabase() {

    // === Festivals & Zones ===
    abstract fun festivalDao(): FestivalDao
    abstract fun tariffZoneDao(): TariffZoneDao
    abstract fun planZoneDao(): PlanZoneDao

    // === Core App Entities ===
    abstract fun userDAO(): UserDAO
    abstract fun editorDAO(): EditorDAO
    abstract fun reservationDAO(): ReservationDAO
    abstract fun gameDAO(): GameDAO
    abstract fun contactDAO(): ContactDAO
    abstract fun reservationGameDAO(): ReservationGameDAO
    abstract fun suiviReservationDAO(): SuiviReservationDAO
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
