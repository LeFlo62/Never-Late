package fr.isep.mobiledev.neverlate.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import fr.isep.mobiledev.neverlate.dao.AlarmDao
import fr.isep.mobiledev.neverlate.entities.Alarm
import kotlinx.coroutines.flow.Flow

class AlarmRepository(private val alarmDao: AlarmDao) {

    val allAlarms : Flow<List<Alarm>> = alarmDao.getAllAlarms()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    fun delete(alarm: Alarm) {
        alarmDao.deleteAlarm(alarm)
    }

    fun getAlarmById(id: Int) : Flow<Alarm> {
        return alarmDao.getAlarmById(id)
    }

    fun update(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    fun upsert(alarm: Alarm) {
        alarmDao.upsertAlarm(alarm)
    }

    fun deleteAlarms(alarms : List<Alarm>) {
        alarmDao.deleteAlarms(alarms)
    }
}