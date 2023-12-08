package fr.isep.mobiledev.neverlate

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.isep.mobiledev.neverlate.ui.theme.NeverLateTheme
import kotlin.math.log


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeverLateTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        schedule()
    }

    private fun schedule(){
        val mgr : AlarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager
        val pending : PendingIntent = PendingIntent.getActivity(this, 0, Intent(applicationContext, WakeUpActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
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