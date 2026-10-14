package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppSettings
import com.example.data.model.PortfolioItem
import com.example.data.model.RadarItem
import com.example.data.model.SocialLinks

@Database(
    entities = [
        PortfolioItem::class,
        SocialLinks::class,
        RadarItem::class,
        AppSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun portfolioDao(): PortfolioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "abdulbasit_portfolio_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
