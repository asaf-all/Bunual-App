package com.nomanim.bunual.api.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class ModelsResponse(

    @SerializedName("RECORDS")
    val body: List<Body>
) {

    @Entity(tableName = "phone_models_table")
    data class Body(
        @ColumnInfo(name = "id")
        val id: String,

        @ColumnInfo(name = "picture")
        @SerializedName("picture")
        val modelImage: String,

        @ColumnInfo(name = "brand_id")
        @SerializedName("brand_id")
        val brandId: String,

        @ColumnInfo(name = "name")
        @SerializedName("name")
        val modelName: String
    ) {

        @PrimaryKey(autoGenerate = true)
        var key: Int = 0
    }
}

