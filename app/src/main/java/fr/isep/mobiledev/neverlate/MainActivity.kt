package fr.isep.mobiledev.neverlate

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import fr.isep.mobiledev.neverlate.entities.Alarm
import fr.isep.mobiledev.neverlate.entities.AlarmItem
import fr.isep.mobiledev.neverlate.ui.theme.NeverLateTheme


class MainActivity : ComponentActivity() {

    lateinit var db : AppDatabase

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
                        AlarmList()
                        Button(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .size(48.dp),
                            shape = CircleShape,
                            onClick = { /*TODO*/ }
                        ) {
                            //TODO icon trop petit on le voit pas, essaie d'augmenter la taille du btn tu verra. Mais faut augmenter l'icone !
                            Icon(Icons.Filled.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AlarmList(){
        var alarms by remember { mutableStateOf(listOf<Alarm>()) }
        initDatabase()
        val alarmDao = db.alarmDao()

        alarmDao.getAllAlarms().observe(this) { alarmList ->
            val alarmListMutable = alarmList.toMutableList()
            if(alarms.isEmpty()){
                for(i in 0..10){
                    alarmListMutable.add(Alarm(i, "Alarm $i", i, i, false))
                }
            }
            alarms = alarmListMutable
        }

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)) {
            items(alarms) { alarm ->
                AlarmItem(alarm, modifier = Modifier.padding(8.dp))
            }
        }
    }

    private fun initDatabase(){
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "neverlate"
        ).build()
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NeverLateTheme {
        Greeting("Android")
    }
}