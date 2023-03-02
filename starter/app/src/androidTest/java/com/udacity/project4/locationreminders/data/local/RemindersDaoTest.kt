package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import junit.framework.TestCase.assertNull

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import org.robolectric.annotation.Config
import java.util.*

@Config(sdk = [31])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runBlocking {
        // GIVEN - insert a reminder
        val reminder = ReminderDTO(
            "title",
            "description",
            "somewhere",
            12.0,
            12.0,
            "random222"
        )
        database.reminderDao().saveReminder(reminder)

        // WHEN - Get the reminder by id from the database
        val loaded = database.reminderDao().getReminderById(reminder.id)

        print("load data : ${loaded?.id} , ${loaded?.title}")
        // THEN - The loaded data contains the expected values
        assertThat(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminderByIdNotFound() = runBlocking {
        // GIVEN - a random reminder id
        val reminderId = UUID.randomUUID().toString()
        // WHEN - Get the reminder by id from the database.
        val loaded = database.reminderDao().getReminderById(reminderId)
        // THEN - The loaded data should be  null.
        assertNull(loaded)
    }


    @Test
    fun deleteReminders() = runBlocking {
        // Given - reminders inserted
        val remindersList = listOf<ReminderDTO>(
            ReminderDTO(
                "title",
                "description",
                "somewhere",
                12.0,
                12.0,
                "random1"
            ),
            ReminderDTO(
                "title",
                "description",
                "somewhere",
                12.0,
                12.0,
                "random2"
            ),
            ReminderDTO(
                "title",
                "description",
                "somewhere",
                12.0,
                12.0,
                "random3"
            )
        )

        remindersList.forEach {
            database.reminderDao().saveReminder(it)
        }

        // WHEN - deleting all reminders
        database.reminderDao().deleteAllReminders()

        // THEN - The list is empty
        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.isEmpty(), `is`(true))
    }
}