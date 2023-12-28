package fr.isep.mobiledev.neverlate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.isep.mobiledev.neverlate.activities.WakeUpActivity
import fr.isep.mobiledev.neverlate.dto.AlarmDTO

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("AlarmReceiver")

        if (context == null || intent == null) return
        val alarmId = intent.getIntExtra("alarmId", -1)

        println("AlarmReceiver: $alarmId")
        if (alarmId == -1) return

        val wakeUpActivityIntent = Intent(context, WakeUpActivity::class.java)
        wakeUpActivityIntent.putExtra("alarmId", alarmId)
        wakeUpActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(wakeUpActivityIntent)

        (context.applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(context)
    }
}