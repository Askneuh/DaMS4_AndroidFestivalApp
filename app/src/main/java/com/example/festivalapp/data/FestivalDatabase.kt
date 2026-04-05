package com.example.festivalapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.festivalapp.data.editor.room.Editor
import com.example.festivalapp.data.editor.room.EditorDAO
import com.example.festivalapp.data.reservation.room.Reservation
import com.example.festivalapp.data.reservation.room.ReservationDAO
import com.example.festivalapp.data.user.room.User
import com.example.festivalapp.data.user.room.UserDAO


@Database(entities = [User::class, Editor::class, Reservation::class], version = 2, exportSchema = false)
abstract class FestivalDatabase: RoomDatabase() {

    abstract fun userDAO(): UserDAO
    abstract fun editorDAO(): EditorDAO
    abstract fun reservationDAO(): ReservationDAO
    companion object {
        @Volatile
        private var Instance: FestivalDatabase? = null

        fun getDatabase(context: Context): FestivalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FestivalDatabase::class.java, "item_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}