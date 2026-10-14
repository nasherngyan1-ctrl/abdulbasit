package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppSettings
import com.example.data.model.PortfolioItem
import com.example.data.model.RadarItem
import com.example.data.model.SocialLinks
import kotlinx.coroutines.flow.Flow

@Dao
interface PortfolioDao {
    // Portfolio Items
    @Query("SELECT * FROM portfolio_items ORDER BY createdAt DESC")
    fun getAllPortfolioItems(): Flow<List<PortfolioItem>>

    @Query("SELECT * FROM portfolio_items WHERE type = :type ORDER BY createdAt DESC")
    fun getPortfolioItemsByType(type: String): Flow<List<PortfolioItem>>

    @Query("SELECT * FROM portfolio_items ORDER BY createdAt DESC")
    suspend fun getAllPortfolioItemsSync(): List<PortfolioItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioItem(item: PortfolioItem): Long

    @Delete
    suspend fun deletePortfolioItem(item: PortfolioItem)

    // Social Links
    @Query("SELECT * FROM social_links WHERE id = 1")
    fun getSocialLinks(): Flow<SocialLinks?>

    @Query("SELECT * FROM social_links WHERE id = 1")
    suspend fun getSocialLinksSync(): SocialLinks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSocialLinks(links: SocialLinks)

    // Radar Items
    @Query("SELECT * FROM radar_items ORDER BY discoveredAt DESC")
    fun getAllRadarItems(): Flow<List<RadarItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadarItem(item: RadarItem): Long

    @Query("DELETE FROM radar_items WHERE id = :id")
    suspend fun deleteRadarItem(id: Long)

    @Query("DELETE FROM radar_items")
    suspend fun clearAllRadarItems()

    // App Settings
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getAppSettings(): Flow<AppSettings?>

    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getAppSettingsSync(): AppSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAppSettings(settings: AppSettings)
}
