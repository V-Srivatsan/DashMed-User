package com.dashmed.dashmed.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class, Package::class, PackageItem::class], version = 1, exportSchema = false)
abstract class DB : RoomDatabase() {

    abstract fun packageDao(): PackageDao
    abstract fun itemDao(): ItemDao
    abstract fun packageItemDao(): PackageItemDao

    companion object {
        @Volatile
        private var INSTANCE: DB? = null

        fun getDatabase(context: Context): DB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DB::class.java,
                    "dashmed_database"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance
                return instance
            }
        }
    }

}