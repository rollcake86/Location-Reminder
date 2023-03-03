package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.FakeReminderDao
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersDAO: FakeReminderDao
    private lateinit var repository: RemindersLocalRepository

    val reminder1 = ReminderDTO(
        "title1",
        "description1",
        "somewhere1",
        11.0,
        11.0,
        "random1"
    )
    val reminder2 = ReminderDTO(
        "title2",
        "descriptio2n",
        "somewhere2",
        12.0,
        12.0,
        "random2"
    )
    val reminder3 = ReminderDTO(
        "title3",
        "description3",
        "somewhere3",
        13.0,
        13.0,
        "random3"
    )

    @Before
    fun setup() {
        remindersDAO = FakeReminderDao()
        repository =
            RemindersLocalRepository(
                remindersDAO
            )
    }



    @Test
    fun insertThreeReminders_getAllThreeFromDatabase() = runBlocking {
        // GIVEN - insert three reminders in the database
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)
        val loadedRemindersList = (repository.getReminders() as Result.Success)?.data
        assertThat(loadedRemindersList?.size, `is`(3))
        assertThat(loadedRemindersList?.isNotEmpty(), `is`(true))
    }

    @Test
    fun getReminderByIdThatExistsInLocalCache() = runBlocking {
        repository.saveReminder(reminder1)

        val loadedReminder = (repository.getReminder(reminder1.id) as Result.Success).data

        assertThat(loadedReminder.id, `is`(reminder1.id))
        assertThat(loadedReminder.title, `is`(reminder1.title))
        assertThat(loadedReminder.description, `is`(reminder1.description))
        assertThat(loadedReminder.location, `is`(reminder1.location))
        assertThat(loadedReminder.latitude, `is`(reminder1.latitude))
        assertThat(loadedReminder.longitude,`is`(reminder1.longitude))
    }

    @Test
    fun getReminderByIdThatDoesNotExistInLocalCache() = runBlocking {
        val reminder = repository.getReminder("fake") as Result.Error
        // THEN - The loaded data contains the expected values
        assertThat(reminder.message, `is`("Reminder not found!"))
    }

    @Test
    fun deleteAllReminders_EmptyListFetchedFromLocalCache() = runBlocking {
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)
        repository.saveReminder(reminder3)
        val reminder = (repository.getReminders() as Result.Success)?.data
        assertThat(reminder?.isNotEmpty(), `is`(true) )
        repository.deleteAllReminders()
        // Then - fetching should return empty list
        val reminder2 = (repository.getReminders() as Result.Success)?.data
        assertThat(reminder2?.isEmpty(), `is`(true) )
    }
}