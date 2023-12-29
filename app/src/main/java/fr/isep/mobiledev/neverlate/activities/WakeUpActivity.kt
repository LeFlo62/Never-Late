package fr.isep.mobiledev.neverlate.activities

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isep.mobiledev.neverlate.AlarmReceiver
import fr.isep.mobiledev.neverlate.R
import fr.isep.mobiledev.neverlate.activities.ui.theme.NeverLateTheme
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import fr.isep.mobiledev.neverlate.rules.PuzzleMath

class WakeUpActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        super.onCreate(savedInstanceState)

        setContent {
            NeverLateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val alarmDto : AlarmDTO = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(AlarmDTO.ALARM_EXTRA, AlarmDTO::class.java)
                    } else {
                        intent.getParcelableExtra<AlarmDTO>(AlarmDTO.ALARM_EXTRA)
                    } ?: return@Surface

                    ContentPreview(alarmDto)
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ContentPreview(alarmDto : AlarmDTO = AlarmDTO(0, "Alarm", puzzle = PuzzleMath())) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = alarmDto.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp))

            val time = DateFormat.format(stringResource(R.string.time_format), System.currentTimeMillis()).toString()
            Text(text = time, style = MaterialTheme.typography.displayLarge)

            alarmDto.puzzle.Content(alarm = alarmDto,
                onSnooze = {
                   Intent(applicationContext, AlarmReceiver::class.java).also { intent ->
                       intent.action = AlarmReceiver.ACTION_SNOOZE
                       intent.putExtra(AlarmDTO.ALARM_EXTRA, alarmDto)
                       sendBroadcast(intent)
                   }
                    finish()
                },
                onDismiss = {
                    Intent(applicationContext, AlarmReceiver::class.java).also { intent ->
                        intent.action = AlarmReceiver.ACTION_DISMISS
                        intent.putExtra(AlarmDTO.ALARM_EXTRA, alarmDto)
                        sendBroadcast(intent)
                    }
                    finish()
                })
        }
    }
}