package fr.isep.mobiledev.neverlate

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.VibratorManager

class AlarmPlayer(context: Context) {

    companion object {
        @Volatile private var instance: AlarmPlayer? = null

        fun getInstance(context: Context): AlarmPlayer {
            return instance ?: synchronized(this) {
                instance ?: AlarmPlayer(context).also { instance = it }
            }
        }
    }

    private val mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI, null, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build(), 0)
    private val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    private val vibrationEffect = VibrationEffect.createWaveform(longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000), 0)

    fun play() {
        mediaPlayer.isLooping = true
        mediaPlayer.start()
        //vibratorManager.defaultVibrator.vibrate(vibrationEffect)
    }

    fun stop() {
        mediaPlayer.stop()
        mediaPlayer.release()
        //vibratorManager.defaultVibrator.cancel()
    }
}