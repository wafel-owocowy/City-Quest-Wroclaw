package com.example.city_quest_wroclaw.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.city_quest_wroclaw.R
@TypeConverters(Converters::class)
@Database(entities = [Attraction::class, Visit::class, Achievement::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun attractionDao(): AttractionDao
    abstract fun VisitDao(): VisitDao
    abstract fun AchievementDao(): AchievementDao
    abstract fun StatisticsDao(): StatisticsDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "city_quest_database"
                )
                    .addCallback(AppDatabaseCallback())
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.attractionDao())
                }
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.attractionDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val cursor = db.query("SELECT COUNT(*) FROM attractions")
                        if (cursor.moveToFirst()) {
                            val count = cursor.getInt(0)
                            if (count == 0) {
                                populateDatabase(database.attractionDao())
                            }
                        }
                        cursor.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: AttractionDao) {
            val wroclawAttractions = listOf(
                Attraction(
                    id = 1,
                    name = "Rynek Główny",
                    description = "Serce Wrocławia, jedno z największych założeń urbanistycznych tego typu w Europie.",
                    latitude = 51.1093,
                    longitude = 17.0326,
                    imageResId = R.drawable.attraction_1,
                    videoResId = R.raw.rynek_video
                ),
                Attraction(
                    id = 2,
                    name = "Hala Stulecia",
                    description = "Zabytek wpisany na Listę Światowego Dziedzictwa UNESCO.",
                    latitude = 51.1069,
                    longitude = 17.0772,
                    imageResId = R.drawable.attraction_2,
                    videoResId = R.raw.hala_stulecia_video
                ),
                Attraction(
                    id = 3,
                    name = "Ostrów Tumski",
                    description = "Najstarsza, zabytkowa część Wrocławia z katedrą św. Jana Chrzciciela.",
                    latitude = 51.1141,
                    longitude = 17.0461,
                    imageResId = R.drawable.attraction_3
                ),
                Attraction(
                    id = 4,
                    name = "Panorama Racławicka",
                    description = "Wielkie malowidło przedstawiające bitwę pod Racławicami.",
                    latitude = 51.1101,
                    longitude = 17.0444,
                    imageResId = R.drawable.attraction_4,
                    videoResId = R.raw.panorama_raclawicka_video,
                    audioResId = R.raw.panorama_raclawicka_audio
                ),
                Attraction(
                    id = 5,
                    name = "ZOO Wrocław",
                    description = "Najstarszy ogród zoologiczny w Polsce, znany z Afrykarium.",
                    latitude = 51.1047,
                    longitude = 17.0746,
                    imageResId = R.drawable.attraction_5
                ),
                Attraction(id = 6, name = "Ogród Japoński", description = "Relikt Wystawy Stulecia, piękny ogród w stylu japońskim.", latitude = 51.1090, longitude = 17.0788, imageResId = R.drawable.attraction_6),
                Attraction(id = 7, name = "Most Grunwaldzki", description = "Słynny most wiszący we Wrocławiu.", latitude = 51.1096, longitude = 17.0544, imageResId = R.drawable.attraction_7),
                Attraction(id = 8, name = "Hydropolis", description = "Centrum wiedzy o wodzie.", latitude = 51.1039, longitude = 17.0577, imageResId = R.drawable.attraction_8),
                Attraction(id = 9, name = "Kolejkowo", description = "Największa makieta kolejowa w Polsce.", latitude = 51.0975, longitude = 17.0201, imageResId = R.drawable.attraction_9),
                Attraction(id = 10, name = "Sky Tower", description = "Najwyższy budynek we Wrocławiu z punktem widokowym.", latitude = 51.0945, longitude = 17.0185, imageResId = R.drawable.attraction_10),
                Attraction(id = 11, name = "Opera Wrocławska", description = "Zabytkowy budynek opery.", latitude = 51.1054, longitude = 17.0312, imageResId = R.drawable.attraction_11),
                Attraction(id = 12, name = "Narodowe Forum Muzyki", description = "Nowoczesna sala koncertowa.", latitude = 51.1065, longitude = 17.0276, imageResId = R.drawable.attraction_12),
                Attraction(id = 13, name = "Wrocławskie Krasnale", description = "Setki małych figurek rozsianych po mieście.", latitude = 51.1095, longitude = 17.0322, imageResId = R.drawable.attraction_13),
                Attraction(id = 14, name = "Dzielnica Czterech Wyznań", description = "Obszar tolerancji i współistnienia religii.", latitude = 51.1075, longitude = 17.0254, imageResId = R.drawable.attraction_14),
                Attraction(id = 15, name = "Park Południowy", description = "Jeden z najpiękniejszych parków miejskich we Wrocławiu.", latitude = 51.0768, longitude = 17.0163, imageResId = R.drawable.attraction_15)
            )

            wroclawAttractions.forEach { dao.insert(it) }
        }
    }
}
