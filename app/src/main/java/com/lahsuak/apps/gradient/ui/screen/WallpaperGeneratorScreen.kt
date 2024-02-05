package com.lahsuak.apps.gradient.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun WallpaperGeneratorScreen(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
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
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier
                .background(
                    brush =
                    if (isSweepEnable()) {
                        if (isSweepGradientInside()) {
                            Brush.radialGradient(colors.take(colorCount()))
                        } else {
                            Brush.radialGradient(
                                colors
                                    .take(colorCount())
                                    .reversed()
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
                                        .take(colorCount())
                                        .reversed()
                                )

                            3 ->
                                Brush.linearGradient(
                                    colors
                                        .take(colorCount())
                                        .reversed()
                                )

                            4 ->
                                Brush.verticalGradient(
                                    colors
                                        .take(colorCount())
                                        .reversed()
                                )

                            5 ->
                                Brush.linearGradient(
                                    colors
                                        .take(colorCount())
                                        .reversed()
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
        ) {}
    }
}
