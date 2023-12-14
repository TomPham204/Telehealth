package com.example.telehealth.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.telehealth.model.AppointmentModel
import com.example.telehealth.model.DoctorModel
import com.example.telehealth.model.ProfileModel

@Database(entities = [ProfileModel::class, AppointmentModel::class,
    DoctorModel::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun appointmentDao(): AppointmentDao
    abstract fun doctorDao(): DoctorDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext,
                    LocalDatabase::class.java, "telehealth_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
