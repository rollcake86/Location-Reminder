package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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

    @Before
    fun setupViewModel() {
        // Initialise the repository with no reminders.
        remindersLocalRepository = FakeDataSource()
        val appContext = ApplicationProvider.getApplicationContext() as Application
        saveReminderViewModel = SaveReminderViewModel(appContext, remindersLocalRepository)
    }

    @Test
    fun whenIncompleteInfo_validationReturnsNull() {
        // GIVEN - incomplete reminder fields, title is null
        saveReminderViewModel.onClear()
        saveReminderViewModel.reminderTitle.value = null
        saveReminderViewModel.reminderDescription.value = "some description"
        saveReminderViewModel.reminderSelectedLocationStr.value = null
        saveReminderViewModel.longitude.value = 10.0
        saveReminderViewModel.latitude.value = 10.0

        // WHEN - atempting to validate
        val result = saveReminderViewModel.validateEnteredData(
            ReminderDataItem(
                saveReminderViewModel.reminderTitle.value,
                saveReminderViewModel.reminderDescription.value,
                saveReminderViewModel.reminderSelectedLocationStr.value,
                saveReminderViewModel.longitude.value,
                saveReminderViewModel.latitude.value,
                "someId"
            )
        )
        // THEN - result is false
        MatcherAssert.assertThat(result, Is.`is`(false))

    }
}