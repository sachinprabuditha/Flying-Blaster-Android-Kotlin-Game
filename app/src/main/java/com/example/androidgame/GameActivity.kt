package com.example.androidgame

import androidx.appcompat.app.AppCompatActivity

import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager

class GameActivity : AppCompatActivity() {

    private lateinit var gameView: GameView

    // Create the game view
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity to fullscreen mode
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        // Get the screen size
        val point = Point()
        windowManager.defaultDisplay.getSize(point)

        // Initialize and set the content view to the GameView
        gameView = GameView(this, point.x, point.y)

        setContentView(gameView)
    }

    // Pause and resume the game view when the activity is paused or resumed
    override fun onPause() {
        super.onPause()
        // Pause the game view when the activity is paused
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        // Resume the game view when the activity is resumed
        gameView.resume()
    }
}
