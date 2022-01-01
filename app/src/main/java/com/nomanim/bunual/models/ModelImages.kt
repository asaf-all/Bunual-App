package com.nomanim.bunual.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imagesUri")
data class ModelImages(

    @ColumnInfo(name = "imageUri")
    val imageUri: String) {

    @PrimaryKey(autoGenerate = true)
    var key: Int = 0
}