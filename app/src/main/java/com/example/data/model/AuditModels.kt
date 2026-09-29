package com.example.data.model

enum class Severity {
    CRITICAL, HIGH, MEDIUM, LOW
}

enum class FindingStatus {
    OPEN, IN_PROGRESS, CLOSED, OVERDUE, RECURRING
}

enum class RiskAcceptanceStatus {
    ACCEPTED_TEMPORARILY, REJECTED, UNRESOLVED, REMEDIATED, NONE
}

enum class RemediationStatus {
    OVERDUE, INCOMPLETE, IN_PROGRESS, VERIFIED, WAITING_EVIDENCE
}

enum class ControlHealth {
    CRITICAL_RECURRING, FAILING, PARTIAL, HEALTHY
}

enum class MemoryType {
    FINDING, CONTROL, DECISION, REMEDIATION, POLICY, EVIDENCE, OBSERVATION
}

data class AuditFinding(
    val id: String, // e.g. "F-104", "F-204", "F-301"
    val auditYear: Int, // 2024, 2025, 2026
    val auditTitle: String,
    val title: String,
    val description: String,
    val category: String, // e.g. "Access Control", "Vendor Security", "Data Retention", "Incident Response"
    val severity: Severity,
    val controlId: String, // e.g. "C-17"
    val controlName: String,
    val owner: String, // e.g. "IT Security Team"
    val riskAcceptanceStatus: RiskAcceptanceStatus,
    val riskAcceptanceReason: String? = null,
    val compensatingControl: String? = null,
    val remediationId: String? = null,
    val recurringFromId: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceCount: Int = 0,
    val status: FindingStatus,
    val evidenceSummary: String? = null
)

data class ComplianceControl(
    val id: String, // e.g. "C-17"
    val code: String, // e.g. "AC-02.1"
    val name: String,
    val department: String,
    val policyRef: String,
    val frequency: String,
    val description: String,
    val lastAuditedYear: Int,
    val failureHistoryYears: List<Int>,
    val recurrenceRiskLevel: String, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    val healthStatus: ControlHealth,
    val totalFindingsCount: Int = 0
)

data class RemediationCommitment(
    val id: String, // e.g. "R-22"
    val findingId: String,
    val controlId: String,
    val title: String,
    val description: String,
    val owner: String,
    val committedDate: String,
    val deadline: String,
    val isOverdue: Boolean,
    val completionPercentage: Int,
    val status: RemediationStatus,
    val verificationNotes: String? = null
)

data class EvidenceRecord(
    val id: String,
    val findingId: String?,
    val controlId: String?,
    val title: String,
    val type: String,
    val year: Int,
    val summary: String,
    val documentRef: String,
    val verifiedBy: String
)

data class AuditDecision(
    val id: String,
    val year: Int,
    val findingId: String,
    val controlId: String,
    val title: String,
    val decisionType: String, // "Risk Acceptance", "Postponement", "Compensating Control"
    val decisionMaker: String, // e.g. "CISO & Risk Committee"
    val rationale: String,
    val compensatingControl: String,
    val promisedRemediation: String,
    val targetDeadline: String,
    val currentRealStatus: String
)

data class HindsightMemory(
    val id: String,
    val bankId: String = "novabank-compliance",
    val text: String,
    val tags: List<String>,
    val timestamp: Long,
    val temporalYear: Int,
    val entities: List<String>,
    val memoryType: MemoryType,
    val confidence: Float = 0.95f,
    val findingId: String? = null,
    val controlId: String? = null
)

data class MemoryEvidenceNode(
    val year: Int,
    val stepTitle: String,
    val description: String,
    val entityOrOwner: String,
    val type: String, // "FINDING", "DECISION", "REMEDIATION", "RECURRENCE"
    val isAlert: Boolean = false
)

data class HindsightReflection(
    val question: String,
    val answer: String,
    val recurringPatternDetected: Boolean,
    val rootCauseSummary: String,
    val memoriesUsedCount: Int,
    val confidence: String,
    val memorySources: List<HindsightMemory>,
    val evidenceChain: List<MemoryEvidenceNode>,
    val recommendations: List<String>
)

data class InvestigationPlanItem(
    val priority: String, // "CRITICAL", "HIGH", "MEDIUM"
    val category: String,
    val controlCode: String,
    val task: String,
    val historicalBasis: String,
    val requestedEvidence: String,
    val targetTeam: String
)

data class PolicyImpactAnalysis(
    val policyTitle: String,
    val changeSummary: String,
    val affectedControlsCount: Int,
    val affectedHistoricalFindingsCount: Int,
    val affectedControls: List<ComplianceControl>,
    val affectedFindings: List<AuditFinding>,
    val impactAssessment: String,
    val actionItems: List<String>,
    val recalledMemories: List<HindsightMemory> = emptyList(),
    val reflectionInsight: String? = null
)

data class EvidenceCausalLink(
    val fromNodeTitle: String,
    val fromNodeType: String, // "FINDING", "DECISION", "REMEDIATION"
    val toNodeTitle: String,
    val toNodeType: String,
    val relationshipDescription: String,
    val yearSpan: String
)

data class DecisionReasoningStep(
    val stepIndex: Int,
    val phase: String,
    val actor: String,
    val year: Int,
    val description: String,
    val factClassification: String, // "HISTORICAL FACT (VERIFIED)" vs "EXECUTIVE ASSUMPTION (FAILED)"
    val isCriticalAlert: Boolean = false
)

data class DecisionReasoningChain(
    val decision: AuditDecision,
    val targetFinding: AuditFinding?,
    val targetControl: ComplianceControl?,
    val remediation: RemediationCommitment?,
    val reasoningSteps: List<DecisionReasoningStep>,
    val institutionalMemoriesUsed: List<HindsightMemory>,
    val executiveSummary: String,
    val rootCauseHypothesis: String,
    val compensatingControlStatus: String,
    val regulatoryVerdict: String
)

