package com.sameh.wheel_date_picker

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawWithContent

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import kotlin.math.roundToInt

@Composable
fun WheelDatePickerDialog(
    modifier: Modifier = Modifier,
    title: String = "Select Date",
    yearsRange: IntRange = (2010..2100),
    onDateSelected: (start: LocalDate) -> Unit = { _ -> },
    onDismiss: () -> Unit,
    okButtonTitle: String = "OK",
    cancelButtonTitle: String = "Cancel",
) {
    var selectedDay by remember { mutableIntStateOf(LocalDate.now().dayOfMonth) }
    var selectedMonth by remember { mutableIntStateOf(LocalDate.now().monthValue) }
    var selectedYear by remember { mutableIntStateOf(LocalDate.now().year) }

    // Adjust days when month or year changes
    val maxDays = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
    if (selectedDay > maxDays) selectedDay = maxDays

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 4.dp,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))

                // Wheel pickers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelPicker(
                        range = (1..maxDays).toList(),
                        selected = selectedDay,
                        onSelected = { selectedDay = it }
                    )

                    WheelPicker(
                        range = (1..12).toList(),
                        selected = selectedMonth,
                        onSelected = { selectedMonth = it },
                        formatter = { Month.of(it).name.take(3) }
                    )

                    WheelPicker(
                        range = yearsRange.toList(),
                        selected = selectedYear,
                        onSelected = { selectedYear = it }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(cancelButtonTitle)
                    }
                    TextButton(onClick = {
                        val selectedDate = LocalDate.of(selectedYear, selectedMonth, selectedDay)
                        onDateSelected(selectedDate)
                        onDismiss()
                    }) {
                        Text(okButtonTitle)
                    }
                }
            }
        }
    }
}

@Composable
fun <T> WheelPicker(
    range: List<T>,
    selected: T,
    onSelected: (T) -> Unit,
    formatter: (T) -> String = { it.toString() }
) {
    val scope = rememberCoroutineScope()
    val itemHeight = 48.dp
    val density = LocalDensity.current

    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = range.indexOf(selected).coerceAtLeast(0)
    )

    // Auto-detect the centered item
    LaunchedEffect(state) {
        val itemHeightPx = with(density) { itemHeight.toPx() }

        snapshotFlow { state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset }
            .map { (index, offset) ->
                val offsetItems = offset / itemHeightPx
                val visibleIndex = (index + offsetItems.roundToInt()).coerceIn(range.indices)
                range.getOrNull(visibleIndex)
            }
            .distinctUntilChanged()
            .collectLatest { item -> item?.let(onSelected) }
    }


    // Snap to nearest when scroll stops
    LaunchedEffect(state.isScrollInProgress) {
        if (!state.isScrollInProgress) {
            val itemHeightPx = with(density) { itemHeight.toPx() }
            val targetIndex = (state.firstVisibleItemScrollOffset / itemHeightPx).roundToInt() +
                    state.firstVisibleItemIndex
            scope.launch {
                state.animateScrollToItem(targetIndex.coerceIn(range.indices))
            }
        }
    }


    Box(
        modifier = Modifier
            .width(90.dp)
            .height(220.dp)
            .drawWithContent {
                drawContent()
                // Add top & bottom fade shadows for wheel illusion
                val fade = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.9f),
                        Color.Transparent,
                        Color.Transparent,
                        Color.White.copy(alpha = 0.9f)
                    ),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(fade)
            },
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 90.dp)
        ) {
            items(range) { item ->
                val isSelected = item == selected
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface
                    else Color.Gray, label = ""
                )

                Text(
                    text = formatter(item),
                    fontSize = if (isSelected) 18.sp else 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = animatedColor,
                    modifier = Modifier
                        .height(itemHeight)
                        .padding(vertical = 2.dp)
                )
            }
        }

        // Selection frame
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            HorizontalDivider(
                modifier = Modifier.align(Alignment.TopCenter),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            HorizontalDivider(
                modifier = Modifier.align(Alignment.BottomCenter),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}
