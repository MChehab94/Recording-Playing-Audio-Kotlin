package mchehab.com.recordingplayingaudio

import android.Manifest
import android.content.pm.PackageManager
import kotlinx.android.synthetic.main.activity_main.*
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import java.io.File

class MainActivity : AppCompatActivity() {

    var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    var FILE_RECORDING = ""

    val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED
    val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    val PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FILE_RECORDING = "${externalCacheDir.absolutePath}/recorder.aac"

        setButtonRecordListener()
        setButtonPlayRecordingListener()
        enableDisableButtonPlayRecording()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PERMISSION_GRANTED){
                record()
            }
        }
    }

    private fun enableDisableButtonPlayRecording(){
        buttonPlayRecording.isEnabled = doesFileExist()
    }

    private fun doesFileExist(): Boolean{
        val file = File(FILE_RECORDING)
        return file.exists()
    }

    private fun setButtonRecordListener(){
        buttonRecord.setOnClickListener {
            if(buttonRecord.text.toString().equals(getString(R.string.record), true)){
                record()
            }else{
                stopRecording()
                enableDisableButtonPlayRecording()
                buttonRecord.text = getString(R.string.record)
            }
        }
    }

    private fun setButtonPlayRecordingListener(){
        buttonPlayRecording.setOnClickListener {
            if(buttonPlayRecording.text.toString().equals(getString(R.string.playRecord), true)){
                buttonPlayRecording.text = getString(R.string.stopPlayingRecord)
                playRecording()
            }else{
                buttonPlayRecording.text = getString(R.string.playRecord)
                stopPlayingRecording()
            }
        }
    }

    private fun isPermissionGranted(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) checkSelfPermission(AUDIO_PERMISSION) == PERMISSION_GRANTED
        else return true

    }

    private fun requestAudioPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(arrayOf(AUDIO_PERMISSION), PERMISSION_REQUEST_CODE)
        }
    }

    private fun record(){
        if(!isPermissionGranted()){
            requestAudioPermission()
            return
        }
        buttonRecord.text = getString(R.string.stopRecording)
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        mediaRecorder!!.setOutputFile(FILE_RECORDING)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
    }

    private fun stopRecording(){
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun playRecording(){
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setDataSource(FILE_RECORDING)
        mediaPlayer!!.prepare()
        mediaPlayer!!.start()
        mediaPlayer!!.setOnCompletionListener {
            buttonPlayRecording.text = getString(R.string.playRecord)
        }
    }

    private fun stopPlayingRecording(){
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}