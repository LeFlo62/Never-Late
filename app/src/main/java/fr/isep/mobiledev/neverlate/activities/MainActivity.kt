package fr.isep.mobiledev.neverlate.activities

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import fr.isep.mobiledev.neverlate.AlarmReceiver
import fr.isep.mobiledev.neverlate.NeverLateApplication
import fr.isep.mobiledev.neverlate.R
import fr.isep.mobiledev.neverlate.activities.ui.theme.NeverLateTheme
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.model.AlarmViewModel
import fr.isep.mobiledev.neverlate.model.AlarmViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class MainActivity : ComponentActivity() {

    private val alarmViewModel by viewModels<AlarmViewModel> {
        AlarmViewModelFactory((this.applicationContext as NeverLateApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()

        val channel = NotificationChannel(AlarmReceiver.CHANNEL_ID, "NeverLate Alarm", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "NeverLate Alarm"
            enableVibration(true)
            setSound(null, null)
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        setContent {
            NeverLateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box{
                        AlarmList()
                        IconButton(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    shape = CircleShape
                                )
                                .size(64.dp),
                            onClick = {
                                val intent = Intent(this@MainActivity, EditAlarmActivity::class.java)
                                startActivity(intent)
                            }
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = getString(R.string.add), tint = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AlarmList(){
        val alarms : List<Alarm> by alarmViewModel.allAlarms.observeAsState(listOf())

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)) {
            items(alarms) { alarm ->
                AlarmItem(alarm = alarm, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(this@MainActivity, EditAlarmActivity::class.java)
                        intent.putExtra(AlarmDTO.ALARM_EXTRA, AlarmDTO(alarm))
                        startActivity(intent)
                    })
            }
        }
    }

    @Composable
    fun AlarmItem(modifier: Modifier = Modifier, alarm: Alarm) {
        Row(modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
        ){
            Column(modifier = Modifier.weight(1f)){
                if(alarm.name.isNotEmpty()) {
                    Text(text = alarm.name, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onPrimaryContainer)
                }

                val triggerTime : Calendar = Calendar.getInstance()
                triggerTime.timeInMillis = alarm.getNextExecution()
                val formattedTriggerTime = DateFormat.format(stringResource(R.string.time_format), triggerTime).toString()
                Text(text = formattedTriggerTime, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)

                val formattedTriggerDate = DateFormat.format(stringResource(R.string.date_format), triggerTime).toString()
                Text(text = formattedTriggerDate, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)

            }

            var toggled by remember(alarm) { mutableStateOf(alarm.toggled) }

            Switch(checked = toggled,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically),
                onCheckedChange = {
                    toggled = it
                    alarm.toggled = it
                    alarmViewModel.update(alarm).invokeOnCompletion {
                        CoroutineScope(Dispatchers.Main).launch {
                            (applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(applicationContext)
                        }
                    }
                }
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Can post notifications.
        } else {
            // Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Can post notifications.
                println("Can post notifications")
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                println("Display an educational UI explaining to the user the features that will be enabled")
                AlertDialog.Builder(this)
                    .setTitle("Notification Permission Needed")
                    .setMessage("This app needs the Notification permission, please accept to use alarm functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .create()
                    .show()
            } else {
                // Directly ask for the permission
                println("Directly ask for the permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}