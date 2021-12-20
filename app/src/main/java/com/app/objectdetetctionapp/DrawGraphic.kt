package com.app.objectdetetctionapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class DrawGraphic(context: Context?, rect: Rect, text: String) : View(context) {
    var borderPaint: Paint
    var textPaint: Paint
    var rect: Rect
    var text: String

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(text, rect.centerX().toFloat(), rect.centerY().toFloat(), textPaint)
        canvas.drawRect(rect.left.toFloat(),
            rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), borderPaint)
    }

    init {
        this.rect = rect
        this.text = text
        borderPaint = Paint()
        borderPaint.color = Color.RED
        borderPaint.strokeWidth = 10f
        borderPaint.style = Paint.Style.STROKE
        textPaint = Paint()
        textPaint.color = Color.RED
        textPaint.strokeWidth = 50f
        textPaint.textSize = 32f
        textPaint.style = Paint.Style.FILL
    }
}