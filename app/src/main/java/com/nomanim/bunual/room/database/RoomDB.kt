package com.nomanim.bunual.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nomanim.bunual.models.ModelImages
import com.nomanim.bunual.api.entity.ModelPhoneBrands
import com.nomanim.bunual.api.entity.ModelPhoneModels
import com.nomanim.bunual.api.entity.ModelPlaces
import com.nomanim.bunual.room.RoomServices

@Database(
    entities = arrayOf(
        ModelPhoneBrands::class, ModelPhoneModels::class, ModelImages::class, ModelPlaces::class
    ), version = 1
)

abstract class RoomDB : RoomDatabase() {

    abstract fun getDataFromRoom(): RoomServices

    companion object {

        @Volatile
        private var instance: RoomDB? = null

        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: makeDatabase(context).also { instance = it }
        }

        private fun makeDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, RoomDB::class.java, "roomDatabase"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}