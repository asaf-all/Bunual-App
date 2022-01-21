package com.nomanim.bunual.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nomanim.bunual.api.entity.BrandsResponse
import com.nomanim.bunual.api.entity.ModelsResponse
import com.nomanim.bunual.api.entity.RegionsResponse
import com.nomanim.bunual.room.RoomServices

@Database(
    entities = arrayOf(
        BrandsResponse.Body::class,
        ModelsResponse.Body::class,
        RegionsResponse::class
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
            context.applicationContext, RoomDB::class.java, "room_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}