package com.arsenosov.notebook.compose.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.arsenosov.notebook.compose.MyAppBar
import com.arsenosov.notebook.database.entitites.Contact
import com.arsenosov.notebook.database.entitites.Group
import com.arsenosov.notebook.util.Screen
import com.arsenosov.notebook.util.State
import com.arsenosov.notebook.viewmodels.MainViewModel
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.customView
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ContactsScreen(navController: NavController, viewModel: MainViewModel, openDrawer: () -> Unit) {
    val contactsList = viewModel.contactListLive.value
    val state = viewModel.stateContactsLive.value
    Column {
        MyAppBar(title = Screen.Contacts.title) {
            openDrawer()
        }
        if (state == State.LOADING) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(75.dp, 75.dp),
                )
            }
        } else {
            if (contactsList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Контактов пока нет. Добавьте один!",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(
                        modifier = Modifier.height(15.dp)
                    )
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.Black
                        ),
                        onClick = {
                            navController.navigate(Screen.NewContact.route) {
                                launchSingleTop = true
                            }
                        }) {
                        Text(
                            text = "Добавить контакт",
                            fontSize = 16.sp,
                        )
                    }
                }
            } else {
                ContactList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    viewModel = viewModel,
                    items = contactsList,
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun ContactList(modifier: Modifier, viewModel: MainViewModel, items: List<Contact>) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(
            items = items,
            itemContent = {
                Contact(
                    viewModel = viewModel,
                    contact = it,
                )
            }
        )
    }
}

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun Contact(viewModel: MainViewModel, contact: Contact) {
    val dialogState = rememberMaterialDialogState()
    val state = remember { mutableStateOf(State.LOADING)}
    val group = remember { mutableStateOf(Group.NoGroup)}
    LaunchedEffect(
        key1 = state.value,
    ) {
        if (state.value == State.LOADING) {
            group.value = viewModel.getGroupById(contact.groupId)
            state.value = State.SUCCESS
        }
    }
    if (state.value == State.LOADING) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            border = BorderStroke(
                width = 1.dp,
                color = Color.Black,
            ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp, 30.dp),
                )
            }
        }
    } else {
        ContactDialog(
            viewModel = viewModel,
            dialogState = dialogState,
            contact = contact,
            group = group.value,
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            border = BorderStroke(
                width = 1.dp,
                color = Color.Black,
            ),
            backgroundColor = Color(group.value.color),
            onClick = {
                dialogState.show()
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp),
            ) {
                Text(
                    text = contact.name,
                )
                Spacer(
                    modifier = Modifier
                        .height(5.dp),
                )
                Text(
                    text = contact.phone,
                )
            }
        }
    }
}

