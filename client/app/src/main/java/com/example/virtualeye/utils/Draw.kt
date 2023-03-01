package com.example.virtualeye.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

@SuppressLint("ViewConstructor")
class Draw(context: Context?, var rect: Rect, var text: String) : View(context) {

    lateinit var boundaryPaint : Paint
    lateinit var textPaint: Paint

    init {
        init()
    }

    private fun init() {
        boundaryPaint = Paint()
        boundaryPaint.color = Color.RED
        boundaryPaint.strokeWidth = 15f
        boundaryPaint.style = Paint.Style.STROKE

        textPaint = Paint()
        textPaint.color = Color.GREEN
        textPaint.strokeWidth = 75f
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 60f // Set the text size to 50 pixels
    }

    override fun onDraw(canvas: Canvas?) {
        // Calculate the width of the text
        val textWidth = textPaint.measureText(text)

        // Calculate the x-coordinate of the text to center it in the box
        val textX = rect.centerX().toFloat() - textWidth / 2

        // Draw the text and the box
        canvas?.drawText(text, textX, rect.centerY().toFloat(), textPaint)
        canvas?.drawRect(rect.left.toFloat(), rect.top.toFloat(), rect.bottom.toFloat(), rect.right.toFloat(), boundaryPaint)

    }
}