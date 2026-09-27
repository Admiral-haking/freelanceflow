package com.example

import com.example.ui.components.AppFormatters
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun formatCurrency_formatsProperly() {
        val formatted = AppFormatters.formatCurrency(1500.0, "USD")
        assertEquals("$1,500.00", formatted)
    }

    @Test
    fun formatSecondsToTime_formatsCorrectly() {
        val timeString = AppFormatters.formatSecondsToTime(3665)
        assertEquals("01:01:05", timeString)
    }

    @Test
    fun formatDurationShort_formatsHoursAndMinutes() {
        val duration = AppFormatters.formatDurationShort(5400)
        assertEquals("1h 30m", duration)
    }
}
