package com.example.dailyhabittracker

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Habit::class,
        FoodItem::class,
        NutritionEntry::class,
        UserProfile::class,
        Exercise::class,
    ],
    version = 4,
    exportSchema = false,
)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun foodDao(): FoodDao

    abstract fun nutritionDao(): NutritionDao

    abstract fun userProfileDao(): UserProfileDao

    abstract fun exerciseDao(): ExerciseDao

    companion object {

        @Volatile
        private var INSTANCE: HabitDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    "ALTER TABLE habits ADD COLUMN dailyTarget REAL NOT NULL DEFAULT 0.0"
                )

                database.execSQL(
                    "ALTER TABLE habits ADD COLUMN targetUnit TEXT NOT NULL DEFAULT ''"
                )

                database.execSQL(
                    "ALTER TABLE habits ADD COLUMN todayProgress REAL NOT NULL DEFAULT 0.0"
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS food_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        calories REAL NOT NULL DEFAULT 0.0,
                        protein REAL NOT NULL DEFAULT 0.0,
                        carbohydrates REAL NOT NULL DEFAULT 0.0,
                        fat REAL NOT NULL DEFAULT 0.0,
                        fiber REAL NOT NULL DEFAULT 0.0,
                        vitaminA REAL NOT NULL DEFAULT 0.0,
                        vitaminC REAL NOT NULL DEFAULT 0.0,
                        calcium REAL NOT NULL DEFAULT 0.0,
                        iron REAL NOT NULL DEFAULT 0.0
                    )
                    """.trimIndent()
                )

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS nutrition_entries (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        foodName TEXT NOT NULL,
                        quantity REAL NOT NULL,
                        unit TEXT NOT NULL,
                        calories REAL NOT NULL DEFAULT 0.0,
                        protein REAL NOT NULL DEFAULT 0.0,
                        carbohydrates REAL NOT NULL DEFAULT 0.0,
                        fat REAL NOT NULL DEFAULT 0.0,
                        fiber REAL NOT NULL DEFAULT 0.0,
                        vitaminA REAL NOT NULL DEFAULT 0.0,
                        vitaminC REAL NOT NULL DEFAULT 0.0,
                        calcium REAL NOT NULL DEFAULT 0.0,
                        iron REAL NOT NULL DEFAULT 0.0,
                        date TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {

            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_profile (
                        id INTEGER NOT NULL,
                        age INTEGER NOT NULL,
                        gender TEXT NOT NULL,
                        weightKg REAL NOT NULL,
                        heightCm REAL NOT NULL,
                        activityLevel TEXT NOT NULL,
                        goal TEXT NOT NULL,
                        PRIMARY KEY(id)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {

            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS exercises (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        caloriesBurned REAL NOT NULL,
                        isCompleted INTEGER NOT NULL,
                        date TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): HabitDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}