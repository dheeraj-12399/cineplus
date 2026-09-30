package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        Movie::class,
        TvShow::class,
        TvSeason::class,
        Episode::class,
        WatchHistory::class,
        MyListItem::class,
        UserProfile::class,
        AuthorizedPlaybackSource::class,
        CachedMediaEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class CineDatabase : RoomDatabase() {
    abstract fun dao(): CineDao

    companion object {
        @Volatile
        private var INSTANCE: CineDatabase? = null

        fun getInstance(context: Context): CineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CineDatabase::class.java,
                    "cinepulse_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
