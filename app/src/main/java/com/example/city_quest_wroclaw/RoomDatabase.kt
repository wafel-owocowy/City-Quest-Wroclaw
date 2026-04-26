package com.example.city_quest_wroclaw

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.Date

// Klasy reprezentująca encje w bazie
// lokalizacja
@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "audio_guide") val audioGuideId: Int,
    @ColumnInfo(name = "lat") val latitude: Float,
    @ColumnInfo(name = "long") val longitude: Float
)
// i interfejs do niej
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
@Entity(tableName = "history")
data class Visit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "timestamp") val time: Date,
    @ColumnInfo(name = "locationId") val locationId: Int,
)
data class VisitWithLocation(
    @Embedded
    val location: Location,
    @Relation(
        entity = Visit::class,
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val visitList: List<Visit>
)
@Dao
interface VisitDao{
    @Transaction
    @Query("SELECT * FROM history WHERE locationId=:visitId")
    fun getCategoryWithItsQuotes(visitId: Int): List<VisitWithLocation>
}
// dziwactwo na przechowywanie dat
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
// Encja osiągnięć
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val dateObtained: Date?,
)
// i interfejs do niej
@Dao
interface AchievementDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: Achievement)

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): LiveData<List<Location>>

    @Query("SELECT * FROM achievements WHERE id = :achievementID LIMIT 1")
    suspend fun findById(achievementID: Int): Location?
}
@Entity(tableName = "stats")
data class Statistics(
    @ColumnInfo(name = "username") val username: String,
    @ColumnInfo(name = "distance") val distance: Float
)
@Dao
interface StatisticsDao{
    @Query("SELECT username FROM stats")
    fun getUsername(): String

    @Query("SELECT distance FROM stats")
    fun getTotalDistance(): Float

    @Query("SELECT COUNT(*) FROM history")
    fun getVisitCount(): Int
}
//Klasa będąca interfejsem do encji z możliwymi zapytaniami sql

// Klasa sklejająca encje i interfejsy w bazę danych
@TypeConverters(Converters::class)
@Database(entities = [Location::class, Visit::class, Achievement::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun LocationDao(): LocationDao
    abstract fun VisitDao(): VisitDao
    abstract fun AchievementDao(): AchievementDao
    abstract fun StatisticsDao(): StatisticsDao
}
// inicjalizator bazy danych (chyba powinien bć gdzieś indziej)
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