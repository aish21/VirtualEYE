package com.example.virtualeye.utils

// Required Imports
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

/**
 * A custom View class that draws a rectangular boundary and text in it.
 * The boundary is drawn using a red paint and the text is drawn using a green paint.
 *
 * @param context the context of the View
 * @param rect a Rect object that defines the dimensions of the boundary
 * @param text the text to be displayed in the boundary
 */

@SuppressLint("ViewConstructor")
class Draw(context: Context?, var rect: Rect, var text: String) : View(context) {

    lateinit var boundaryPaint : Paint
    lateinit var textPaint: Paint

    init {
        init()
    }

    /**
     * Initializes the boundary and text paints with appropriate colors, widths, and styles.
     */

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

    /**
     * Draws the boundary and text on the canvas.
     *
     * @param canvas the canvas on which to draw the boundary and text
     */

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