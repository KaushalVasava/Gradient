package com.lahsuak.apps.gradient.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun RandomBackgroundScreen(
    modifier: Modifier = Modifier,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorsTray(
    modifier: Modifier = Modifier,
    onColorChange: (Color, Int) -> Unit,
    colors: List<Color> = listOf<Color>(
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
    var selectedIndex by rememberSaveable {
        mutableIntStateOf(-1)
    }
    val bottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isBottomSheetOpened by rememberSaveable {
        mutableStateOf(false)
    }
    if (isBottomSheetOpened) {
        ModalBottomSheet(
            sheetState = bottomSheet,
            onDismissRequest = {
                isBottomSheetOpened = false
            }
        ) {
            ColorPickerDialog { value, color ->
                isBottomSheetOpened = value
                if (color != null) {
                    onColorChange(color, selectedIndex)
                }
            }
        }
    }
    LazyRow(modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
        item {
            Box(
                Modifier
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Black, CircleShape)
                    .background(Color.Transparent)
                    .size(40.dp)
                    .clickable {
                        isBottomSheetOpened = true
                        selectedIndex = -1
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black)
            }
        }
        itemsIndexed(colorList) { index, item ->
            CircularColor(item) {
                isBottomSheetOpened = true
                selectedIndex = index
            }
        }
    }
}

@Preview
@Composable
fun CircularColor(color: Color = Color.White, onClick: () -> Unit = {}) {
    Box(
        Modifier
            .padding(horizontal = 8.dp)
            .clip(CircleShape)
            .background(color)
            .size(40.dp)
            .clickable { onClick() }
    )
}