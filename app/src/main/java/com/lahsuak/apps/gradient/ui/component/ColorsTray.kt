package com.lahsuak.apps.gradient.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lahsuak.apps.gradient.R
import com.lahsuak.apps.gradient.ui.screen.ColorPickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorsTray(
    modifier: Modifier = Modifier,
    onColorChange: (Color, Int) -> Unit,
    onRemoveClick: (Int) -> Unit,
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
            CircularColor(
                item,
                onRemoveClick = {
                    onRemoveClick(index)
                }
            ) {
                isBottomSheetOpened = true
                selectedIndex = index
            }
        }
    }
}

@Preview
@Composable
fun CircularColor(
    color: Color = Color.White,
    onRemoveClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Box(
        Modifier
            .padding(horizontal = 8.dp)
            .size(40.dp)
    ) {
        Box(
            Modifier
                .clip(CircleShape)
                .background(color)
                .size(35.dp)
                .clickable { onClick() }
        )
        Icon(
            Icons.Default.Clear,
            contentDescription = stringResource(R.string.remove),
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.White)
                .size(16.dp)
                .clickable {
                    onRemoveClick()
                }
        )
    }
}