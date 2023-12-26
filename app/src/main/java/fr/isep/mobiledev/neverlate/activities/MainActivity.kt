package fr.isep.mobiledev.neverlate.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isep.mobiledev.neverlate.NeverLateApplication
import fr.isep.mobiledev.neverlate.R
import fr.isep.mobiledev.neverlate.activities.ui.theme.NeverLateTheme
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.model.AlarmViewModel
import fr.isep.mobiledev.neverlate.model.AlarmViewModelFactory


class MainActivity : ComponentActivity() {

    private val alarmViewModel by viewModels<AlarmViewModel> {
        AlarmViewModelFactory((this.applicationContext as NeverLateApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NeverLateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box{
                        AlarmList(alarmViewModel)
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
    private fun AlarmList(viewModel : AlarmViewModel){
        val alarms : List<Alarm> by viewModel.allAlarms.observeAsState(listOf())

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

    @Preview(showBackground = true)
    @Composable
    fun AlarmItem(modifier: Modifier = Modifier, alarm: Alarm = Alarm(0, "Test", 9, 10, true)) {
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

                Text(text = "${alarm.hour}:${alarm.minute}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(text = "Tomorrow", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)

            }

            var toggled by remember { mutableStateOf(alarm.toggled) }

            Switch(checked = toggled,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically),
                onCheckedChange = {
                    toggled = it
                    alarm.toggled = it
                    alarmViewModel.update(alarm)
                }
            )
        }
    }

    private fun schedule(){
        val mgr : AlarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, WakeUpActivity::class.java)
        //TODO use broadcast
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        val pending : PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        println("canScheduleExactAlarms: " + mgr.canScheduleExactAlarms())
        if(mgr.canScheduleExactAlarms()){
            mgr.setAlarmClock(AlarmManager.AlarmClockInfo(System.currentTimeMillis()+ 30000, pending), pending)
        } else {
            Intent().also { intent ->
                intent.action = android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                applicationContext.startActivity(intent)
            }
        }
    }
}