package com.example.festivalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.editor.room.EditorDAO
import com.example.festivalapp.data.festival.FestivalDao
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
        FestivalEntity::class,
        TariffZoneEntity::class,
        User::class,
        Editor::class,
        Reservation::class,
        PlanZoneEntity::class
    ],
    version = 4, 
    exportSchema = false
)
abstract class FestivalDatabase : RoomDatabase() {

    // === Tes DAO ===
    abstract fun festivalDao(): FestivalDao
    abstract fun tariffZoneDao(): TariffZoneDao
    abstract fun planZoneDao(): PlanZoneDao
    
    // === Les DAO de l'équipe ===
    abstract fun userDAO(): UserDAO
    abstract fun editorDAO(): EditorDAO
    abstract fun reservationDAO(): ReservationDAO

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