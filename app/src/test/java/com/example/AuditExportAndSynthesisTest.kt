package com.example

import com.example.data.model.AuditFinding
import com.example.data.model.AuditDecision
import com.example.data.model.RemediationCommitment
import com.example.data.model.Severity
import com.example.data.model.FindingStatus
import com.example.data.model.HindsightMemory
import com.example.data.model.MemoryType
import com.example.service.export.AuditExportData
import org.junit.Assert.*
import org.junit.Test

class AuditExportAndSynthesisTest {

    @Test
    fun testAuditExportDataStructure() {
        val exportData = AuditExportData(
            auditYear = 2026,
            auditTitle = "NovaBank Annual Security & Regulatory Audit (2026)",
            generatedDate = "2026-09-28 10:00:00",
            findings = listOf(
                AuditFinding(
                    id = "F-301",
                    auditYear = 2026,
                    auditTitle = "2026 Comprehensive Security Review",
                    title = "Oracle Core Banking Bastion Hardware MFA Bypass",
                    controlId = "C-17",
                    controlName = "Privileged Access Management & Bastion MFA",
                    severity = Severity.CRITICAL,
                    status = FindingStatus.RECURRING,
                    category = "Access Control",
                    description = "89 direct root sessions logged without hardware MFA.",
                    isRecurring = true,
                    owner = "IT Security Team",
                    riskAcceptanceStatus = com.example.data.model.RiskAcceptanceStatus.ACCEPTED_TEMPORARILY
                )
            ),
            remediations = listOf(
                RemediationCommitment(
                    id = "R-22",
                    findingId = "F-104",
                    controlId = "C-17",
                    title = "CyberArk PAM Bastion MFA Enforcement",
                    description = "Deploy CyberArk PAM Bastion MFA",
                    committedDate = "2024-04-15",
                    deadline = "2024-11-30",
                    owner = "Marcus Vance (CISO)",
                    completionPercentage = 35,
                    isOverdue = true,
                    status = com.example.data.model.RemediationStatus.OVERDUE,
                    verificationNotes = "CyberArk agent incompatible with AIX OS"
                )
            ),
            decisions = listOf(
                AuditDecision(
                    id = "DEC-2024-01",
                    findingId = "F-104",
                    controlId = "C-17",
                    year = 2024,
                    decisionType = "Risk Acceptance (Temporary)",
                    decisionMaker = "Marcus Vance (CISO)",
                    title = "Temporary Risk Acceptance for Bastion MFA",
                    rationale = "Project Horizon cloud migration will retire server in Q1 2025",
                    compensatingControl = "Weekly manual auth.log reviews",
                    promisedRemediation = "Deploy CyberArk PAM",
                    targetDeadline = "2024-11-30",
                    currentRealStatus = "Expired & Unrevoked"
                )
            ),
            memories = listOf(
                HindsightMemory(
                    id = "MEM-01",
                    text = "CISO accepted risk on bastion MFA awaiting cloud cutover",
                    tags = listOf("C-17", "decision", "risk-acceptance"),
                    timestamp = System.currentTimeMillis(),
                    temporalYear = 2024,
                    entities = listOf("C-17", "Bastion MFA"),
                    confidence = 0.98f,
                    memoryType = MemoryType.DECISION
                )
            ),
            executiveSummary = "Critical recurring exposures identified under OCC & SOC 2 frameworks."
        )

        assertEquals(2026, exportData.auditYear)
        assertEquals(1, exportData.findings.size)
        assertEquals(1, exportData.remediations.size)
        assertEquals(1, exportData.decisions.size)
        assertEquals(1, exportData.memories.size)
        assertTrue(exportData.findings.first().isRecurring)
        assertTrue(exportData.remediations.first().isOverdue)
    }
}
