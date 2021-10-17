package com.nomanim.bunual.retrofit.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "phoneBrandsTable")
data class ModelPhoneBrands(

    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val brandName: String ) {


    @PrimaryKey(autoGenerate = true)
    var key:Int = 0

}