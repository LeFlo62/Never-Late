package fr.isep.mobiledev.neverlate.entities

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm (
    @PrimaryKey val id: Int = 0,
    @ColumnInfo("name") var name: String = "",
    @ColumnInfo("hour") var hour: Int = 0,
    @ColumnInfo("minute") var minute: Int = 0,
    @ColumnInfo("toggled") var toggled: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun AlarmItem(alarm: Alarm = Alarm(0, "Test", 12, 30, true), modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.then(modifier)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(16.dp))
            //.border(2.dp, color = Color.Black, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(text = alarm.name, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onPrimaryContainer)
        Row {
            Text(text = "${alarm.hour}:${alarm.minute}", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = alarm.toggled,
                modifier = Modifier
                    .padding(start = 16.dp),
                onCheckedChange = {
                    alarm.toggled = it
                }
            )
        }
        Text(text = "Tomorrow", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}