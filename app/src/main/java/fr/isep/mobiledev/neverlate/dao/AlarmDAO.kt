package fr.isep.mobiledev.neverlate.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.isep.mobiledev.neverlate.entities.Alarm

@Dao
interface AlarmDAO {

    @Query("SELECT * FROM alarm")
    fun getAllAlarms() : LiveData<List<Alarm>>

    @Query("SELECT * FROM alarm WHERE id = :id")
    fun getAlarmById(id: Int) : Alarm

    @Insert
    fun insertAlarm(alarm: Alarm)

    @Delete
    fun deleteAlarm(alarm: Alarm)

}