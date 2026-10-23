package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.Gold500
import com.example.ui.theme.Maroon700

@Composable
fun AlpanaDivider(
    modifier: Modifier = Modifier,
    color: Color = Gold500.copy(alpha = 0.6f)
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(18.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        // Thin horizontal baseline
        drawLine(
            color = color.copy(alpha = 0.3f),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 1.dp.toPx()
        )

        // Repeating subtle diamond and petal motif
        val step = 40.dp.toPx()
        var x = step / 2
        while (x < width) {
            val path = Path().apply {
                moveTo(x, centerY - 6.dp.toPx())
                lineTo(x + 6.dp.toPx(), centerY)
                lineTo(x, centerY + 6.dp.toPx())
                lineTo(x - 6.dp.toPx(), centerY)
                close()
            }
            drawPath(path = path, color = color, style = Stroke(width = 1.5.dp.toPx()))

            // Small dot in the center of the diamond
            drawCircle(
                color = color,
                radius = 1.5.dp.toPx(),
                center = Offset(x, centerY)
            )

            x += step
        }
    }
}
