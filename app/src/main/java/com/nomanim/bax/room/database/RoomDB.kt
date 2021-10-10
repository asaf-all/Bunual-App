package com.nomanim.bax.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nomanim.bax.models.ModelImages
import com.nomanim.bax.retrofit.models.ModelPhoneBrands
import com.nomanim.bax.retrofit.models.ModelPhoneModels
import com.nomanim.bax.room.services.RoomDatabaseDao

@Database(entities = arrayOf(ModelPhoneBrands::class, ModelPhoneModels::class, ModelImages::class),version = 1)
abstract class RoomDB : RoomDatabase() {

    abstract fun getDataFromRoom() : RoomDatabaseDao


    companion object {

        @Volatile private var instance : RoomDB? = null

        private val lock = Any()

        operator fun invoke(context : Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also { instance = it }
        }

        private fun makeDatabase(context : Context) = Room.databaseBuilder(
            context.applicationContext,RoomDB::class.java,"roomDatabase")
            .fallbackToDestructiveMigration()
            .build()
    }
}