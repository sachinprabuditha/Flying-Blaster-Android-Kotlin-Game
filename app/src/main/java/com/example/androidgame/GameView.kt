package com.example.androidgame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

// Game view class
class GameView(activity: GameActivity, private val screenX: Int, private val screenY: Int) :
    SurfaceView(activity), Runnable {

    // Game loop thread
    private var thread: Thread
    private var isPlaying = false
    private var isGameOver = false
    private var score = 0
    private var prefs: SharedPreferences
    private var screenRatioX: Float = 0f
    private var screenRatioY: Float = 0f
    private var paint: Paint
    private var birds: Array<Bird>
    private var random: Random
    private var soundPool: SoundPool
    private var bullets: MutableList<Bullet>
    private var sound: Int
     private var flight: Flight
    private var activity: GameActivity
    private var background1: Background
    private var background2: Background

    // Initialize game elements
    init {
        this.activity = activity

        // Load game resources
        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE)

        // Initialize sound pool for playing game sounds
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()

            SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build()
        } else
            SoundPool(1, AudioManager.STREAM_MUSIC, 0)

        // Load the bullet shooting sound
        sound = soundPool.load(activity, R.raw.shoot, 1)

        // Calculate screen ratios for scaling
        screenRatioX = 1920f / screenX
        screenRatioY = 1080f / screenY

        // Initialize game elements
        background1 = Background(screenX, screenY, resources)
        background2 = Background(screenX, screenY, resources)

        // Initialize the flight object
        flight = Flight(this, screenY, resources)

        // Initialize the list of bullets
        bullets = ArrayList()

        // Set the initial position of the background images
        background2.x = screenX

        // Initialize the paint object for drawing on the canvas
        paint = Paint()
        paint.textSize = 128f
        paint.color = Color.BLACK

        // Initialize the bird array
        birds = Array(4) { Bird(resources) }

        // Initialize the random object for generating random numbers
        random = Random()

        // Start the game loop
        thread = Thread(this)
    }

    // Game loop
    override fun run() {
        while (isPlaying) {
            update()
            draw()
            sleep()
        }
    }

    private fun update() {
        // Update the positions of game elements
        background1.x -= (10 * screenRatioX).toInt()
        background2.x -= (10 * screenRatioX).toInt()

        // Handle background movement loop
        if (background1.x + background1.background.width < 0) {
            background1.x = screenX
        }

        if (background2.x + background2.background.width < 0) {
            background2.x = screenX
        }

        // Move the flight (player's character) based on user input
        if (flight.isGoingUp)
            flight.y -= (30 * screenRatioY).toInt()
        else
            flight.y += (30 * screenRatioY).toInt()

        // Ensure the flight stays within the screen boundaries
        if (flight.y < 0)
            flight.y = 0

        if (flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height

        // Update bullet positions and check for collisions with birds
        val trash: MutableList<Bullet> = ArrayList()

        for (bullet in bullets) {
            if (bullet.x > screenX)
                trash.add(bullet)

            bullet.x += (50 * screenRatioX).toInt()

            // Check for collisions with birds
            for (bird in birds) {
                if (Rect.intersects(bird.getCollisionShape(), bullet.getCollisionShape())) {
                    score++
                    bird.x = -500
                    bullet.x = screenX + 500
                    bird.wasShot = true
                }
            }
        }

        // Remove bullets that have gone off screen
        for (bullet in trash)
            bullets.remove(bullet)

        // Update bird positions and check for collisions with the flight
        for (bird in birds) {
            bird.x -= bird.speed

            if (bird.x + bird.width < 0) {

                // Game over if a bird reaches the left edge of the screen
                if (!bird.wasShot  || Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {
                    isGameOver = true
                    return
                }
                // Reset bird position and speed
                val bound = (30 * screenRatioX).toInt()
                bird.speed = random.nextInt(bound)

                // Ensure the bird speed is not too slow
                if (bird.speed < (10 * screenRatioX).toInt())
                    bird.speed = (10 * screenRatioX).toInt()

                bird.x = screenX
                bird.y = random.nextInt(screenY - bird.height)

                bird.wasShot = false
            }


        }
    }

    private fun draw() {
        // Draw game elements on the canvas
        if (holder.surface.isValid) {
            val canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)

            // Draw birds
            for (bird in birds)
                canvas.drawBitmap(bird.getBird(), bird.x.toFloat(), bird.y.toFloat(), paint)

            // Draw score
            canvas.drawText(score.toString(), (screenX / 2).toFloat(), 164f, paint)

            if (isGameOver) {
                // Game over screen
                isPlaying = false
                canvas.drawBitmap(flight.getDead(), flight.x.toFloat(), flight.y.toFloat(), paint)
                holder.unlockCanvasAndPost(canvas)
                saveIfHighScore()
                waitBeforeExiting()
                return
            }

            // Draw flight and bullets
            canvas.drawBitmap(flight.getFlight(), flight.x.toFloat(), flight.y.toFloat(), paint)

            // Draw bullets
            for (bullet in bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x.toFloat(), bullet.y.toFloat(), paint)

            // Unlock the canvas and post the drawing
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun waitBeforeExiting() {
        // Wait for a few seconds before exiting the game
        try {
            Thread.sleep(3000)
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.finish()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun saveIfHighScore() {
        // Save the high score if the current score surpasses it
        if (prefs.getInt("highscore", 0) < score) {
            val editor = prefs.edit()
            editor.putInt("highscore", score)
            editor.apply()
        }
    }

    private fun sleep() {
        // Pause the thread for a short period to control the game's frame rate
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        // Resume the game loop
        isPlaying = true
        thread.start()
    }

    fun pause() {
        // Pause the game loop
        isPlaying = false
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Handle touch events for controlling the flight
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (event.x < screenX / 2) {
                    flight.isGoingUp = true
                }
            }
            MotionEvent.ACTION_UP -> {
                flight.isGoingUp = false
                if (event.x > screenX / 2)
                    flight.toShoot++
            }
        }
        return true
    }

    fun newBullet() {
        // Create a new bullet fired from the flight
        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1f, 1f, 0, 0, 1f)

        // Create a new bullet object and add it to the list of bullets
        val bullet = Bullet(resources)
        bullet.x = flight.x + flight.width
        bullet.y = flight.y + (flight.height / 2)
        bullets.add(bullet)
    }
}
