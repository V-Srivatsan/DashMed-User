package com.dashmed.dashmed.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {
    @Insert
    suspend fun insert(item: Package)

    @Update
    suspend fun update(item: Package)

    @Delete
    suspend fun delete(item: Package)

    @Query("SELECT * FROM Package ORDER BY id DESC")
    fun getPackages(): Flow<List<Package>>

    @Query("SELECT * FROM Package WHERE id = :id")
    suspend fun getPackage(id: Int): Package

    @Query("DELETE FROM Package")
    suspend fun truncate()
}


@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM Item WHERE id = :uid")
    suspend fun getItem(uid: String): Item

    @Query("DELETE FROM Item")
    suspend fun truncate()
}


@Dao
interface PackageItemDao {
    @Insert
    suspend fun insert(item: PackageItem)

    @Update
    suspend fun update(item: PackageItem)

    @Delete
    suspend fun delete(item: PackageItem)

    @Query("SELECT * FROM PackageItem WHERE packageID = :packageId")
    suspend fun getPackageItems(packageId: Int): List<PackageItem>

    @Query("SELECT COUNT(*) FROM PackageItem WHERE itemID = :itemId AND packageID = :packageId ")
    suspend fun isItemInPackage(itemId: String, packageId: Int): Int

    @Query("SELECT * FROM PackageItem WHERE itemID = :itemId AND packageID = :packageId")
    suspend fun getPackageItem(itemId: String, packageId: Int): PackageItem

    @Query("SELECT * FROM PackageItem JOIN Item ON PackageItem.itemID=Item.id WHERE packageID = :id")
    fun getItemsOfPackage(id: Int): Flow<List<Item>>

    @Query("DELETE FROM PackageItem")
    suspend fun truncate()
}