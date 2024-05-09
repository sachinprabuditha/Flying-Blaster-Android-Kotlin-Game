package com.example.androidgame

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Bullet(private val res: Resources) {

    var x = 0
    var y = 0
    private var width = 0
    private var height = 0
    var bullet: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bullet)

    // Screen ratios for scaling
    private var screenRatioX = Resources.getSystem().displayMetrics.widthPixels.toFloat() / 1920f
    private var screenRatioY = Resources.getSystem().displayMetrics.heightPixels.toFloat() / 1080f

    init {

        // Calculate width and height of the bullet based on screen ratio
        width = bullet.width / 4
        height = bullet.height / 4

        // Scale the bullet bitmap according to screen ratio
        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        // Scale the bullet bitmap to match screen size
        bullet = Bitmap.createScaledBitmap(bullet, width, height, false)
    }

    // Get collision shape of the bullet
    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}
