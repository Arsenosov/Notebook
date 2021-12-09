package com.arsenosov.notebook.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.arsenosov.notebook.compose.MyAppBar
import com.arsenosov.notebook.util.Screen
import com.arsenosov.notebook.viewmodels.MainViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.InternalCoroutinesApi
import java.time.format.DateTimeFormatter

@InternalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun NewScheduleScreen(viewModel: MainViewModel, openDrawer: () -> Unit) {
    Column {
        val isMenuDown = remember { mutableStateOf(false)}
        val textFieldSize = remember { mutableStateOf(Size.Zero)}
        val selectedContact = viewModel.contactSelectedScheduleLive.value
        val errorContact = viewModel.contactGroupErrorScheduledLive.value
        val contactsList = viewModel.contactListLive.value

        val selectedDate = viewModel.dateSelectedScheduleLive.value
        val selectedTime = viewModel.timeSelectedScheduleLive.value
        val errorDatetime = viewModel.datetimeErrorScheduleLive.value

        val dateDialogState = rememberMaterialDialogState()
        val timeDialogState = rememberMaterialDialogState()

        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton("Ок")
                negativeButton("Отмена")
            }
        ) {
            timepicker(
                initialTime = selectedTime,
                title = "Выберите время",
                onTimeChange = {
                    viewModel.changeSelectedTime(it)
                },
                is24HourClock = true,
            )
        }

        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton("Ок")
                negativeButton("Отмена")
            }
        ) {
            datepicker(
                initialDate = selectedDate,
                title = "Выберите дату",
                onDateChange = {
                    viewModel.changeSelectedDate(it)
                },
            )
        }

        MyAppBar(title = Screen.NewSchedule.title) {
            openDrawer()
        }
        Column(
            modifier = Modifier
                .padding(15.dp),
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                OutlinedTextField(
                    value = selectedContact?.name.toString(),
                    onValueChange = {},
                    readOnly = true,
                    isError = errorContact.isNotEmpty(),
                    colors = TextFieldDefaults
                        .outlinedTextFieldColors(
                            errorBorderColor = Color.Red,
                            errorCursorColor = Color.Red,
                            errorLabelColor = Color.Red,
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            textFieldSize.value = it.size.toSize()
                        },
                    label = {
                        Text(
                            text = if (errorContact.isNotEmpty()) errorContact else "Контакт"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    isMenuDown.value = !isMenuDown.value
                                }
                        )
                    }
                )
                DropdownMenu(
                    expanded = isMenuDown.value,
                    onDismissRequest = {
                        isMenuDown.value = false
                    },
                    modifier = Modifier
                        .width(textFieldSize.value.width.dp)
                ) {
                    contactsList.forEach {
                        DropdownMenuItem(
                            onClick = {
                                viewModel.changeSelectedContact(it)
                                isMenuDown.value = false
                            },
                        ) {
                            Text(
                                text = it.name
                            )
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(10.dp),
            )
            Text(
                text = "Группы"
            )
            ScheduleGroups(
                viewModel = viewModel,
            )
            OutlinedTextField(
                value = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                isError = errorDatetime.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        dateDialogState.show()
                    },
                label = {
                    Text(
                        text = if (errorDatetime.isNotEmpty()) errorDatetime else "Выбранный день"
                    )
                },
            )
            Spacer(
                modifier = Modifier
                    .height(10.dp),
            )
            OutlinedTextField(
                value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onValueChange = {},
                readOnly = true,
                isError = errorDatetime.isNotEmpty(),
                enabled = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        timeDialogState.show()
                    },
                label = {
                    Text(
                        text = if (errorDatetime.isNotEmpty()) errorDatetime else "Выбранное время"
                    )
                },
            )
            Spacer(
                modifier = Modifier
                    .height(10.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Black),
                    onClick = {
                        viewModel.checkNewScheduleSubmit()
                    }) {
                    Text(
                        text = "Запланировать",
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}

@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ScheduleGroups(viewModel: MainViewModel) {
    val groups = viewModel.groupListLive.value
    val selectedGroup = viewModel.groupSelectedScheduleLive.value

    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            horizontal = 10.dp,
            vertical = 15.dp,
        ),
    ) {
        items(
            count = groups.size,
            itemContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Card(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(1.dp, Color.Black, CircleShape)
                            .size(30.dp),
                        backgroundColor = Color(groups[it].color),
                        onClick = {
                            viewModel.changeSelectedGroupSchedule(groups[it])
                        },
                    ) {
                        if (selectedGroup == groups[it]) {
                            Image(
                                modifier = Modifier
                                    .fillParentMaxSize(),
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Selected group",
                            )
                        }
                    }
                    Spacer(
                        modifier = Modifier
                            .width(20.dp),
                    )
                    Text(
                        text = groups[it].name,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        )
    }
}