package fr.isep.mobiledev.neverlate

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import fr.isep.mobiledev.neverlate.activities.EditAlarmActivity
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.entities.AlarmItem
import fr.isep.mobiledev.neverlate.model.AlarmViewModel
import fr.isep.mobiledev.neverlate.model.AlarmViewModelFactory
import fr.isep.mobiledev.neverlate.ui.theme.NeverLateTheme


class MainActivity : ComponentActivity() {

    private val alarmViewModel by viewModels<AlarmViewModel> {
        AlarmViewModelFactory((this.applicationContext as NeverLateApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NeverLateTheme {
                // A surface container using the 'background' color from the theme
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
                                alarmViewModel.insert(Alarm(0, "Test", 12, 30, true))
//                                val intent = Intent(this@MainActivity, EditAlarmActivity::class.java)
//                                startActivity(intent)
                            }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onSecondary)
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
                AlarmItem(alarm, modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(this@MainActivity, EditAlarmActivity::class.java)
                        intent.putExtra("alarmId", alarm.id)
                        startActivity(intent)
                    })
            }
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