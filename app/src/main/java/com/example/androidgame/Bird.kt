package com.example.androidgame

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

import com.example.androidgame.R

class Bird(res: Resources) {

    var speed = 20
    var wasShot = true
    var x = 0
    var y: Int
    var width: Int
    var height: Int
    var birdCounter = 1
    var bird1: Bitmap
    var bird2: Bitmap
    var bird3: Bitmap
    var bird4: Bitmap

    // Screen ratios for scaling
    private var screenRatioX = Resources.getSystem().displayMetrics.widthPixels.toFloat() / 1920f
    private var screenRatioY = Resources.getSystem().displayMetrics.heightPixels.toFloat() / 1080f

    init {
        // Decode bitmaps from resources
        bird1 = BitmapFactory.decodeResource(res, R.drawable.bird1)
        bird2 = BitmapFactory.decodeResource(res, R.drawable.bird2)
        bird3 = BitmapFactory.decodeResource(res, R.drawable.bird3)
        bird4 = BitmapFactory.decodeResource(res, R.drawable.bird4)

        // Calculate width and height based on screen ratio
        width = bird1.width / 6
        height = bird1.height / 6

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        // Scale bitmaps to match screen size
        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false)
        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false)
        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false)
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false)

        // Set initial position off the screen
        y = -height
    }

    // Method to get the current bird bitmap
    fun getBird(): Bitmap {
        when (birdCounter) {
            1 -> {
                birdCounter++
                return bird1
            }
            2 -> {
                birdCounter++
                return bird2
            }
            3 -> {
                birdCounter++
                return bird3
            }
            else -> {
                birdCounter = 1
                return bird4
            }
        }
    }

    // Method to get the collision shape of the bird
    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}
