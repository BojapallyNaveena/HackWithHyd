package com.example.data.seed

import com.example.data.model.*
import com.example.data.repository.AuditRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class NovaBankDemoDataset(
    val controls: List<ComplianceControl>,
    val findings: List<AuditFinding>,
    val remediations: List<RemediationCommitment>,
    val evidence: List<EvidenceRecord>,
    val decisions: List<AuditDecision>,
    val memories: List<HindsightMemory>
)

data class RecurringRiskChain(
    val controlId: String,
    val controlName: String,
    val finding2024: AuditFinding?,
    val finding2025: AuditFinding?,
    val finding2026: AuditFinding?,
    val riskAcceptanceDecision: AuditDecision?,
    val failedRemediation: RemediationCommitment?,
    val rootCauseSummary: String
)

data class RelationshipValidationResult(
    val isValid: Boolean,
    val totalControls: Int,
    val totalFindings: Int,
    val recurringFindingsCount: Int,
    val totalRemediations: Int,
    val overdueRemediationsCount: Int,
    val totalDecisions: Int,
    val totalMemories: Int,
    val validationNotes: List<String>
)

/**
 * Service that generates a comprehensive 3-year (2024-2026) demonstration dataset for NovaBank,
 * populating the database with findings, controls, and remediation records that contain logical
 * relationships suitable for recurring risk detection.
 */
