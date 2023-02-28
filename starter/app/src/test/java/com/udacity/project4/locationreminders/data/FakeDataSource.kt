package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var remindersServiceData: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        return Result.Success(remindersServiceData.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersServiceData.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Test exception ")
        }
        remindersServiceData.firstOrNull { it.id == id }?.let { return Result.Success(it) }
        return Result.Error("Could not find reminder")
    }

    override suspend fun deleteAllReminders() {
        remindersServiceData.clear()
    }


}