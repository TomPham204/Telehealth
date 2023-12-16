package com.example.telehealth.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.telehealth.data.dao.AppointmentDao
import com.example.telehealth.data.dataclass.AppointmentModel
import com.example.telehealth.data.dao.DoctorDao
import com.example.telehealth.data.dataclass.DoctorModel
import com.example.telehealth.data.dao.ProfileDao
import com.example.telehealth.data.dataclass.ProfileModel

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
