package fr.isep.mobiledev.neverlate.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isep.mobiledev.neverlate.NeverLateApplication
import fr.isep.mobiledev.neverlate.R
import fr.isep.mobiledev.neverlate.activities.ui.theme.NeverLateTheme
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.model.AlarmViewModel
import fr.isep.mobiledev.neverlate.model.AlarmViewModelFactory
import fr.isep.mobiledev.neverlate.rules.DayOfWeek
import fr.isep.mobiledev.neverlate.rules.PreciseDate
import fr.isep.mobiledev.neverlate.rules.Rule
import fr.isep.mobiledev.neverlate.utils.Section
import java.util.Calendar

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
                    if(intent.hasExtra(AlarmDTO.ALARM_EXTRA)) {
                        val alarmDto : AlarmDTO? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(AlarmDTO.ALARM_EXTRA, AlarmDTO::class.java)
                        } else {
                            intent.getParcelableExtra<AlarmDTO>(AlarmDTO.ALARM_EXTRA)
                        }
                        if(alarmDto != null) {
                            EditAlarm(Alarm(alarmDto.id, if(alarmDto.name != null) alarmDto.name!! else "", alarmDto.hour, alarmDto.minute, alarmDto.toggled, alarmDto.rules))
                        }
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

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            val is24Hour = booleanResource(id = R.bool.is24Hour)
            val timeState by remember(alarm) { mutableStateOf(TimePickerState(alarm.hour, alarm.minute, is24Hour)) }
            var name by remember(alarm) { mutableStateOf(alarm.name) }
            var rules by remember(alarm) { mutableStateOf(alarm.rules) }

            val datePickerState by remember { mutableStateOf(DatePickerState(System.currentTimeMillis(), null, currentYear..(currentYear+100), DisplayMode.Picker)) }

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

                        if(rules.any{ it.javaClass == PreciseDate::class.java }){
                            rules = listOf(PreciseDate(Calendar.getInstance().apply { timeInMillis = datePickerState.selectedDateMillis!! }))
                        }

                        alarm.rules = rules

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
                singleLine = true,
                placeholder = { Text(text = stringResource(R.string.alarm_name)) },
                onValueChange = {
                    name = it
                }
            )


            //TODO save to database
            Section(name = stringResource(R.string.repeat), opened = true) {
                var singleDate by remember { mutableStateOf(rules.any { it.javaClass == PreciseDate::class.java }) }
                Row{
                    Checkbox(checked = singleDate, onCheckedChange = {
                        singleDate = it
                    })
                    Text(text = stringResource(R.string.single_date), modifier = Modifier.align(Alignment.CenterVertically))
                }
                if(singleDate){
                    DatePicker(state = datePickerState, modifier = Modifier.fillMaxWidth())
                }
                Section(name = stringResource(id = R.string.repeated), opened = true, disabled = singleDate) {
                    var dayOfWeek by remember(alarm) { mutableStateOf(rules.any{it.javaClass == DayOfWeek::class.java}) }
                    Row{
                        Checkbox(checked = dayOfWeek, onCheckedChange = {dayOfWeek = it})
                        Text(text = stringResource(R.string.day_of_week), modifier = Modifier.align(Alignment.CenterVertically))
                    }
                    if(dayOfWeek){
                        var days by remember(alarm) { mutableStateOf(if(rules.any{it.javaClass == DayOfWeek::class.java}) (rules.find { it.javaClass == DayOfWeek::class.java } as DayOfWeek).days else listOf(false, false, false, false, false, false, false)) }

                        Row{
                            for(day in 0..6){
                               IconButton(
                                  onClick = {
                                    days = days.toMutableList().apply {
                                         set(day, !get(day))
                                    }
                                    rules = listOf(DayOfWeek(days))
                                  },
                                  modifier = Modifier
                                      .weight(1f)
                                      .minimumInteractiveComponentSize()
                                      .padding(4.dp),
                                  colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if(days[day]) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = if(days[day]) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer
                                  )
                               ) {
                                   Text(text = stringArrayResource(id = R.array.day_of_week_short)[day])
                               }
                            }
                        }
                    }
                }
            }

            if(alarm.id != 0){
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
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
}