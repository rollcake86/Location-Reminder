package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.core.IsNot
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config


@Config(sdk = [31])
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //Completed: provide testing to the RemindersListViewModel and its live data objects

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Subject under test
    private lateinit var remindersListViewModel: RemindersListViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var remindersRepository: FakeDataSource

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
    fun setupViewModel() {
        stopKoin()

        // Initialise the repository with no reminders.
        remindersRepository = FakeDataSource()

        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(), remindersRepository
        )
        remindersListViewModel.showLoading.value = true
    }

    @After
    fun endViewModel(){
        remindersListViewModel.showLoading.value = false
    }

    @Test
    fun getRemindersList() {
        val remindersList = mutableListOf(reminder1, reminder2, reminder3)
        remindersRepository = FakeDataSource(remindersList)
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
        remindersListViewModel.loadReminders()
        Assert.assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue(),
            (IsNot.not(emptyList()))
        )
        Assert.assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue().size,
            CoreMatchers.`is`(remindersList.size)
        )
    }

    @Test
    fun check_loading() {
        remindersRepository = FakeDataSource(mutableListOf())
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        Assert.assertThat(
            remindersListViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun returnError() {
        remindersRepository = FakeDataSource(mutableListOf())
        remindersListViewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
        remindersListViewModel.loadReminders()
        Assert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("No reminders found")
        )
    }
}