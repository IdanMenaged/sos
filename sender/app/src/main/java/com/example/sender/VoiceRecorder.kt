import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult

const val TAG = "VoiceRecorder" // logging tag

class VoiceRecorder(private val context: Context): RecognitionListener {
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    init {
        speechRecognizer.setRecognitionListener(this)

        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-us")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "please speak into the mic")

        speechRecognizer.startListening(recognizerIntent)
    }

    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "onReadyForSpeech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        Log.d(TAG, "onRmsChanged")
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "onBufferReceived")

    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(TAG, "onPartialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "onEvent $eventType")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech")
    }

    override fun onError(error: Int) {
        Log.d(TAG, "onError $error")
    }

    override fun onResults(results: Bundle?) {
        Log.d(TAG, "onReadyForSpeech")
        val result = results?.getStringArrayList("results_recognition")

        Log.d(TAG, "result: $result")
    }
}