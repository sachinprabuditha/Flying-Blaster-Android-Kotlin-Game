package com.example.androidgame

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

// Class representing the flight object in the game
class Flight(private val gameView: GameView, screenY: Int, res: Resources) {

    // Variables to manage shooting and movement
    var toShoot = 0
    var isGoingUp = false
    var x = 0
    var y = 0
    var width = 0
    var height = 0
    private var wingCounter = 0
    private var shootCounter = 1
    private var flight1: Bitmap
    private var flight2: Bitmap
    private var shoot1: Bitmap
    private var shoot2: Bitmap
    private var shoot3: Bitmap
    private var shoot4: Bitmap
    private var shoot5: Bitmap
    private var dead: Bitmap

    // Screen ratios for scaling
    var screenRatioX = Resources.getSystem().displayMetrics.widthPixels.toFloat() / 1920f
    var screenRatioY = Resources.getSystem().displayMetrics.heightPixels.toFloat() / 1080f

    // Initialization block to set up flight properties
    init {
        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1)
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2)

        width = flight1.width / 4
        height = flight1.height / 4

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false)
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false)

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1)
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2)
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3)
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4)
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5)

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false)
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false)
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false)
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false)
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false)

        dead = BitmapFactory.decodeResource(res, R.drawable.dead)
        dead = Bitmap.createScaledBitmap(dead, width, height, false)

        y = screenY / 2
        x = (64 * screenRatioX).toInt()
    }

    // Method to get the appropriate flight bitmap based on the current state
    fun getFlight(): Bitmap {
        return if (toShoot != 0) {
            when (shootCounter) {
                1 -> {
                    shootCounter++
                    shoot1 // Return shoot frame 1
                }
                2 -> {
                    shootCounter++
                    shoot2 // Return shoot frame 2
                }
                3 -> {
                    shootCounter++
                    shoot3 // Return shoot frame 3
                }
                4 -> {
                    shootCounter++
                    shoot4 // Return shoot frame 4
                }
                else -> {
                    shootCounter = 1
                    toShoot--
                    gameView.newBullet()
                    shoot5 // Return shoot frame 5
                }
            }
        } else {
            if (wingCounter == 0) {
                wingCounter++
                flight1 // Return flight bitmap with wings up
            } else {
                wingCounter--
                flight2 // Return flight bitmap with wings down
            }
        }
    }

    // Method to get the collision shape of the flight
    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    // Method to get the bitmap for dead flight
    fun getDead(): Bitmap {
        return dead
    }


}
