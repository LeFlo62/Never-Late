package fr.isep.mobiledev.neverlate

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import fr.isep.mobiledev.neverlate.converter.PuzzleConverter
import fr.isep.mobiledev.neverlate.converter.RuleConverter
import fr.isep.mobiledev.neverlate.dao.AlarmDao
import fr.isep.mobiledev.neverlate.entities.Alarm

@Database(entities = [Alarm::class], version = 12)
@TypeConverters(RuleConverter::class, PuzzleConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao

    companion object {
        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room
                .databaseBuilder(context, AppDatabase::class.java, "neverlate")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}