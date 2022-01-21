package com.nomanim.bunual.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nomanim.bunual.api.entity.BrandsResponse
import com.nomanim.bunual.api.entity.ModelsResponse
import com.nomanim.bunual.api.entity.RegionsResponse

@Dao
interface RoomServices {

    @Insert
    suspend fun insertModelNames(vararg phoneModelResponse: ModelsResponse.Body)

    @Insert
    suspend fun insertBrandNames(vararg phoneBrandResponse: BrandsResponse.Body)

    @Insert
    suspend fun insertPlaceNames(vararg placeName: RegionsResponse)

    @Query("DELETE FROM phone_brands_table")
    suspend fun deleteBrandNames()

    @Query("DELETE FROM phone_models_table")
    suspend fun deleteModelNames()

    @Query("SELECT * FROM phone_brands_table")
    suspend fun getBrandNamesFromDb() : List<BrandsResponse.Body>

    @Query("SELECT * FROM phone_models_table")
    suspend fun getModelNamesFromDb() : List<ModelsResponse.Body>

}