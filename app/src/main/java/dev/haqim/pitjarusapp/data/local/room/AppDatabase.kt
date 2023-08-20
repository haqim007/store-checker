package dev.haqim.pitjarusapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.haqim.pitjarusapp.data.local.entity.StoreEntity
import dev.haqim.pitjarusapp.data.local.room.dao.StoreDao

@Database(
    entities = [
        StoreEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase(){
    
    abstract fun storeDao(): StoreDao
    
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pitjarus.db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { 
                        INSTANCE = it
                    }
            }
        }
    }
    
}