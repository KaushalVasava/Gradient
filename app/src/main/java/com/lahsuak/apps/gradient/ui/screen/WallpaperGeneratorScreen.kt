package com.lahsuak.apps.gradient.ui.screen

import android.content.Context
import android.util.AttributeSet
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.random.Random


class WallpaperView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {
    @Composable
    override fun Content() {
        RandomBackgroundScreen()
    }
}

//@PreviewLightDark
//@PreviewFontScale
//@PreviewScreenSizes
@Preview
@Composable
fun RandomBackgroundScreen(
    colors: List<Color> = mutableListOf(
        Color.Magenta,
        Color.Blue,
        Color.Yellow,
        Color.Red,
        Color.Green
    ),
    colorCount: () -> Int = { 5 },
    rotateAngle: () -> Float = { 0f },
    isSweepEnable: () -> Boolean = { false },
    isSweepGradientInside: () -> Boolean = { false },
) {

    val largeRadialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = colors.take(2),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0.0f, 0.95f)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush =
                if (isSweepEnable()) {
                    if (isSweepGradientInside()) {
//                        largeRadialGradient
                        Brush.radialGradient(colors.take(colorCount()))
                    } else {
                        Brush.radialGradient(
                            colors
                                .reversed()
                                .take(colorCount())
                        )
                    }
                } else {
                    when (rotateAngle().toInt() / 45) {
                        0 ->
                            Brush.verticalGradient(colors.take(colorCount()))

                        1 -> {
                            Brush.linearGradient(colors.take(colorCount()))
                        }


                        2 ->
                            Brush.horizontalGradient(
                                colors
                                    .reversed()
                                    .take(colorCount())
                            )

                        3 ->
                            Brush.linearGradient(
                                colors
                                    .reversed()
                                    .take(colorCount())
                            )

                        4 ->
                            Brush.verticalGradient(
                                colors
                                    .reversed()
                                    .take(colorCount())
                            )

                        5 ->
                            Brush.linearGradient(
                                colors
                                    .reversed()
                                    .take(colorCount())
                            )

                        6 ->
                            Brush.horizontalGradient(colors.take(colorCount()))

                        7 ->
                            Brush.linearGradient(colors.take(colorCount()))

                        else ->
                            Brush.verticalGradient(colors.take(colorCount()))
                    }
                }
            )
    )
}

@Preview
@Composable
fun ColorsTray(
    modifier: Modifier = Modifier,
    colors: List<Color> =
        listOf<Color>(
            Color.Magenta,
            Color.Blue,
            Color.Yellow,
            Color.Red,
            Color.Green,
            Color.LightGray,
            Color.Cyan,
            Color.Black,
            Color.White
        ),
) {

    val colorList by remember {
        mutableStateOf(colors)
    }
    var isActionButtonClick by rememberSaveable {
        mutableStateOf(false)
    }
    LazyRow(modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
        item {
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Black, CircleShape)
                    .background(Color.Transparent)
                    .size(40.dp)
                    .clickable {
                        isActionButtonClick = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
        items(colorList) {
            CircularColor(it)
        }
    }
//    if (isActionButtonClick) {
//        HsvColorPicker(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(450.dp)
//                .padding(10.dp),
//            controller = controller,
//            onColorChanged = { colorEnvelope: ColorEnvelope ->
////                colorList.toMutableList().add(colorEnvelope.color)
////                isActionButtonClick = false
//            }
//        )
//    }
}

@Preview
@Composable
fun CircularColor(color: Color = Color.White) {
    Box(
        Modifier
            .padding(horizontal = 8.dp)
            .clip(CircleShape)
            .background(color)
            .size(40.dp)
    )
}

@Composable
fun DrawShapes(shapes: Shapes) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val random = Random(48) // Seed for reproducibility

        repeat(100) {
            val color = getRandomColor(random)
            val shapeType = getRandomShape(random)

            val x = random.nextFloat() * canvasWidth
            val y = random.nextFloat() * canvasHeight

            drawShape(x, y, shapeType, color, shapes)
        }
    }
}

fun getRandomColor(random: Random): Color {
    return Color(random.nextInt(), random.nextInt(), random.nextInt())
}

fun getRandomShape(random: Random): ShapeType {
    return when (random.nextInt(3)) {
        0 -> ShapeType.RECTANGLE
        1 -> ShapeType.CIRCLE
        else -> ShapeType.ROUNDED_RECTANGLE
    }
}

enum class ShapeType {
    RECTANGLE,
    CIRCLE,
    ROUNDED_RECTANGLE
}

fun DrawScope.drawRect(color: Color, x: Float, y: Float, width: Float, height: Float) {
    drawRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(width, height)
    )
}

fun DrawScope.drawShape(x: Float, y: Float, shapeType: ShapeType, color: Color, shapes: Shapes) {
    val shapeSize = 50f

    when (shapeType) {
        ShapeType.RECTANGLE -> drawRect(color, x, y, shapeSize, shapeSize)
        ShapeType.CIRCLE -> drawCircle(color, x, y, shapeSize)
        ShapeType.ROUNDED_RECTANGLE -> drawRoundedRect(color, x, y, shapeSize, shapeSize)
    }
}

fun DrawScope.drawCircle(color: Color, x: Float, y: Float, radius: Float) {
    drawCircle(
        color = color,
        center = Offset(x + radius, y + radius),
        radius = radius
    )
}

fun DrawScope.drawRoundedRect(color: Color, x: Float, y: Float, width: Float, height: Float) {
    drawRoundRect(
        color = color,
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = CornerRadius(x, y)
    )
}