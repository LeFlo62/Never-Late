package fr.isep.mobiledev.neverlate

import android.app.Application
import fr.isep.mobiledev.neverlate.repository.AlarmRepository

class NeverLateApplication : Application(){
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AlarmRepository(database.alarmDao()) }
}