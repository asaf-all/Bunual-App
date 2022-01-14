package com.nomanim.bunual.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nomanim.bunual.models.ModelImages
import com.nomanim.bunual.api.entity.ModelPhoneBrands
import com.nomanim.bunual.api.entity.ModelPhoneModels
import com.nomanim.bunual.api.entity.ModelPlaces

@Dao
interface RoomServices {

    @Insert
    suspend fun insertModelNames(vararg phoneModel: ModelPhoneModels) : List<Long>

    @Insert
    suspend fun insertBrandNames(vararg phoneBrand: ModelPhoneBrands)

    @Insert
    suspend fun insertPlaceNames(vararg placeName: ModelPlaces)

    @Query("DELETE FROM phoneBrandsTable")
    suspend fun deleteBrandNames()

    @Query("DELETE FROM phoneModelsTable")
    suspend fun deleteModelNames()

    @Query("SELECT * FROM phoneBrandsTable")
    suspend fun getBrandNamesFromDb() : List<ModelPhoneBrands>

    @Query("SELECT * FROM phoneModelsTable")
    suspend fun getModelNamesFromDb() : List<ModelPhoneModels>

}