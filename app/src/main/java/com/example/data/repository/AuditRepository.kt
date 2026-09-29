package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuditRepository(private val dao: AuditDao) {

    val allFindings: Flow<List<AuditFinding>> = dao.getAllFindings().map { list ->
        list.map { it.toDomain() }
    }

    val recurringFindings: Flow<List<AuditFinding>> = dao.getRecurringFindings().map { list ->
        list.map { it.toDomain() }
    }

    val allControls: Flow<List<ComplianceControl>> = dao.getAllControls().map { list ->
        list.map { it.toDomain() }
    }

    val allRemediations: Flow<List<RemediationCommitment>> = dao.getAllRemediations().map { list ->
        list.map { it.toDomain() }
    }

    val overdueRemediations: Flow<List<RemediationCommitment>> = dao.getOverdueRemediations().map { list ->
        list.map { it.toDomain() }
    }

    val allEvidence: Flow<List<EvidenceRecord>> = dao.getAllEvidence().map { list ->
        list.map { it.toDomain() }
    }

    val allDecisions: Flow<List<AuditDecision>> = dao.getAllDecisions().map { list ->
        list.map { it.toDomain() }
    }

    val allMemories: Flow<List<HindsightMemory>> = dao.getAllMemories().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun getFindingById(id: String): AuditFinding? = dao.getFindingById(id)?.toDomain()

    suspend fun getControlById(id: String): ComplianceControl? = dao.getControlById(id)?.toDomain()

    suspend fun getFindingsForControl(controlId: String): List<AuditFinding> =
        dao.getFindingsForControl(controlId).map { it.toDomain() }

    suspend fun getRemediationsForControl(controlId: String): List<RemediationCommitment> =
        dao.getRemediationsForControl(controlId).map { it.toDomain() }

    suspend fun getDecisionsForFindingOrControl(findingId: String?, controlId: String?): List<AuditDecision> =
        dao.getDecisionsForFindingOrControl(findingId, controlId).map { it.toDomain() }

    suspend fun getEvidenceForFindingOrControl(findingId: String?, controlId: String?): List<EvidenceRecord> =
        dao.getEvidenceForFindingOrControl(findingId, controlId).map { it.toDomain() }

    suspend fun recallMemoriesByKeyword(query: String): List<HindsightMemory> =
        dao.recallMemoriesByKeyword(query).map { it.toDomain() }

    suspend fun recallMemoriesByYear(year: Int): List<HindsightMemory> =
        dao.recallMemoriesByYear(year).map { it.toDomain() }

    suspend fun recallMemoriesByTarget(controlId: String?, findingId: String?): List<HindsightMemory> =
        dao.recallMemoriesByTarget(controlId, findingId).map { it.toDomain() }

    suspend fun insertMemories(memories: List<HindsightMemory>) {
        dao.insertMemories(memories.map { it.toEntity() })
    }

    suspend fun seedDatabase(
        findings: List<AuditFinding>,
        controls: List<ComplianceControl>,
        remediations: List<RemediationCommitment>,
        evidence: List<EvidenceRecord>,
        decisions: List<AuditDecision>,
        memories: List<HindsightMemory>
    ) {
        dao.clearFindings()
        dao.clearControls()
        dao.clearRemediations()
        dao.clearEvidence()
        dao.clearDecisions()
        dao.clearMemories()

        dao.insertFindings(findings.map { it.toEntity() })
        dao.insertControls(controls.map { it.toEntity() })
        dao.insertRemediations(remediations.map { it.toEntity() })
        dao.insertEvidence(evidence.map { it.toEntity() })
        dao.insertDecisions(decisions.map { it.toEntity() })
        dao.insertMemories(memories.map { it.toEntity() })
    }
}

