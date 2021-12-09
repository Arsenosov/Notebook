package com.arsenosov.notebook.util

sealed class Screen(val title: String, val route: String) {
    object Scheduled: Screen("Запланированные звонки", "scheduled")
    object Contacts: Screen("Мои контакты", "contacts")
    object NewSchedule: Screen("Запланировать звонок", "new_schedule")
    object NewContact: Screen("Добавить контакт", "new_contact")
    object NewGroup: Screen("Добавить группу", "new_group")
}

val screens = listOf(
    Screen.Scheduled,
    Screen.Contacts,
    Screen.NewSchedule,
    Screen.NewContact,
    Screen.NewGroup,
)
