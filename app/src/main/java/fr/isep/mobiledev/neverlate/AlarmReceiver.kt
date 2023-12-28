package fr.isep.mobiledev.neverlate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.isep.mobiledev.neverlate.activities.WakeUpActivity
import fr.isep.mobiledev.neverlate.dto.AlarmDTO

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Alarm received")
        if (context == null || intent == null) return

        val alarmDto : AlarmDTO? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AlarmDTO.ALARM_EXTRA, AlarmDTO::class.java)
        } else {
            intent.getParcelableExtra<AlarmDTO>(AlarmDTO.ALARM_EXTRA)
        }

        if (alarmDto == null) return

        val wakeUpActivityIntent = Intent(context, WakeUpActivity::class.java)
        wakeUpActivityIntent.putExtra(AlarmDTO.ALARM_EXTRA, alarmDto)
        wakeUpActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(wakeUpActivityIntent)

        (context.applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(context)
    }
}