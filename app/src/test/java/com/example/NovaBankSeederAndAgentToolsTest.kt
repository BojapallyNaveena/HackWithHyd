package com.example

import com.example.ai.AgentTools
import com.example.data.seed.NovaBankDataSeeder
import org.junit.Assert.*
import org.junit.Test

class NovaBankSeederAndAgentToolsTest {

    @Test
    fun testNovaBankDataSeeder_generates3YearDataset() {
        val seeder = NovaBankDataSeeder()
        val dataset = seeder.generate3YearDataset()

        assertTrue("Controls list should not be empty", dataset.controls.isNotEmpty())
        assertTrue("Findings list should not be empty", dataset.findings.isNotEmpty())
        assertTrue("Remediations list should not be empty", dataset.remediations.isNotEmpty())
        assertTrue("Decisions list should not be empty", dataset.decisions.isNotEmpty())
        assertTrue("Memories list should not be empty", dataset.memories.isNotEmpty())

        val years = dataset.findings.map { it.auditYear }.distinct()
        assertTrue("Dataset must cover 2024", years.contains(2024))
        assertTrue("Dataset must cover 2025", years.contains(2025))
        assertTrue("Dataset must cover 2026", years.contains(2026))

        val validation = seeder.validateRelationships(dataset)
        assertTrue("Dataset referential relationships must be valid: ${validation.validationNotes}", validation.isValid)
    }

    @Test
    fun testAgentTools_declarationsContainCoreTools() {
        val seeder = NovaBankDataSeeder()
        val dataset = seeder.generate3YearDataset()
        assertNotNull(dataset)

        val chains = seeder.getRecurringRiskChains()
        assertTrue("Should contain multiple recurring risk chains", chains.size >= 4)
        val c17 = chains.firstOrNull { it.controlId == "C-17" }
        assertNotNull("Should contain C-17 PAM chain", c17)
        assertNotNull("Should have 2024 finding", c17?.finding2024)
        assertNotNull("Should have 2025 finding", c17?.finding2025)
        assertNotNull("Should have 2026 finding", c17?.finding2026)
        assertNotNull("Should have risk acceptance decision", c17?.riskAcceptanceDecision)
        assertNotNull("Should have failed remediation", c17?.failedRemediation)
    }
}
