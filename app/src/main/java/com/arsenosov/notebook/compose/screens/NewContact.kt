package com.arsenosov.notebook.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
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
import com.arsenosov.notebook.compose.MyAppBar
import com.arsenosov.notebook.util.Screen
import com.arsenosov.notebook.viewmodels.MainViewModel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun NewContactScreen(viewModel: MainViewModel, openDrawer: () -> Unit) {
    Column {
        val queryName = viewModel.nameQueryContactLive.value
        val nameError = viewModel.nameErrorContactLive.value

        val queryEmail = viewModel.emailQueryContactLive.value
        val emailError = viewModel.emailErrorContactLive.value

        val queryPhone = viewModel.phoneQueryContactLive.value
        val phoneError = viewModel.phoneErrorContactLive.value

        val queryAddress = viewModel.addressQueryContactLive.value

        val queryInfo = viewModel.infoQueryContactLive.value

        val focusManager = LocalFocusManager.current

        MyAppBar(title = Screen.NewContact.title) {
            openDrawer()
        }
        Column(
            modifier = Modifier
                .padding(15.dp),
        ) {
            OutlinedTextField(
                label = {
                    Text(
                        text = if (nameError.isNotEmpty()) nameError else "Имя",
                    )
                },
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        errorBorderColor = Color.Red,
                        errorCursorColor = Color.Red,
                        errorLabelColor = Color.Red,
                    ),
                isError = nameError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth(),
                value = queryName,
                onValueChange = {newValue ->
                    viewModel.changeQueryNameContact(newValue)
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
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = if (emailError.isNotEmpty()) emailError else "Email",
                    )
                },
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        errorBorderColor = Color.Red,
                        errorCursorColor = Color.Red,
                        errorLabelColor = Color.Red,
                    ),
                isError = emailError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth(),
                value = queryEmail,
                onValueChange = {newValue ->
                    viewModel.changeQueryEmail(newValue)
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
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = if (phoneError.isNotEmpty()) phoneError else "Телефон",
                    )
                },
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        errorBorderColor = Color.Red,
                        errorCursorColor = Color.Red,
                        errorLabelColor = Color.Red,
                    ),
                isError = phoneError.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth(),
                value = queryPhone,
                onValueChange = {newValue ->
                    viewModel.changeQueryPhone(newValue)
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
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = "Адрес"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                value = queryAddress,
                onValueChange = {newValue ->
                    viewModel.changeQueryAddress(newValue)
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
            Spacer(
                modifier = Modifier
                    .height(10.dp)
            )
            OutlinedTextField(
                label = {
                    Text(
                        text = "Дополнительная информация"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                value = queryInfo,
                onValueChange = {newValue ->
                    viewModel.changeQueryInfo(newValue)
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
                    .height(10.dp)
            )
            Text(
                text = "Группа",
            )
            ContactGroupGrid(
                viewModel = viewModel,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White, contentColor = Color.Black),
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.checkNewContactSubmit()
                    }) {
                    Text(
                        text = "Добавить",
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
fun ContactGroupGrid(viewModel: MainViewModel) {
    val groups = viewModel.groupListLive.value
    val selected = viewModel.groupSelectedContactLive.value

    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        contentPadding = PaddingValues(
            top = 15.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 15.dp,
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
                            viewModel.changeSelectedGroupContact(it)
                        },
                    ) {
                        if (selected == it) {
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
                            .width(10.dp),
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