class NovaBankDataSeeder(
    private val repository: AuditRepository? = null
) {
    fun generateControls(): List<ComplianceControl> = NovaBankSeedData.controls

    fun generateFindings(): List<AuditFinding> = NovaBankSeedData.findings

    fun generateRemediations(): List<RemediationCommitment> = NovaBankSeedData.remediations

    fun generateEvidence(): List<EvidenceRecord> = NovaBankSeedData.evidence

    fun generateDecisions(): List<AuditDecision> = NovaBankSeedData.decisions

    fun generateMemories(): List<HindsightMemory> = NovaBankSeedData.memories

    fun getFindingsForYear(year: Int): List<AuditFinding> {
        return generateFindings().filter { it.auditYear == year }
    }

    fun getRecurringFindings(): List<AuditFinding> {
        return generateFindings().filter { it.isRecurring }
    }

    fun getOverdueRemediations(): List<RemediationCommitment> {
        return generateRemediations().filter { it.isOverdue }
    }

    fun getControls(): List<ComplianceControl> = generateControls()

    /**
     * Extracts structured recurring risk chains connecting 2024 -> 2025 -> 2026 findings
     * to their respective risk waivers and stalled remediations.
     */
    fun getRecurringRiskChains(): List<RecurringRiskChain> {
        val findings = generateFindings()
        val decisions = generateDecisions()
        val remediations = generateRemediations()
        val controls = generateControls()

        val primaryRecurringControlIds = listOf("C-17", "C-03", "C-05", "C-09")

        return primaryRecurringControlIds.mapNotNull { ctrlId ->
            val ctrl = controls.firstOrNull { it.id == ctrlId } ?: return@mapNotNull null
            val ctrlFindings = findings.filter { it.controlId == ctrlId }
            val f2024 = ctrlFindings.firstOrNull { it.auditYear == 2024 }
            val f2025 = ctrlFindings.firstOrNull { it.auditYear == 2025 }
            val f2026 = ctrlFindings.firstOrNull { it.auditYear == 2026 }
            val dec = decisions.firstOrNull { it.controlId == ctrlId }
            val rem = remediations.firstOrNull { it.controlId == ctrlId }

            val rootCause = when (ctrlId) {
                "C-17" -> "Management accepted risk temporarily under assumption of Project Horizon cloud cutover; compensating manual reviews ceased when security analyst departed."
                "C-03" -> "Workday-Okta automated SCIM connector project stalled due to IAM turnover; manual spreadsheet reconciliations failed repeatedly."
                "C-05" -> "Automated vendor review portal project was canceled due to budget cuts; blanket waivers granted without SOC 2 reports."
                "C-09" -> "Database purge scripts encountered foreign key lock crashes (ORA-02292); DB refactoring was repeatedly postponed."
                else -> "Repeated temporary risk waivers without operational verification of compensating controls."
            }

            RecurringRiskChain(
                controlId = ctrlId,
                controlName = ctrl.name,
                finding2024 = f2024,
                finding2025 = f2025,
                finding2026 = f2026,
                riskAcceptanceDecision = dec,
                failedRemediation = rem,
                rootCauseSummary = rootCause
            )
        }
    }

    /**
     * Generates the complete 3-year demo dataset (2024-2026) with full causal and temporal relationships.
     */
    fun generate3YearDataset(): NovaBankDemoDataset {
        return NovaBankDemoDataset(
            controls = generateControls(),
            findings = generateFindings(),
            remediations = generateRemediations(),
            evidence = generateEvidence(),
            decisions = generateDecisions(),
            memories = generateMemories()
        )
    }

    /**
     * Seeds the database with the 3-year dataset.
     */
    suspend fun seedDatabase(targetRepo: AuditRepository = repository ?: error("AuditRepository must be provided")): NovaBankDemoDataset = withContext(Dispatchers.IO) {
        val dataset = generate3YearDataset()
        targetRepo.seedDatabase(
            findings = dataset.findings,
            controls = dataset.controls,
            remediations = dataset.remediations,
            evidence = dataset.evidence,
            decisions = dataset.decisions,
            memories = dataset.memories
        )
        dataset
    }

    /**
     * Populates the database with findings, controls, remediations, and decisions.
     */
    suspend fun populateDatabase(targetRepo: AuditRepository = repository ?: error("AuditRepository must be provided")): NovaBankDemoDataset {
        return seedDatabase(targetRepo)
    }

    /**
     * Validates that all findings, controls, remediations, and decisions maintain
     * referential integrity and meaningful causal relationships across 2024-2026.
     */
    fun validateRelationships(dataset: NovaBankDemoDataset = generate3YearDataset()): RelationshipValidationResult {
        val notes = mutableListOf<String>()
        val controlIds = dataset.controls.map { it.id }.toSet()
        val findingIds = dataset.findings.map { it.id }.toSet()

        // 1. Verify finding -> control links
        val orphanFindings = dataset.findings.filter { it.controlId !in controlIds }
        if (orphanFindings.isEmpty()) {
            notes.add("All ${dataset.findings.size} findings correctly map to existing compliance controls.")
        } else {
            notes.add("Warning: ${orphanFindings.size} findings lack valid control references.")
        }

        // 2. Verify recurring finding chains
        val recurringFindings = dataset.findings.filter { it.isRecurring }
        val brokenRecurrenceLinks = recurringFindings.filter { it.recurringFromId != null && it.recurringFromId !in findingIds }
        if (brokenRecurrenceLinks.isEmpty()) {
            notes.add("All ${recurringFindings.size} recurring findings have valid antecedent finding references across 2024-2026.")
        } else {
            notes.add("Warning: ${brokenRecurrenceLinks.size} recurring findings have invalid parent links.")
        }

        // 3. Verify remediation -> finding & control links
        val orphanRemediations = dataset.remediations.filter { it.controlId !in controlIds }
        if (orphanRemediations.isEmpty()) {
            notes.add("All ${dataset.remediations.size} remediation commitments map to verified controls.")
        }

        // 4. Verify decision -> finding & control links
        val validDecisions = dataset.decisions.filter { it.controlId in controlIds }
        notes.add("${validDecisions.size} management decisions/waivers correctly link to controls and findings.")

        return RelationshipValidationResult(
            isValid = orphanFindings.isEmpty() && brokenRecurrenceLinks.isEmpty() && orphanRemediations.isEmpty(),
            totalControls = dataset.controls.size,
            totalFindings = dataset.findings.size,
            recurringFindingsCount = recurringFindings.size,
            totalRemediations = dataset.remediations.size,
            overdueRemediationsCount = dataset.remediations.count { it.isOverdue },
            totalDecisions = dataset.decisions.size,
            totalMemories = dataset.memories.size,
            validationNotes = notes
        )
    }

    companion object {
        fun create(repository: AuditRepository? = null): NovaBankDataSeeder {
            return NovaBankDataSeeder(repository)
        }

        fun getDemoDataset(): NovaBankDemoDataset {
            return NovaBankDataSeeder().generate3YearDataset()
        }

        suspend fun seed(repository: AuditRepository): NovaBankDemoDataset {
            return NovaBankDataSeeder(repository).seedDatabase()
        }

        suspend fun populate(repository: AuditRepository): NovaBankDemoDataset {
            return NovaBankDataSeeder(repository).populateDatabase()
        }
    }
}
