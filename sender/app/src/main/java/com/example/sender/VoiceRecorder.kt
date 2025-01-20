import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

// todo: test
class VoiceRecorder(
    private val context: Context,
    private val keyword: String,
    private val onKeywordDetected: () -> Unit, // Callback when the keyword is detected
    private val onError: (Int) -> Unit = {}    // Optional callback for handling errors
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    init {
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("VoiceRecorder", "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceRecorder", "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("VoiceRecorder", "Speech ended")
            }

            override fun onError(error: Int) {
                Log.e("VoiceRecorder", "Error occurred: $error")
                onError(error)
                if (isListening) {
                    startListening() // Restart listening on error
                }
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    Log.d("VoiceRecorder", "Recognized text: $spokenText")
                    if (spokenText.contains(keyword, ignoreCase = true)) {
                        Log.d("VoiceRecorder", "Keyword detected: $keyword")
                        onKeywordDetected()
                        stopListening()
                    } else {
                        startListening() // Continue listening
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        if (!isListening) {
            isListening = true
            val intent = Intent().apply {
                putExtra(SpeechRecognizer.RESULTS_RECOGNITION, true)
            }
            speechRecognizer?.startListening(intent)
        }
    }

    fun stopListening() {
        if (isListening) {
            isListening = false
            speechRecognizer?.stopListening()
        }
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}