package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.seed.NovaBankSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AuditMind AI", appName)
    }

    @Test
    fun `verify novabank multi-year seed data integrity`() {
        val findings = NovaBankSeedData.findings
        val controls = NovaBankSeedData.controls
        val memories = NovaBankSeedData.memories

        assertTrue("Should have at least 30 findings", findings.size >= 30)
        assertTrue("Should have at least 10 controls", controls.size >= 10)
        assertTrue("Should have Hindsight persistent memories", memories.isNotEmpty())

        val recurringFindings = findings.filter { it.isRecurring }
        assertTrue("Should have multiple recurring findings across years", recurringFindings.size >= 3)
    }
}
