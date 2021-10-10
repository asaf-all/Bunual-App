package com.nomanim.bax.room.services

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nomanim.bax.models.ModelImages
import com.nomanim.bax.retrofit.models.ModelPhoneBrands
import com.nomanim.bax.retrofit.models.ModelPhoneModels

@Dao
interface RoomDatabaseDao {

    @Insert
    suspend fun insertModelNames(vararg phoneModel: ModelPhoneModels) : List<Long>

    @Insert
    suspend fun insertBrandNames(vararg phoneBrand: ModelPhoneBrands)

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

    @Query("SELECT * FROM imagesUri")
    suspend fun getImagesUriFromDb() : List<ModelImages>

}