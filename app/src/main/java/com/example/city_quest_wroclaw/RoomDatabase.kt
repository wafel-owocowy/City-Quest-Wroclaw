package com.example.city_quest_wroclaw

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
// Klasa reprezentująca encję w bazie
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "audio_guide") val audioGuideId: Int
)
//Klasa będąca interfejsem do encji z możliwymi zapytaniami sql
@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location)

    @Query("SELECT * FROM locations")
    fun getAllUsers(): LiveData<List<Location>>

    @Query("SELECT * FROM locations WHERE id = :locationId LIMIT 1")
    suspend fun findById(locationId: Int): Location?

    @Delete
    suspend fun delete(location: Location)
}
// Klasa sklejająca encje i interfejsy w bazę danych
@Database(entities = [Location::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun LocationDao(): LocationDao
}
// inicjalizator bazy danych
object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "quest-db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}