package fr.isep.mobiledev.neverlate.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import fr.isep.mobiledev.neverlate.R
import fr.isep.mobiledev.neverlate.dto.AlarmDTO
import kotlin.math.roundToInt
import kotlin.random.Random

class PuzzleNone : Puzzle {
    private val className : String = this::class.java.name

    override fun getClassName(): String {
        return className
    }

    @OptIn(ExperimentalWearMaterialApi::class)
    @Composable
    override fun Content(alarm : AlarmDTO, onSnooze : () -> Unit, onDismiss : () -> Unit){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    onSnooze
                }) {
                Text(text = stringResource(R.string.snooze), style = MaterialTheme.typography.headlineMedium)
            }

            val width = 250.dp
            val pointSize = 48.dp

            val swipeableState = rememberSwipeableState(0)
            val sizePx = with(LocalDensity.current) { width.toPx() - pointSize.toPx() }
            val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states

            if(swipeableState.currentValue == 1){
                onDismiss()
            }

            Box(
                modifier = Modifier
                    .width(width)
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Horizontal
                    )
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(4.dp)
            ) {
                Box(
                    Modifier
                        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                        .size(pointSize)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(text = stringResource(R.string.dismiss),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                    )
            }
        }
    }
}

class PuzzleMath : Puzzle {
    private val className : String = this::class.java.name

    override fun getClassName(): String {
        return className
    }

    @Composable
    override fun Content(alarm : AlarmDTO, onSnooze : () -> Unit, onDismiss : () -> Unit){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = {
                    onSnooze
                }) {
                Text(text = stringResource(R.string.snooze), style = MaterialTheme.typography.headlineMedium)
            }

            val left by remember(alarm) { mutableIntStateOf(Random.nextInt(10, 100)) }
            val right by remember(alarm) { mutableIntStateOf(Random.nextInt(10, 100)) }
            val result by remember(alarm) { mutableIntStateOf(left + right) }

            Text(text = stringResource(R.string.math_dismiss),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp))
            Text(text = "$left + $right = ?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp))

            var answer by remember(alarm) { mutableStateOf("") }

            TextField(
                value = answer,
                onValueChange = { answer = it },
                label = { Text(text = stringResource(R.string.answer)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if(answer.isNotEmpty() && answer.toInt() == result){
                onDismiss()
            }
        }
    }
}