package com.arsenosov.notebook.compose

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arsenosov.notebook.alarm.AlarmReceiver.Companion.CHANNEL_NAME
import com.arsenosov.notebook.alarm.AlarmReceiver.Companion.NOTIFICATION_ID
import com.arsenosov.notebook.compose.screens.*
import com.arsenosov.notebook.ui.theme.NotebookTheme
import com.arsenosov.notebook.util.Screen
import com.arsenosov.notebook.util.screens
import com.arsenosov.notebook.viewmodels.MainViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {

    private lateinit var myMainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myMainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContent {
            NotebookTheme {
                MainScreen(viewModel = myMainViewModel)
            }
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_NAME, "notebookAlarmChannel", NotificationManager.IMPORTANCE_HIGH)
            channel.description = "Канал для уведомлений о созвонах"
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@InternalCoroutinesApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val openDrawer = {
        scope.launch {
            drawerState.open()
        }
    }
    Surface {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalDrawer(
                drawerState = drawerState,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        SideBar(navController = navController,
                            onDestinationClicked = { route ->
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            })
                    }
                }
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Scheduled.route,
                    ) {
                        composable(Screen.Scheduled.route) {
                            ScheduledScreen(navController, viewModel) {
                                openDrawer()
                            }
                        }
                        composable(Screen.NewSchedule.route) {
                            NewScheduleScreen(viewModel) {
                                openDrawer()
                            }
                        }
                        composable(Screen.Contacts.route) {
                            ContactsScreen(navController, viewModel) {
                                openDrawer()
                            }
                        }
                        composable(Screen.NewContact.route) {
                            NewContactScreen(viewModel) {
                                openDrawer()
                            }
                        }
                        composable(Screen.NewGroup.route) {
                            NewGroupScreen(viewModel) {
                                openDrawer()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyAppBar(title: String, onClick: () -> Unit) {
    TopAppBar(
        title = { Text(
            text = title
        )},
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.background,
        contentColor = contentColorFor(backgroundColor = MaterialTheme.colors.background),
        actions = {
            IconButton(
                onClick = { onClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open navigation menu",
                )
            }
        }
    )
}

@Composable
fun SideBar(modifier: Modifier = Modifier,
            navController: NavController,
            onDestinationClicked: (route: String) -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        screens.forEach { screen ->
            val backgroundColor = if (screen.route == currentRoute) Color.LightGray else Color.Transparent
            Text(
                text = screen.title,
                fontSize = 20.sp,
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(color = backgroundColor)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clickable {
                        onDestinationClicked(screen.route)
                    }
            )
        }
    }
}