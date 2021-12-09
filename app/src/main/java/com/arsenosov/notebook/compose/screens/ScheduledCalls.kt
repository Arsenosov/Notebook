package com.arsenosov.notebook.compose.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.arsenosov.notebook.compose.MyAppBar
import com.arsenosov.notebook.database.entities.ScheduledCall
import com.arsenosov.notebook.util.Screen
import com.arsenosov.notebook.viewmodels.MainViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.InternalCoroutinesApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

//TODO("make sure that list of scheduled calls is updated after its' time passed")
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun ScheduledScreen(navController: NavController, viewModel: MainViewModel, openDrawer: () -> Unit) {
    val scheduledList = viewModel.scheduleListLive.value
    Column {
        MyAppBar(title = Screen.Scheduled.title) {
            openDrawer()
        }
        if (scheduledList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Созвонов пока нет. Запланируйте один!",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(
                    modifier = Modifier.height(15.dp)
                )
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Black),
                    onClick = {
                        navController.navigate(Screen.NewSchedule.route) {
                            launchSingleTop = true
                        }
                    }) {
                    Text(
                        text = "Запланировать созвон",
                        fontSize = 16.sp,
                    )
                }
            }
        } else {
            SchedulesList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                viewModel = viewModel,
                items = scheduledList
            )
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun SchedulesList(modifier: Modifier, viewModel: MainViewModel, items: List<ScheduledCall>) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = items,
            itemContent = {
                ScheduleItem(
                    viewModel = viewModel,
                    item = it
                )
            }
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun ScheduleItem(viewModel: MainViewModel, item: ScheduledCall) {
    val dialogState = rememberMaterialDialogState()
    val color = if (item.contactId == null) Color(viewModel.getGroupById(item.groupId!!).color)
        else Color(viewModel.getGroupById(viewModel.getContactById(item.contactId)?.groupId ?: 1).color)
    ScheduleDialog(
        dialogState = dialogState,
        viewModel = viewModel,
        schedule = item,
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.Black
        ),
        backgroundColor = color,
        onClick = {
            dialogState.show()
        },
    ) {
        Column {
            Text(
                text = if (item.contactId == null) viewModel.getGroupById(item.groupId!!).name
                    else viewModel.getContactById(item.contactId)?.name ?: "",
                fontWeight = FontWeight.Bold,
            )
            Spacer(
                modifier = Modifier
                    .height(15.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = SimpleDateFormat("HH:mm dd.MM", Locale("ru")).format(Date(1000000 * item.time))
                )
                if (item.contactId != null) {
                    Spacer(
                        modifier = Modifier
                            .width(20.dp),
                    )
                    Text(
                        text = viewModel.getContactById(item.contactId)?.phone ?: ""
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@InternalCoroutinesApi
@Composable
fun ScheduleDialog(dialogState: MaterialDialogState, viewModel: MainViewModel, schedule: ScheduledCall) {
    val selectedContact = remember { mutableStateOf(schedule.contactId)}
    val selectedGroup = remember { mutableStateOf(schedule.groupId)}
    val selectedDate = remember { mutableStateOf(Instant.ofEpochMilli(schedule.time).atZone(ZoneId.systemDefault()).toLocalDate())}
    val selectedTime = remember { mutableStateOf(Instant.ofEpochMilli(schedule.time).atZone(ZoneId.systemDefault()).toLocalTime())}
    val isMenuDown = remember { mutableStateOf(false)}
    val timeDialogState = rememberMaterialDialogState()
    val dateDialogState = rememberMaterialDialogState()
    val textFieldSize = remember { mutableStateOf(Size.Zero)}
    val errorMessage = remember { mutableStateOf("")}
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            button("Сохранить") {
                val updatedSchedule = ScheduledCall(
                    id = schedule.id,
                    contactId = selectedContact.value,
                    groupId = selectedGroup.value,
                    time = selectedDate.value.atTime(selectedTime.value)
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                )
                errorMessage.value = viewModel.checkUpdateSchedule(updatedSchedule)
                if (errorMessage.value == "")
                    dialogState.hide()
            }
            button("Удалить") {
                viewModel.checkDeleteSchedule(schedule)
                dialogState.hide()
            }
            button("Отмена") {
                dialogState.hide()
            }
        }
    ) {
        customView {
            Column {
                MaterialDialog(
                    dialogState = timeDialogState,
                    buttons = {
                        positiveButton("Ок")
                        negativeButton("Отмена")
                    },
                ) {
                    timepicker(
                        initialTime = selectedTime.value,
                        waitForPositiveButton = true,
                        onTimeChange = {
                            selectedTime.value = it
                            errorMessage.value = ""
                        },
                        is24HourClock = true,
                    )
                }
                MaterialDialog(
                    dialogState = dateDialogState,
                    buttons = {
                        positiveButton("Ок")
                        negativeButton("Отмена")
                    },
                ) {
                    datepicker(
                        initialDate = selectedDate.value,
                        waitForPositiveButton = true,
                        onDateChange = {
                            selectedDate.value = it
                            errorMessage.value = ""
                        },
                    )
                }
                Column {
                    OutlinedTextField(
                        value = if (selectedContact.value == null) "" else viewModel.getContactById(selectedContact.value!!)?.name ?: "",
                        onValueChange = {
                            errorMessage.value = ""
                        },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                textFieldSize.value = it.size.toSize()
                            },
                        label = {
                            Text(
                                text = "Контакт"
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
                        },
                    )
                    DropdownMenu(
                        expanded = isMenuDown.value,
                        onDismissRequest = {
                            isMenuDown.value = false
                        },
                        modifier = Modifier
                            .width(textFieldSize.value.width.dp),
                    ) {
                        viewModel.contactListLive.value.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    selectedContact.value = it.id
                                    selectedGroup.value = 1
                                    isMenuDown.value = false
                                }
                            ) {
                                Text(
                                    text = it.name,
                                )
                            }
                        }
                    }
                }
                Text(
                    text = "Группы"
                )
                LazyVerticalGrid(
                    cells = GridCells.Fixed(3),
                    contentPadding = PaddingValues(10.dp),
                ) {
                    items(
                        count = viewModel.groupListLive.value.size,
                        itemContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Card(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .border(1.dp, Color.Black, CircleShape)
                                        .size(25.dp),
                                    backgroundColor = Color(viewModel.groupListLive.value[it].color),
                                    onClick = {
                                        selectedGroup.value = viewModel.groupListLive.value[it].id!!
                                        errorMessage.value = ""
                                    }
                                ) {
                                    if (selectedGroup.value == viewModel.groupListLive.value[it].id) {
                                        Image(
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .fillParentMaxSize(),
                                        )
                                    }
                                }
                                Spacer(
                                    modifier = Modifier
                                        .width(15.dp),
                                )
                                Text(
                                    text = viewModel.groupListLive.value[it].name,
                                )
                            }
                        }
                    )
                }
                OutlinedTextField(
                    value = selectedDate.value.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            dateDialogState.show()
                        },
                    label = {
                        Text(
                            text = "Выбранная дата"
                        )
                    }
                )
                OutlinedTextField(
                    value = selectedTime.value.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            timeDialogState.show()
                        },
                    label = {
                        Text(
                            text = "Выбранное время"
                        )
                    }
                )
                if (errorMessage.value.isNotEmpty()) {
                    Text(
                        text = errorMessage.value,
                        color = Color.Red,
                    )
                }
            }
        }
    }
}

