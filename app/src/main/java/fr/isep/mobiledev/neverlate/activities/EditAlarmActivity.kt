package fr.isep.mobiledev.neverlate.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.text.input.KeyboardType
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
import fr.isep.mobiledev.neverlate.rules.MonthOfYear
import fr.isep.mobiledev.neverlate.rules.PreciseDate
import fr.isep.mobiledev.neverlate.rules.Rule
import fr.isep.mobiledev.neverlate.rules.WeekOfYear
import fr.isep.mobiledev.neverlate.utils.Section
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

            val rules = remember(alarm) { mutableStateOf(alarm.rules) }

            val singleDate = remember { mutableStateOf(alarm.id == 0 || rules.value.any { it.javaClass == PreciseDate::class.java }) }
            val datePickerState by remember { mutableStateOf(DatePickerState(if(rules.value.any { it.javaClass == PreciseDate::class.java }) (rules.value.find{ it.javaClass == PreciseDate::class.java } as PreciseDate).getTimeMillis() else System.currentTimeMillis(), null, currentYear..(currentYear+100), DisplayMode.Picker)) }

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
                    enabled = rules.value.isNotEmpty() || singleDate.value,
                    onClick = {
                        alarm.hour = timeState.hour
                        alarm.minute = timeState.minute
                        alarm.name = name

                        if(singleDate.value){
                            rules.value = listOf(PreciseDate(Calendar.getInstance().apply { timeInMillis = datePickerState.selectedDateMillis!! }))
                        }

                        alarm.rules = rules.value

                        alarmViewModel.upsert(alarm).invokeOnCompletion {
                            CoroutineScope(Dispatchers.Main).launch {
                                (applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(applicationContext)
                            }
                        }
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

            Section(name = stringResource(R.string.repeat), opened = true) {
                SingleDate(singleDate, datePickerState, rules)

                Section(name = stringResource(id = R.string.repeated), opened = !singleDate.value, disabled = singleDate.value) {
                    DayOfWeek(alarm, rules)

                    WeekOfYear(alarm, rules)

                    MonthOfYear(alarm, rules)
                }
            }

            if(alarm.id != 0){
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                    onClick = {
                        alarmViewModel.delete(alarm).invokeOnCompletion {
                            CoroutineScope(Dispatchers.Main).launch {
                                (applicationContext as NeverLateApplication).alarmScheduler.scheduleNextAlarm(applicationContext)
                            }
                        }
                        finish()
                    }
                ) {
                    Text(text = stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SingleDate(singleDate : MutableState<Boolean>, datePickerState : DatePickerState, rules : MutableState<List<Rule>>){
        Row{
            Checkbox(checked = singleDate.value, onCheckedChange = {
                singleDate.value = it
                if(!it){
                    rules.value = listOf()
                }
            })
            Text(text = stringResource(R.string.single_date), modifier = Modifier.align(Alignment.CenterVertically))
        }
        if(singleDate.value){
            DatePicker(state = datePickerState, modifier = Modifier.fillMaxWidth())
        }
    }

    @Composable
    fun DayOfWeek(alarm : Alarm, rules : MutableState<List<Rule>>){
        var dayOfWeek by remember(alarm) { mutableStateOf(rules.value.any{it.javaClass == DayOfWeek::class.java}) }
        Row{
            Checkbox(checked = dayOfWeek, onCheckedChange = {dayOfWeek = it})
            Text(text = stringResource(R.string.day_of_week), modifier = Modifier.align(Alignment.CenterVertically))
        }
        if(dayOfWeek){
            var days by remember(alarm) { mutableStateOf(if(rules.value.any{it.javaClass == DayOfWeek::class.java}) (rules.value.find { it.javaClass == DayOfWeek::class.java } as DayOfWeek).days else listOf(false, false, false, false, false, false, false)) }

            Row{
                for(day in 0..6){
                    IconButton(
                        onClick = {
                            days = days.toMutableList().apply {
                                set(day, !get(day))
                            }
                            rules.value = rules.value.filter { it.javaClass != DayOfWeek::class.java  } + DayOfWeek(days)
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

    @Composable
    fun WeekOfYear(alarm : Alarm, rules : MutableState<List<Rule>>){
        var weekOfYear by remember(alarm) { mutableStateOf(rules.value.any{it.javaClass == fr.isep.mobiledev.neverlate.rules.WeekOfYear::class.java}) }
        Row{
            Checkbox(checked = weekOfYear, onCheckedChange = {weekOfYear = it})
            Text(text = stringResource(R.string.week_of_year), modifier = Modifier.align(Alignment.CenterVertically))
        }
        if(weekOfYear){
            var period by remember(alarm) { mutableStateOf(if(rules.value.any{it.javaClass == fr.isep.mobiledev.neverlate.rules.WeekOfYear::class.java}) (rules.value.find { it.javaClass == fr.isep.mobiledev.neverlate.rules.WeekOfYear::class.java } as fr.isep.mobiledev.neverlate.rules.WeekOfYear).period else 1) }
            var offset by remember(alarm) { mutableStateOf(if(rules.value.any{it.javaClass == fr.isep.mobiledev.neverlate.rules.WeekOfYear::class.java}) (rules.value.find { it.javaClass == fr.isep.mobiledev.neverlate.rules.WeekOfYear::class.java } as fr.isep.mobiledev.neverlate.rules.WeekOfYear).offset else 0) }

            Row{
                Text(text = stringResource(R.string.every), modifier = Modifier.align(Alignment.CenterVertically))
                TextField(value = period.toString(),
                    modifier = Modifier
                        .width(96.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        period = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 1
                        if(period <= 1) period = 2
                        if(period > 52) period = 52
                        rules.value = rules.value.filter { it.javaClass != WeekOfYear::class.java  } + WeekOfYear(period, offset)
                    }
                )
                Text(text = stringResource(R.string.weeks), modifier = Modifier.align(Alignment.CenterVertically))
            }

            Row{
                Text(text = stringResource(R.string.starting_on), modifier = Modifier.align(Alignment.CenterVertically))
                TextField(value = offset.toString(),
                    modifier = Modifier
                        .width(96.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {
                        offset = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
                        if(offset < 0) offset = 0
                        if(offset >= period) offset = period - 1
                        rules.value = rules.value.filter { it.javaClass != WeekOfYear::class.java  } + WeekOfYear(period, offset)
                    }
                )
                Text(text = stringResource(R.string.weeks), modifier = Modifier.align(Alignment.CenterVertically))
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    fun MonthOfYear(alarm : Alarm, rules : MutableState<List<Rule>>){
        var monthOfYear by remember(alarm) { mutableStateOf(rules.value.any{it.javaClass == MonthOfYear::class.java}) }
        Row{
            Checkbox(checked = monthOfYear, onCheckedChange = {monthOfYear = it})
            Text(text = stringResource(R.string.month_of_year), modifier = Modifier.align(Alignment.CenterVertically))
        }

        if(monthOfYear){
            var months by remember(alarm) { mutableStateOf(if(rules.value.any{it.javaClass == MonthOfYear::class.java}) (rules.value.find { it.javaClass == MonthOfYear::class.java } as MonthOfYear).months else listOf(false, false, false, false, false, false, false, false, false, false, false, false)) }

            FlowRow(modifier = Modifier.fillMaxWidth()){
                for(month in 0..11){
                    FilterChip(
                        selected = months[month],
                        onClick = {
                            months = months.toMutableList().apply {
                                set(month, !get(month))
                            }
                            rules.value = rules.value.filter { it.javaClass != MonthOfYear::class.java  } + MonthOfYear(months)
                        }, label = {
                            Text(text = stringArrayResource(id = R.array.month_of_year)[month])
                        })
                }
            }
        }
    }

}