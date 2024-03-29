package fr.isep.mobiledev.neverlate

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.VibratorManager

class AlarmPlayer() {

    companion object {
        @Volatile private var instance: AlarmPlayer? = null

        fun getInstance(context: Context): AlarmPlayer {
            return instance ?: synchronized(this) {
                instance ?: AlarmPlayer().also { instance = it }
            }
        }
    }

    private var mediaPlayer : MediaPlayer? = null

    fun play(context: Context) {
        if(mediaPlayer == null){
            val audioService = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            val audioSessionIdd = audioService.generateAudioSessionId()
            mediaPlayer = MediaPlayer.create(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null, AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(), audioSessionIdd)
        }
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}