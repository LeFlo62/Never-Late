package fr.isep.mobiledev.neverlate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import fr.isep.mobiledev.neverlate.activities.MainActivity
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.model.AlarmViewModel
import fr.isep.mobiledev.neverlate.model.AlarmViewModelFactory
import fr.isep.mobiledev.neverlate.repository.AlarmRepository
import java.time.ZonedDateTime

class AlarmScheduler private constructor(context: Context) {

    companion object {
        @Volatile private var instance: AlarmScheduler? = null

        fun getInstance(context: Context): AlarmScheduler {
            return instance ?: synchronized(this) {
                instance ?: AlarmScheduler(context).also { instance = it }
            }
        }
    }

    private val alarmRepository : AlarmRepository

    init {
        this.alarmRepository = (context as NeverLateApplication).repository
    }

    suspend fun scheduleNextAlarm(context: Context) {
        alarmRepository.allAlarms.collect {
            println("Scheduling next alarm")
            println(it)
            for (alarm in it) {
                println("${DateFormat.format("HH:mm:ss dd/MM/yy", alarm.getNextExecution())} (${alarm.getNextExecution()})")
            }
            val alarm = it.filter { alarm -> alarm.toggled && alarm.getNextExecution() > System.currentTimeMillis() }.minByOrNull { alarm -> alarm.getNextExecution() } ?: return@collect

            println("Next alarm is '${alarm.name}' at ${DateFormat.format("HH:mm:ss dd/MM/yy", alarm.getNextExecution())} (${alarm.getNextExecution()}) now is ${System.currentTimeMillis()} - ${ZonedDateTime.now().offset.totalSeconds * 1000}")
            val mgr : AlarmManager = context.getSystemService(ComponentActivity.ALARM_SERVICE) as AlarmManager

            val receiverIntent = Intent(context, AlarmReceiver::class.java).apply {
                action = AlarmReceiver.ACTION_ALARM
                putExtra(AlarmDTO.ALARM_EXTRA, AlarmDTO(alarm))
            }

            val pendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val showIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)

            mgr.cancel(pendingIntent)
            mgr.setAlarmClock(AlarmManager.AlarmClockInfo(alarm.getNextExecution(), showIntent), pendingIntent)

        }

    }

}