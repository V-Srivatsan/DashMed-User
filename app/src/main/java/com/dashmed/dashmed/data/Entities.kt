package com.dashmed.dashmed.data

import androidx.room.*

@Entity
data class Package (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)


@Entity
data class Item (
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val composition: String, // Many strings separated by ;
    val cost: Float,
    val expiration: Int,
)


@Entity(
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Package::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("packageID"),
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Item::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("itemID"),
            onDelete = ForeignKey.CASCADE
        )
    ),
    indices = [Index(value = ["packageID", "itemID"], unique = true)]
)
data class PackageItem (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageID: Int,
    val itemID: String,
    val quantity: Int,
)