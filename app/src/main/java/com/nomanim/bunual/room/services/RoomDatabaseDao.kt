package com.nomanim.bunual.room.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nomanim.bunual.models.ModelImages
import com.nomanim.bunual.retrofit.models.ModelPhoneBrands
import com.nomanim.bunual.retrofit.models.ModelPhoneModels
import com.nomanim.bunual.retrofit.models.ModelPlaces

@Dao
interface RoomDatabaseDao {

    @Insert
    suspend fun insertModelNames(vararg phoneModel: ModelPhoneModels) : List<Long>

    @Insert
    suspend fun insertBrandNames(vararg phoneBrand: ModelPhoneBrands)

    @Insert
    suspend fun insertPlaceNames(vararg placeName: ModelPlaces)

    @Insert
    suspend fun insertImagesUri(vararg imagesUri: ModelImages)

    @Query("DELETE FROM phoneBrandsTable")
    suspend fun deleteBrandNames()

    @Query("DELETE FROM phoneModelsTable")
    suspend fun deleteModelNames()

    @Query("DELETE FROM imagesUri")
    suspend fun deleteImagesUri()

    @Query("SELECT * FROM phoneBrandsTable")
    suspend fun getBrandNamesFromDb() : List<ModelPhoneBrands>

    @Query("SELECT * FROM phoneModelsTable")
    suspend fun getModelNamesFromDb() : List<ModelPhoneModels>

    @Query("SELECT * FROM placesTable")
    suspend fun getPlaceNamesFromDb() : List<ModelPlaces>

    @Query("SELECT * FROM imagesUri")
    suspend fun getImagesUriFromDb() : List<ModelImages>


}