package com.arsenosov.notebook.viewmodels

import android.app.Application
import android.content.Context
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.arsenosov.notebook.database.Database
import com.arsenosov.notebook.database.entitites.Contact
import com.arsenosov.notebook.database.entitites.Group
import com.arsenosov.notebook.database.entitites.ScheduledCall
import com.arsenosov.notebook.util.State
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.*

@InternalCoroutinesApi
class MainViewModel(app: Application): AndroidViewModel(app) {

    init {
        //TODO("fix this abomination")
        /*viewModelScope.launch {
            delay(25)
            groupDao.insert(Group.NoGroup)
        }*/
        viewModelScope.launch {
            delay(100)
            contactFlowLive.collect { result ->
                contactListLive.value = result
                contactSelectedScheduleLive.value = result.firstOrNull()
                stateContactsLive.value = State.SUCCESS
            }
        }
        viewModelScope.launch {
            delay(100)
            groupFlowLive.collect { result ->
                groupListLive.value = result
                groupSelectedScheduleLive.value = result.first()
                groupSelectedContactLive.value = 1
            }
        }
        viewModelScope.launch {
            delay(100)
            scheduleFlowLive.collect { result ->
                scheduleListLive.value = result
                stateScheduledLive.value = State.SUCCESS
            }
        }
    }

    private val dataBase = Room
        .databaseBuilder(app, Database::class.java, "notebook-database")
        .fallbackToDestructiveMigration()
        .build()
    private val contactDao = dataBase.contactDao()
    private val groupDao = dataBase.groupDao()
    private val scheduledDao = dataBase.scheduledDao()
    //private val alarmManager = getSystemService()
    //Current states
    val stateContactsLive: MutableState<State> = mutableStateOf(State.LOADING)
    val stateScheduledLive: MutableState<State> = mutableStateOf(State.LOADING)
    //Flow of current schedules
    private val scheduleFlowLive: Flow<List<ScheduledCall>> = scheduledDao.getAll()
    //List of current schedules
    val scheduleListLive: MutableState<List<ScheduledCall>> = mutableStateOf(emptyList())
    //Flow of current contacts
    private val contactFlowLive: Flow<List<Contact>> = contactDao.getAll()
    //List of current contacts
    val contactListLive: MutableState<List<Contact>> = mutableStateOf(emptyList())
    //Flow of current groups
    private val groupFlowLive: Flow<List<Group>> = groupDao.getAll()
    //List of current groups
    val groupListLive: MutableState<List<Group>> = mutableStateOf(listOf(Group.NoGroup))

    //Name Query in TextField in NewGroup Screen
    val nameQueryGroupLive: MutableState<String> = mutableStateOf("")
    //Error in Name Query in TextField in NewGroup Screen
    val nameErrorGroupLive: MutableState<String> = mutableStateOf("")
    //Selected Color in NewGroup Screen //TODO("until release check color compatibility with ColorPicker Dialog")
    val colorSelectedGroupLive: MutableState<Color> = mutableStateOf(Color.Yellow)

    //Name Query is TextField in NewContact Screen
    val nameQueryContactLive: MutableState<String> = mutableStateOf("")
    //Error in Name Query in TextField in NewContact Screen
    val nameErrorContactLive: MutableState<String> = mutableStateOf("")
    //Name Query is TextField in NewContact Screen
    val emailQueryContactLive: MutableState<String> = mutableStateOf("")
    //Error in Name Query in TextField in NewContact Screen
    val emailErrorContactLive: MutableState<String> = mutableStateOf("")
    //Name Query is TextField in NewContact Screen
    val phoneQueryContactLive: MutableState<String> = mutableStateOf("")
    //Error in Name Query in TextField in NewContact Screen
    val phoneErrorContactLive: MutableState<String> = mutableStateOf("")
    //Address Query in TextField in NewContact Screen
    val addressQueryContactLive: MutableState<String> = mutableStateOf("")
    //Info Query in TextField in NewContact Screen
    val infoQueryContactLive: MutableState<String> = mutableStateOf("")
    //Currently Selected Group in NewContactScreen (no Group is default)
    val groupSelectedContactLive: MutableState<Int> = mutableStateOf(1)

    //Currently Selected Contact in DropDownMenu in NewSchedule Screen
    val contactSelectedScheduleLive: MutableState<Contact?> = mutableStateOf(contactListLive.value.firstOrNull())
    //Error in Selected Contact/Group in NewSchedule Screen
    val contactGroupErrorScheduledLive: MutableState<String> = mutableStateOf("")
    //Currently Selected Group in LazyColumn in NewSchedule Screen
    val groupSelectedScheduleLive: MutableState<Group> = mutableStateOf(groupListLive.value.first())
    //Currently Selected Date in NewSchedule Screen
    val dateSelectedScheduleLive: MutableState<LocalDate> = mutableStateOf(LocalDate.now())
    //Currently Selected Time in NewSchedule Screen
    val timeSelectedScheduleLive: MutableState<LocalTime> = mutableStateOf(LocalTime.now())
    //Error in DateTime in NewSchedule Screen
    val datetimeErrorScheduleLive: MutableState<String> = mutableStateOf("")

