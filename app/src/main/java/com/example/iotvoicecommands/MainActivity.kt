package com.example.iotvoicecommands

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Button
import android.widget.TextView
import org.chromium.net.CronetEngine
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.ByteBuffer

private const val SPEECH_REQUEST_CODE = 12241
private const val esp_url = " http://893f-160-39-204-35.ngrok.io"
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    var recordButtonPressed: Boolean = false
    lateinit var convertedTextView: TextView
    lateinit var cronetEngine: CronetEngine
    lateinit var retrofit : Retrofit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recordButton = findViewById<Button>(R.id.recordTextButton)
        cronetEngine = CronetEngine.Builder(applicationContext).build()
        convertedTextView = findViewById<TextView>(R.id.voiceText)
        retrofit = Retrofit.Builder()
            .baseUrl(esp_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        recordButton.text = "Start Recording"

        recordButton.setOnClickListener {
            getSpeechText()
        }

        val sendButton = findViewById<Button>(R.id.btnSendText)
        sendButton.setOnClickListener {
            sendCommand(convertedTextView.text.toString())
        }
    }

    fun getSpeechText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        startActivityForResult(intent, SPEECH_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e(TAG, "Entered onActivityResult")
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "Result code matches")
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results?.get(0) ?: results.toString()
                }
            convertedTextView.text = spokenText
            Log.e("MainActivity", "Set the result")
        }
    }

    fun sendCommand(command: String) {
        Log.e(TAG, "making the request")
        val apiInterface = retrofit.create(APIInterface::class.java)
        val call = apiInterface.post_command(command)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.e(TAG, "Received response: " + response.body())
                findViewById<TextView>(R.id.textBoardResponse).text = response.body()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Received failure" + t.stackTraceToString())
                findViewById<TextView>(R.id.textBoardResponse).text = t.toString()
            }

        })

    }

}