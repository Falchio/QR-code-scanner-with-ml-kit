package ru.piteravto.barcodescanner.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class Box(context: Context?) : View(context) {
    private val paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.GREEN
        strokeWidth = 6F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas == null) return


        val w2 = width / 2f
        val h2 = height / 2f
        val h6 = height / 6f
        canvas.drawRect(
            w2 - h6,
            h2 - h6,
            w2 + h6,
            h2 + h6,
            paint
        )
    }

}