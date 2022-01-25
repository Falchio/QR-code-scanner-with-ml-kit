package ru.piteravto.barcodescanner.scanner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/** Данное view можно просто ображать в xml разметке layout'а */
class Box : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

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