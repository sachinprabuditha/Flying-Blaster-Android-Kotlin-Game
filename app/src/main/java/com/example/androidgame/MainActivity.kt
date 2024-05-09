package com.example.androidgame

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Initialize mute status
    private var isMute: Boolean = false

    // Create the main activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity to full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Set the layout for the activity
        setContentView(R.layout.activity_main)

        // Start button click listener to start the game activity
        findViewById<View>(R.id.start).setOnClickListener {
            startActivity(Intent(this@MainActivity, GameActivity::class.java))
        }

        // Display the high score
        val highScoreTxt = findViewById<TextView>(R.id.highScore)

        val prefs: SharedPreferences = getSharedPreferences("game", MODE_PRIVATE)
        highScoreTxt.text = "HighScore: ${prefs.getInt("highscore", 0)}"

        // Retrieve mute status from SharedPreferences
        isMute = prefs.getBoolean("isMute", false)

        // Set sound control icon based on mute status
        val volumeCtrl = findViewById<ImageView>(R.id.sound)

        volumeCtrl.setImageResource(
            if (isMute) R.drawable.baseline_volume_off_24
            else R.drawable.baseline_volume_up_24
        )

        // Sound control click listener to toggle mute status
        volumeCtrl.setOnClickListener {
            isMute = !isMute
            volumeCtrl.setImageResource(
                if (isMute) R.drawable.baseline_volume_off_24
                else R.drawable.baseline_volume_up_24
            )

            // Save mute status to SharedPreferences
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("isMute", isMute)
            editor.apply()
        }
    }
}
