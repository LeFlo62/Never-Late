package fr.isep.mobiledev.neverlate.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isep.mobiledev.neverlate.NeverLateApplication
import fr.isep.mobiledev.neverlate.R
import fr.isep.mobiledev.neverlate.activities.ui.theme.NeverLateTheme
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.model.AlarmViewModel
import fr.isep.mobiledev.neverlate.model.AlarmViewModelFactory

class EditAlarmActivity : ComponentActivity() {

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
                    if(intent.hasExtra("alarmId")) {
                        val alarm : Alarm by alarmViewModel.getAlarmById(intent.getIntExtra("alarmId", 0)).observeAsState(Alarm())
                        EditAlarm(alarm)
                    } else {
                        EditAlarm()
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun EditAlarm(alarm : Alarm? = Alarm(toggled = true)) {
        if(alarm == null) {
            // This case happen when the alarm is being deleted
            return
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)){

            val timeState by remember(alarm) { mutableStateOf(TimePickerState(alarm.hour, alarm.minute, true)) }
            var name by remember(alarm) { mutableStateOf(alarm.name) }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onClick = {
                        finish()
                    }
                ) {
                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.close))
                }
                Text(text = if(alarm.id == 0) stringResource(R.string.create_alarm) else stringResource(R.string.edit_alarm),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold)
                IconButton(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    onClick = {
                        alarm.hour = timeState.hour
                        alarm.minute = timeState.minute
                        alarm.name = name

                        alarmViewModel.upsert(alarm)
                        finish()
                    }
                ) {
                    Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save))
                }
            }

            TimePicker(state = timeState, modifier = Modifier.fillMaxWidth())
            TextField(value = name,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.alarm_name)) },
                onValueChange = {
                    name = it
                    println(name)
                }
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                onClick = {
                    alarmViewModel.delete(alarm)
                    finish()
                }
            ) {
                Text(text = stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
            }
        }
    }
}