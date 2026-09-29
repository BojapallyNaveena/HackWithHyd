package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "findings")
data class FindingEntity(
    @PrimaryKey val id: String,
    val auditYear: Int,
    val auditTitle: String,
    val title: String,
    val description: String,
    val category: String,
    val severity: String,
    val controlId: String,
    val controlName: String,
    val owner: String,
    val riskAcceptanceStatus: String,
    val riskAcceptanceReason: String? = null,
    val compensatingControl: String? = null,
    val remediationId: String? = null,
    val recurringFromId: String? = null,
    val isRecurring: Boolean = false,
    val recurrenceCount: Int = 0,
    val status: String,
    val evidenceSummary: String? = null
)

@Entity(tableName = "controls")
data class ControlEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val department: String,
    val policyRef: String,
    val frequency: String,
    val description: String,
    val lastAuditedYear: Int,
    val failureHistoryYears: List<Int>,
    val recurrenceRiskLevel: String,
    val healthStatus: String,
    val totalFindingsCount: Int = 0
)

@Entity(tableName = "remediations")
data class RemediationEntity(
    @PrimaryKey val id: String,
    val findingId: String,
    val controlId: String,
    val title: String,
    val description: String,
    val owner: String,
    val committedDate: String,
    val deadline: String,
    val isOverdue: Boolean,
    val completionPercentage: Int,
    val status: String,
    val verificationNotes: String? = null
)

@Entity(tableName = "evidence")
data class EvidenceEntity(
    @PrimaryKey val id: String,
    val findingId: String?,
    val controlId: String?,
    val title: String,
    val type: String,
    val year: Int,
    val summary: String,
    val documentRef: String,
    val verifiedBy: String
)

@Entity(tableName = "decisions")
data class DecisionEntity(
    @PrimaryKey val id: String,
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

@Entity(tableName = "hindsight_memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val bankId: String = "novabank-compliance",
    val text: String,
    val tags: List<String>,
    val timestamp: Long,
    val temporalYear: Int,
    val entities: List<String>,
    val memoryType: String,
    val confidence: Float = 0.95f,
    val findingId: String? = null,
    val controlId: String? = null
)
