package com.example.data.model

/**
 * AuditMind Data Entities
 * Aligns with the NovaBank multi-year compliance & audit structure.
 */

data class Finding(
    val id: String, // e.g. "F-104", "F-204", "F-301"
    val auditYear: Int,
    val auditTitle: String,
    val title: String,
    val description: String,
    val category: String, // e.g. "Access Control", "Vendor Security", "Data Retention"
    val severity: Severity,
    val controlId: String,
    val controlName: String,
    val owner: String,
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

data class Control(
    val id: String, // e.g. "C-17", "C-03", "C-05"
    val code: String, // e.g. "AC-02.1"
    val name: String,
    val department: String,
    val policyRef: String,
    val frequency: String,
    val description: String,
    val lastAuditedYear: Int,
    val failureHistoryYears: List<Int>,
    val recurrenceRiskLevel: String,
    val healthStatus: ControlHealth,
    val totalFindingsCount: Int = 0
)

data class Remediation(
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

data class Evidence(
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

data class Decision(
    val id: String,
    val year: Int,
    val findingId: String,
    val controlId: String,
    val title: String,
    val decisionType: String,
    val decisionMaker: String,
    val rationale: String,
    val compensatingControl: String,
    val promisedRemediation: String,
    val targetDeadline: String,
    val currentRealStatus: String
)

data class PolicyDocument(
    val id: String,
    val title: String,
    val code: String,
    val version: String,
    val department: String,
    val effectiveDate: String,
    val rawText: String,
    val keyRequirements: List<String>,
    val uploadTimestamp: Long = System.currentTimeMillis()
)

// Extension converters to maintain full compatibility across repository & UI
fun AuditFinding.toFinding() = Finding(
    id = id,
    auditYear = auditYear,
    auditTitle = auditTitle,
    title = title,
    description = description,
    category = category,
    severity = severity,
    controlId = controlId,
    controlName = controlName,
    owner = owner,
    riskAcceptanceStatus = riskAcceptanceStatus,
    riskAcceptanceReason = riskAcceptanceReason,
    compensatingControl = compensatingControl,
    remediationId = remediationId,
    recurringFromId = recurringFromId,
    isRecurring = isRecurring,
    recurrenceCount = recurrenceCount,
    status = status,
    evidenceSummary = evidenceSummary
)

fun ComplianceControl.toControl() = Control(
    id = id,
    code = code,
    name = name,
    department = department,
    policyRef = policyRef,
    frequency = frequency,
    description = description,
    lastAuditedYear = lastAuditedYear,
    failureHistoryYears = failureHistoryYears,
    recurrenceRiskLevel = recurrenceRiskLevel,
    healthStatus = healthStatus,
    totalFindingsCount = totalFindingsCount
)

fun RemediationCommitment.toRemediation() = Remediation(
    id = id,
    findingId = findingId,
    controlId = controlId,
    title = title,
    description = description,
    owner = owner,
    committedDate = committedDate,
    deadline = deadline,
    isOverdue = isOverdue,
    completionPercentage = completionPercentage,
    status = status,
    verificationNotes = verificationNotes
)

fun EvidenceRecord.toEvidence() = Evidence(
    id = id,
    findingId = findingId,
    controlId = controlId,
    title = title,
    type = type,
    year = year,
    summary = summary,
    documentRef = documentRef,
    verifiedBy = verifiedBy
)
