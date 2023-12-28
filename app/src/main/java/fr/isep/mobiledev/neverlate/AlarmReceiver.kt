package fr.isep.mobiledev.neverlate

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import fr.isep.mobiledev.neverlate.activities.WakeUpActivity
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "fr.isep.mobiledev.neverlate.ALARM_CHANNEL"
        const val ACTION_ALARM = "fr.isep.mobiledev.neverlate.ACTION_ALARM"
        const val ACTION_SNOOZE = "fr.isep.mobiledev.neverlate.ACTION_SNOOZE"
        const val ACTION_DISMISS = "fr.isep.mobiledev.neverlate.ACTION_DISMISS"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        println("AlarmReceiver")

        if (context == null || intent == null) return
        val alarmDto : AlarmDTO? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(AlarmDTO.ALARM_EXTRA, AlarmDTO::class.java)
        } else {
            intent.getParcelableExtra<AlarmDTO>(AlarmDTO.ALARM_EXTRA)
        }

        if (alarmDto == null) return
        println("AlarmReceiver: ${alarmDto.name}")

        when(intent.action){
            ACTION_ALARM -> playAlarm(context, alarmDto)
            ACTION_SNOOZE -> snoozeAlarm(context, alarmDto)
            ACTION_DISMISS -> dismissAlarm(context, alarmDto)
        }
    }

    private fun dismissAlarm(context: Context, alarmDto: AlarmDTO) {
        println("AlarmReceiver: dismissAlarm")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        (context.applicationContext as NeverLateApplication).alarmPlayer.stop()

        notificationManager.cancel(1)

        CoroutineScope(Dispatchers.Main).launch {
            (context.applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(context)
        }
    }

    private fun snoozeAlarm(context: Context, alarmDto: AlarmDTO) {
        println("AlarmReceiver: snoozeAlarm")
        (context.applicationContext as NeverLateApplication).alarmPlayer.stop()
    }

    private fun playAlarm(context : Context, alarmDto : AlarmDTO) {
        println("AlarmReceiver: playAlarm")
        val wakeUpActivityIntent = Intent(context, WakeUpActivity::class.java)
        wakeUpActivityIntent.putExtra(AlarmDTO.ALARM_EXTRA, alarmDto)
        wakeUpActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(AlarmDTO.ALARM_EXTRA, alarmDto)
        }

        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_DISMISS
            putExtra(AlarmDTO.ALARM_EXTRA, alarmDto)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.clock)
            .setContentTitle("NeverLate")
            .setContentText("Alarm ${alarmDto.name} is ringing")
            .setOngoing(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(PendingIntent.getActivity(context, 0, wakeUpActivityIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT), true)
            .addAction(0, "Snooze", PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
            .addAction(0, "Dismiss", PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
            .setDeleteIntent(PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(1, notification)

        (context.applicationContext as NeverLateApplication).alarmPlayer.play()

        CoroutineScope(Dispatchers.Main).launch {
            (context.applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(context)
        }
    }
}