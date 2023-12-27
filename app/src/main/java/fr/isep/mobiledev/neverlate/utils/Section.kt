package fr.isep.mobiledev.neverlate.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Section(modifier : Modifier = Modifier, headerModifier : Modifier = Modifier, name: String, opened: Boolean = false, disabled : Boolean = false, paddingLeft: Dp = 16.dp, content: @Composable () -> Unit) {
    var _opened : Boolean by remember(opened, disabled) { mutableStateOf(!disabled && opened) }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .then(headerModifier)
                .background(if(disabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .clickable {
                    _opened = !disabled && !_opened
                },
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (_opened) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                style = androidx.compose.ui.text.TextStyle(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                modifier = Modifier.weight(1f)
            )
        }
        Divider()
        if (_opened && !disabled) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingLeft)
            ){
                content()
            }
        }
    }
}