// Extension converters
fun FindingEntity.toDomain() = AuditFinding(
    id = id,
    auditYear = auditYear,
    auditTitle = auditTitle,
    title = title,
    description = description,
    category = category,
    severity = try { Severity.valueOf(severity) } catch (e: Exception) { Severity.HIGH },
    controlId = controlId,
    controlName = controlName,
    owner = owner,
    riskAcceptanceStatus = try { RiskAcceptanceStatus.valueOf(riskAcceptanceStatus) } catch (e: Exception) { RiskAcceptanceStatus.NONE },
    riskAcceptanceReason = riskAcceptanceReason,
    compensatingControl = compensatingControl,
    remediationId = remediationId,
    recurringFromId = recurringFromId,
    isRecurring = isRecurring,
    recurrenceCount = recurrenceCount,
    status = try { FindingStatus.valueOf(status) } catch (e: Exception) { FindingStatus.OPEN },
    evidenceSummary = evidenceSummary
)

fun AuditFinding.toEntity() = FindingEntity(
    id = id,
    auditYear = auditYear,
    auditTitle = auditTitle,
    title = title,
    description = description,
    category = category,
    severity = severity.name,
    controlId = controlId,
    controlName = controlName,
    owner = owner,
    riskAcceptanceStatus = riskAcceptanceStatus.name,
    riskAcceptanceReason = riskAcceptanceReason,
    compensatingControl = compensatingControl,
    remediationId = remediationId,
    recurringFromId = recurringFromId,
    isRecurring = isRecurring,
    recurrenceCount = recurrenceCount,
    status = status.name,
    evidenceSummary = evidenceSummary
)

fun ControlEntity.toDomain() = ComplianceControl(
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
    healthStatus = try { ControlHealth.valueOf(healthStatus) } catch (e: Exception) { ControlHealth.PARTIAL },
    totalFindingsCount = totalFindingsCount
)

fun ComplianceControl.toEntity() = ControlEntity(
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
    healthStatus = healthStatus.name,
    totalFindingsCount = totalFindingsCount
)

fun RemediationEntity.toDomain() = RemediationCommitment(
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
    status = try { RemediationStatus.valueOf(status) } catch (e: Exception) { RemediationStatus.INCOMPLETE },
    verificationNotes = verificationNotes
)

fun RemediationCommitment.toEntity() = RemediationEntity(
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
    status = status.name,
    verificationNotes = verificationNotes
)

fun EvidenceEntity.toDomain() = EvidenceRecord(
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

fun EvidenceRecord.toEntity() = EvidenceEntity(
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

fun DecisionEntity.toDomain() = AuditDecision(
    id = id,
    year = year,
    findingId = findingId,
    controlId = controlId,
    title = title,
    decisionType = decisionType,
    decisionMaker = decisionMaker,
    rationale = rationale,
    compensatingControl = compensatingControl,
    promisedRemediation = promisedRemediation,
    targetDeadline = targetDeadline,
    currentRealStatus = currentRealStatus
)

fun AuditDecision.toEntity() = DecisionEntity(
    id = id,
    year = year,
    findingId = findingId,
    controlId = controlId,
    title = title,
    decisionType = decisionType,
    decisionMaker = decisionMaker,
    rationale = rationale,
    compensatingControl = compensatingControl,
    promisedRemediation = promisedRemediation,
    targetDeadline = targetDeadline,
    currentRealStatus = currentRealStatus
)

fun MemoryEntity.toDomain() = HindsightMemory(
    id = id,
    bankId = bankId,
    text = text,
    tags = tags,
    timestamp = timestamp,
    temporalYear = temporalYear,
    entities = entities,
    memoryType = try { MemoryType.valueOf(memoryType) } catch (e: Exception) { MemoryType.OBSERVATION },
    confidence = confidence,
    findingId = findingId,
    controlId = controlId
)

fun HindsightMemory.toEntity() = MemoryEntity(
    id = id,
    bankId = bankId,
    text = text,
    tags = tags,
    timestamp = timestamp,
    temporalYear = temporalYear,
    entities = entities,
    memoryType = memoryType.name,
    confidence = confidence,
    findingId = findingId,
    controlId = controlId
)
