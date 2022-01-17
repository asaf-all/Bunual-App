package com.nomanim.bunual.api.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "placesTable")
data class ModelPlaces(

    @ColumnInfo(name = "admin_name")
    @SerializedName("admin_name")
    val city: String,
    val population: String = "0" ) : Parcelable {


    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true)
    var key: Int = 0
}

