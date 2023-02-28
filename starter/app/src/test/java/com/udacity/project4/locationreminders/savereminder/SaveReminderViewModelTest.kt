package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.pauseDispatcher
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@Config(sdk = [31])
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var remindersLocalRepository: FakeDataSource
    val list = listOf(ReminderDataItem("title", "description","location",(-360..360).random().toDouble(),(-360..360).random().toDouble()))
    private val firstReminder = list[0]
    @Before
    fun setupViewModel() {
        // Initialise the repository with no reminders.
        remindersLocalRepository = FakeDataSource()
        val appContext = ApplicationProvider.getApplicationContext() as Application
        saveReminderViewModel = SaveReminderViewModel(appContext, remindersLocalRepository)
    }

    @Test
    fun check_loading() {
        remindersLocalRepository = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersLocalRepository)
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(firstReminder)
        Assert.assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), CoreMatchers.`is`(true))
    }

    @Test
    fun returnError() {
        remindersLocalRepository = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersLocalRepository)
        firstReminder.title = null
        saveReminderViewModel.validateAndSaveReminder(firstReminder)
        Assert.assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), CoreMatchers.`is`(
            R.string.err_enter_title))
    }

    @After
    fun tearDown() {
        stopKoin()
    }
}