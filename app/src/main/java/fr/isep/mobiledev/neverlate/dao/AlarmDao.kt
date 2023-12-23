package fr.isep.mobiledev.neverlate.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import fr.isep.mobiledev.neverlate.entities.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm")
    fun getAllAlarms() : Flow<List<Alarm>>

    @Query("SELECT * FROM alarm WHERE id = :id")
    fun getAlarmById(id: Int) : Flow<Alarm>

    @Insert
    fun insertAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

    @Update
    fun updateAlarm(alarm: Alarm)

}