    fun changeQueryAddress(newValue: String) = newValue.also { addressQueryContactLive.value = it }
    fun changeQueryPhone(newValue: String) = newValue.also {
        phoneQueryContactLive.value = it
        phoneErrorContactLive.value = ""
    }
    fun changeQueryEmail(newValue: String) = newValue.also {
        emailQueryContactLive.value = it
        emailErrorContactLive.value = ""
    }
    fun changeQueryNameContact(newValue: String) = newValue.also {
        nameQueryContactLive.value = it
        nameErrorContactLive.value = ""
    }
    fun changeQueryNameGroup(newValue: String) = newValue.also {
        nameQueryGroupLive.value = newValue
        nameErrorGroupLive.value = ""
    }
    fun changeSelectedColorGroup(newValue: Color) = newValue.also { colorSelectedGroupLive.value = it}
    fun changeQueryInfo(newValue: String) = newValue.also { infoQueryContactLive.value = it}
    fun changeSelectedGroupContact(newValue: Int) = newValue.also { groupSelectedContactLive.value = it}
    fun changeSelectedGroupSchedule(newValue: Group) = newValue.also {
        groupSelectedScheduleLive.value = it
        contactSelectedScheduleLive.value = null
        contactGroupErrorScheduledLive.value = ""
    }
    fun changeSelectedContact(newValue: Contact) = newValue.also {
        groupSelectedScheduleLive.value = groupListLive.value.first()
        contactGroupErrorScheduledLive.value = ""
        contactSelectedScheduleLive.value = it
    }
    fun changeSelectedDate(newValue: LocalDate) = newValue.also {
        dateSelectedScheduleLive.value = it
        datetimeErrorScheduleLive.value = ""
    }
    fun changeSelectedTime(newValue: LocalTime) = newValue.also {
        timeSelectedScheduleLive.value = it
        datetimeErrorScheduleLive.value = ""
    }
    fun checkNewContactSubmit() {
        if (nameQueryContactLive.value.isBlank())
            nameErrorContactLive.value = "Имя не может быть пустым!"
        if (emailQueryContactLive.value.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailQueryContactLive.value).matches())
            emailErrorContactLive.value = "Email не подходит под формат!"
        if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneQueryContactLive.value))
            phoneErrorContactLive.value = "Телефон не подходит под формат!"
        else if (getByPhone(phoneQueryContactLive.value).isNotEmpty())
            phoneErrorContactLive.value = "Контакт с таким номером уже записан!"
        if (phoneErrorContactLive.value.isEmpty() &&
            emailErrorContactLive.value.isEmpty() &&
            nameErrorContactLive.value.isEmpty()) {
            viewModelScope.launch {
                contactDao.insert(Contact(
                    id = null,
                    name = nameQueryContactLive.value,
                    email = emailQueryContactLive.value,
                    address = addressQueryContactLive.value,
                    groupId = groupSelectedContactLive.value,
                    phone = phoneQueryContactLive.value,
                    info = infoQueryContactLive.value,
                ))
                Toast.makeText(getApplication<Application>().applicationContext, "Контакт ${nameQueryContactLive.value} успешно записан!", Toast.LENGTH_LONG).show()
                stateContactsLive.value = State.LOADING
                nameQueryContactLive.value = ""
                emailQueryContactLive.value = ""
                addressQueryContactLive.value = ""
                groupSelectedContactLive.value = 1
                phoneQueryContactLive.value = ""
                infoQueryContactLive.value = ""
            }
        }
    }
    private fun getByPhone(phone: String): List<Contact> {
        var result: List<Contact> = emptyList()
        viewModelScope.launch {
            result = contactDao.getAllByPhone(phone)
        }
        return result
    }
    private fun constructScheduledList(list: List<ScheduledCall>) {
        val newList: MutableList<MutableList<ScheduledCall>> = emptyList<MutableList<ScheduledCall>>() as MutableList<MutableList<ScheduledCall>>

    }
    fun checkNewScheduleSubmit() {
        if (LocalDateTime.ofInstant(dateSelectedScheduleLive.value.atTime(timeSelectedScheduleLive.value)
                .atZone(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()).isBefore(
                LocalDateTime.now()))
            datetimeErrorScheduleLive.value = "Нельзя запланировать созвон на время, ранее текущего!"
        if (contactSelectedScheduleLive.value == null && groupSelectedScheduleLive.value == Group.NoGroup ||
            contactSelectedScheduleLive.value != null && groupSelectedScheduleLive.value != Group.NoGroup)
            contactGroupErrorScheduledLive.value = "Необходимо выбрать либо контакт, либо группу!"
        if (datetimeErrorScheduleLive.value.isEmpty() &&
            contactGroupErrorScheduledLive.value.isEmpty()) {
            viewModelScope.launch {
                scheduledDao.insert(
                    ScheduledCall(
                        id = null,
                        contactId = if (groupSelectedScheduleLive.value == Group.NoGroup) contactSelectedScheduleLive.value?.id else null,
                        groupId = if (contactSelectedScheduleLive.value == null) groupSelectedScheduleLive.value.id!! else 1,
                        time = dateSelectedScheduleLive.value.atTime(timeSelectedScheduleLive.value)
                            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),)
                )
                Toast.makeText(getApplication<Application>().applicationContext, "Созвон успешно записан!", Toast.LENGTH_LONG).show()
                stateScheduledLive.value = State.LOADING
                contactSelectedScheduleLive.value = contactListLive.value.firstOrNull()
                groupSelectedScheduleLive.value = groupListLive.value.first()
                timeSelectedScheduleLive.value = LocalTime.now()
                dateSelectedScheduleLive.value = LocalDate.now()
            }
        }
    }
    fun checkNewGroupSubmit() {
        if (nameQueryGroupLive.value.isBlank())
            nameErrorGroupLive.value = "Имя группы не может быть пустым!"
        else
            viewModelScope.launch {
                groupDao.insert(
                    Group(
                        id = null,
                        name = nameQueryGroupLive.value,
                        color = colorSelectedGroupLive.value.toArgb(),
                    )
                )
                Toast.makeText(getApplication<Application>().applicationContext, "Группа ${nameQueryGroupLive.value} успешно записана!", Toast.LENGTH_LONG).show()
                nameQueryGroupLive.value = ""
            }
    }
    suspend fun getGroupById(id: Int): Group {
        return groupDao.getById(id)
    }
    suspend fun getContactById(id: Int?): Contact? {
        if (id == null) return null
        return contactDao.getById(id)
    }
    private fun getSchedulesByContact(id: Int): List<ScheduledCall> {
        var result = emptyList<ScheduledCall>()
        viewModelScope.launch {
            result = scheduledDao.getByContact(id)
        }
        return result
    }
    private fun getSchedulesByGroup(id: Int): List<ScheduledCall> {
        var result = emptyList<ScheduledCall>()
        viewModelScope.launch {
            result = scheduledDao.getByGroup(id)
        }
        return result
    }
    private suspend fun getContactsByGroup(id: Int): List<Contact> {
        return contactDao.getByGroupId(id)
    }
    fun checkUpdateContact(contact: Contact): String {
        if (contact.name.isBlank()) return "Имя не может быть пустым!"
        if (contact.email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(contact.email).matches()) return "Email не подходит под формат!"
        if (!PhoneNumberUtils.isGlobalPhoneNumber(contact.phone)) return "Номер не подходит под формат!"
        if (getByPhone(contact.phone).isNotEmpty() && getByPhone(contact.phone)[0].id != contact.id) return "Контакт с таким номером уже существует!"
        viewModelScope.launch {
            contactDao.update(contact)
            stateContactsLive.value = State.LOADING
        }
        return ""
    }
    fun checkDeleteContact(contact: Contact): String {
        if (getSchedulesByContact(contact.id!!).isNotEmpty()) return "Невозможно удалить контакт, на которого зарегистрирован созвон!"
        viewModelScope.launch {
            contactDao.delete(contact)
            stateContactsLive.value = State.LOADING
        }
        return ""
    }
    fun checkUpdateGroup(group: Group): String {
        if (group.name.isBlank()) return "Название не может быть пустым!"
        viewModelScope.launch {
            groupDao.update(group)
        }
        return ""
    }
    fun checkDeleteGroup(group: Group): String {
        if (getSchedulesByGroup(group.id!!).isNotEmpty()) return "Невозможно удалить группу, на которую зарегистрирован созвон!"
        viewModelScope.launch {
            val list  = getContactsByGroup(group.id)
            list.forEach {
                contactDao.update(Contact(
                    id = it.id,
                    name = it.name,
                    email = it.email,
                    address = it.address,
                    phone = it.phone,
                    groupId = 1,
                    info = it.info,
                ))
            }
            groupDao.delete(group)
            stateContactsLive.value = State.LOADING
        }
        return ""
    }
    fun checkUpdateSchedule(scheduledCall: ScheduledCall): String {
        if (LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledCall.time), ZoneId.systemDefault()).isBefore(LocalDateTime.now()))
            return "Нельзя запланировать созвон на время, ранее текущего!"
        if (scheduledCall.contactId == null && scheduledCall.groupId == 1
            || scheduledCall.contactId != null && scheduledCall.groupId != 1)
                return "Необходимо выбрать либо контакт, либо группу!"
        viewModelScope.launch {
            scheduledDao.update(scheduledCall)
            stateScheduledLive.value = State.LOADING
        }
        return ""
    }
    fun checkDeleteSchedule(scheduledCall: ScheduledCall) {
        viewModelScope.launch {
            scheduledDao.delete(scheduledCall)
            stateScheduledLive.value = State.LOADING
        }
    }
}