@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun ContactDialog(viewModel: MainViewModel, dialogState: MaterialDialogState, contact: Contact, group: Group) {
    val nameQuery = remember { mutableStateOf(contact.name) }
    val emailQuery = remember { mutableStateOf(contact.email) }
    val phoneQuery = remember { mutableStateOf(contact.phone) }
    val addressQuery = remember { mutableStateOf(contact.address) }
    val infoQuery = remember { mutableStateOf(contact.info) }
    val selectedGroupId = remember { mutableStateOf(contact.groupId) }
    val errorMessage = remember { mutableStateOf("") }
    val groupDialogState = rememberMaterialDialogState()
    val focusManager = LocalFocusManager.current
    GroupDialog(
        viewModel = viewModel,
        dialogState = groupDialogState,
        group = group,
        parentState = dialogState,
    )
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            button(
                text = "Сохранить",
            ) {
                val updatedContact = Contact(
                    id = contact.id,
                    name = nameQuery.value,
                    email = emailQuery.value,
                    phone = phoneQuery.value,
                    address = addressQuery.value,
                    info = infoQuery.value,
                    groupId = selectedGroupId.value
                )
                errorMessage.value = viewModel.checkUpdateContact(updatedContact)
                if (errorMessage.value.isEmpty())
                    dialogState.hide()
            }
            button(
                text = "Удалить"
            ) {
                errorMessage.value = viewModel.checkDeleteContact(contact)
                if (errorMessage.value.isEmpty())
                    dialogState.hide()
            }
            button(
                text = "Отмена"
            ) {
                dialogState.hide()
            }
            if (group.id != 1) {
                button(
                    text = "Изменить группу"
                ) {
                    groupDialogState.show()
                }
            }
        },
    ) {
        customView {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Имя",
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = nameQuery.value,
                    onValueChange = {
                        nameQuery.value = it
                        errorMessage.value = ""
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Email",
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = emailQuery.value,
                    onValueChange = {
                        emailQuery.value = it
                        errorMessage.value = ""
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Телефон",
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = phoneQuery.value,
                    onValueChange = {
                        phoneQuery.value = it
                        errorMessage.value = ""
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Адрес"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = addressQuery.value,
                    onValueChange = {
                        addressQuery.value = it
                        errorMessage.value = ""
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    singleLine = true,
                )
                OutlinedTextField(
                    label = {
                        Text(
                            text = "Дополнительная информация"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = infoQuery.value,
                    onValueChange = {
                        infoQuery.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = false,
                    maxLines = 5,
                )
                Spacer(
                    modifier = Modifier
                        .height(0.dp)
                )
                Text(
                    text = "Группа",
                )
                LazyVerticalGrid(
                    cells = GridCells.Fixed(3),
                ) {
                    items(
                        count = viewModel.groupListLive.value.size,
                        itemContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Card(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .border(1.dp, Color.Black, CircleShape)
                                        .size(30.dp),
                                    backgroundColor = Color(viewModel.groupListLive.value[it].color),
                                    onClick = {
                                        selectedGroupId.value = viewModel.groupListLive.value[it].id ?: 1
                                    }
                                ) {
                                    if (selectedGroupId.value == viewModel.groupListLive.value[it].id)
                                        Image(
                                            modifier = Modifier
                                                .fillParentMaxSize(),
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = null
                                        )
                                }
                                Spacer(
                                    modifier = Modifier
                                        .width(5.dp)
                                )
                                Text(
                                    text = viewModel.groupListLive.value[it].name,
                                )
                            }
                        }
                    )
                }
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

@ExperimentalMaterialApi
@InternalCoroutinesApi
@Composable
fun GroupDialog(viewModel: MainViewModel, dialogState: MaterialDialogState, group: Group, parentState: MaterialDialogState) {
    val nameQuery = remember { mutableStateOf(group.name) }
    val selectedColor = remember { mutableStateOf(group.color) }
    val errorMessage = remember { mutableStateOf("") }
    val colorDialogState = rememberMaterialDialogState()
    val focusManager = LocalFocusManager.current
    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            button(
                text = "Сохранить"
            ) {
                val updatedGroup = Group(
                    id = group.id,
                    name = nameQuery.value,
                    color = selectedColor.value,
                )
                errorMessage.value = viewModel.checkUpdateGroup(updatedGroup)
                if (errorMessage.value.isEmpty()) {
                    dialogState.hide()
                    parentState.hide()
                }
            }
            button(
                text = "Удалить"
            ) {
                errorMessage.value = viewModel.checkDeleteGroup(group)
                if (errorMessage.value.isEmpty()) {
                    dialogState.hide()
                    parentState.hide()
                }
            }
            button(
                text = "Отмена"
            ) {
                dialogState.hide()
            }
        }
    ) {
        customView {
            Column {
                OutlinedTextField(
                    value = nameQuery.value,
                    onValueChange = {
                        nameQuery.value = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    label = {
                        Text(
                            text = "Название"
                        )
                    },
                    singleLine = true,
                )
                Spacer(
                    modifier = Modifier
                        .height(5.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Цвет",
                    )
                    Spacer(
                        modifier = Modifier
                            .width(10.dp),
                    )
                    Card(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(1.dp, Color.Black, CircleShape)
                            .size(20.dp),
                        backgroundColor = Color(selectedColor.value),
                        onClick = {
                            colorDialogState.show()
                        }
                    ) {
                        MaterialDialog(
                            dialogState = colorDialogState,
                            buttons = {
                                positiveButton("Ок")
                                negativeButton("Отмена")
                            }
                        ) {
                            colorChooser(
                                colors = ColorPalette.Primary,
                                waitForPositiveButton = true,
                            )
                        }
                    }
                }
            }
        }
    }
}