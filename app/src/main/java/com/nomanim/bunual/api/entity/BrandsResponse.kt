package com.nomanim.bunual.api.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class BrandsResponse(

    @SerializedName("RECORDS")
    val body: List<Body>,
) {

    @Entity(tableName = "phone_brands_table")
    data class Body(
        @ColumnInfo(name = "id")
        val id: String,

        @ColumnInfo(name = "name")
        @SerializedName("name")
        val brandName: String
    ) {

        @PrimaryKey(autoGenerate = true)
        var key: Int = 0
    }
}

