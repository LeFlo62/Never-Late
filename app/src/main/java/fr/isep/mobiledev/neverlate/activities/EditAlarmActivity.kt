package fr.isep.mobiledev.neverlate.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.isep.mobiledev.neverlate.activities.ui.theme.NeverLateTheme
import fr.isep.mobiledev.neverlate.entities.Alarm

class EditAlarmActivity : ComponentActivity() {
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
                        //TODO load from DB
                        EditAlarm(Alarm(intent.getIntExtra("alarmId", 0)))
                    } else {
                        EditAlarm()
                    }
                }
            }
        }
    }

    @Composable
    private fun EditAlarm(alarm : Alarm = Alarm()) {
        Text(text = "Edit alarm ${alarm.id}")
    }
}