package fr.isep.mobiledev.neverlate

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.isep.mobiledev.neverlate.dao.AlarmDAO
import fr.isep.mobiledev.neverlate.entities.Alarm

@Database(entities = [Alarm::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDAO

}