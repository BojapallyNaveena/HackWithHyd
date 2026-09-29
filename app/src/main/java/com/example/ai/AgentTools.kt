package com.example.ai

import com.example.data.model.*
import com.example.data.repository.AuditRepository
import com.example.hindsight.HindsightService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

data class AgentToolParameter(
    val name: String,
    val type: String,
    val description: String,
    val required: Boolean = false
)

data class AgentToolDefinition(
    val name: String,
    val description: String,
    val parameters: List<AgentToolParameter>
)

/**
 * Core agent tools enabling the LLM to perform autonomous research into NovaBank's
 * 3-year audit history, controls, remediations, recurring deficits, and audit deltas.
 */
class AgentTools(
    private val repository: AuditRepository,
    private val hindsight: HindsightService
) {

    // =========================================================================
    // Core Tool 1: search_audit_history
    // =========================================================================

    /**
     * Searches NovaBank audit history across 2024, 2025, and 2026 findings,
     * controls, and institutional memories by keyword, audit year, category, or severity.
     */
    suspend fun search_audit_history(
        query: String = "",
        year: Int? = null,
        category: String? = null,
        severity: String? = null,
        status: String? = null
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val allFindings = repository.allFindings.first()
        val filtered = allFindings.filter { finding ->
            val matchesYear = year == null || finding.auditYear == year
            val matchesCategory = category.isNullOrBlank() || finding.category.contains(category, ignoreCase = true)
            val matchesSeverity = severity.isNullOrBlank() || finding.severity.name.equals(severity, ignoreCase = true)
            val matchesStatus = status.isNullOrBlank() || finding.status.name.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    finding.title.contains(query, ignoreCase = true) ||
                    finding.description.contains(query, ignoreCase = true) ||
                    finding.controlName.contains(query, ignoreCase = true) ||
                    finding.id.contains(query, ignoreCase = true) ||
                    finding.controlId.contains(query, ignoreCase = true)
            matchesYear && matchesCategory && matchesSeverity && matchesStatus && matchesQuery
        }

        val recalledMemories = if (query.isNotBlank()) {
            hindsight.recall_memory(query = query, temporalYear = year, limit = 4)
        } else {
            emptyList()
        }

        mapOf(
            "query" to query,
            "filterYear" to (year ?: "All Years (2024-2026)"),
            "filterCategory" to (category ?: "All Categories"),
            "matchCount" to filtered.size,
            "findings" to filtered,
            "recalledMemories" to recalledMemories
        )
    }

    suspend fun searchAuditHistory(
        query: String = "",
        year: Int? = null,
        category: String? = null
    ): List<AuditFinding> {
        val result = search_audit_history(query = query, year = year, category = category)
        @Suppress("UNCHECKED_CAST")
        return result["findings"] as? List<AuditFinding> ?: emptyList()
    }

    // =========================================================================
    // Core Tool 2: get_control_history
    // =========================================================================

    /**
     * Retrieves the complete multi-year audit history for a specific control across 2024-2026,
     * including associated findings, failure frequency, remediation commitments,
     * management decisions/waivers, and verified evidence records.
     */
    suspend fun get_control_history(controlId: String): Map<String, Any> = withContext(Dispatchers.IO) {
        val normalizedId = if (controlId.startsWith("C-", ignoreCase = true)) {
            controlId.uppercase()
        } else {
            "C-$controlId"
        }

        val control = repository.getControlById(normalizedId)
            ?: repository.getFindingsForControl(normalizedId).firstOrNull()?.let {
                ComplianceControl(
                    id = normalizedId,
                    code = "AC-REF",
                    name = it.controlName,
                    department = "IT Security",
                    policyRef = "POL-SEC",
                    frequency = "Continuous",
                    description = "Control reference for $normalizedId",
                    lastAuditedYear = 2026,
                    failureHistoryYears = listOf(2024, 2025, 2026),
                    recurrenceRiskLevel = "HIGH",
                    healthStatus = ControlHealth.CRITICAL_RECURRING
                )
            }

        val findings = repository.getFindingsForControl(normalizedId)
        val remediations = repository.getRemediationsForControl(normalizedId)
        val decisions = repository.getDecisionsForFindingOrControl(null, normalizedId)
        val evidence = repository.getEvidenceForFindingOrControl(null, normalizedId)
        val evidenceTimeline = hindsight.getMemoryEvidenceForControl(normalizedId)

        mapOf(
            "controlId" to normalizedId,
            "controlName" to (control?.name ?: "Unknown Control"),
            "healthStatus" to (control?.healthStatus?.name ?: "UNKNOWN"),
            "recurrenceRiskLevel" to (control?.recurrenceRiskLevel ?: "UNKNOWN"),
            "failureYears" to (control?.failureHistoryYears ?: findings.map { it.auditYear }.distinct()),
            "totalFindingsCount" to findings.size,
            "findings" to findings,
            "remediationsCount" to remediations.size,
            "remediations" to remediations,
            "decisionsCount" to decisions.size,
            "decisions" to decisions,
            "evidenceCount" to evidence.size,
            "evidence" to evidence,
            "evidenceTimeline" to evidenceTimeline
        )
    }

    suspend fun getControlHistory(controlId: String): Map<String, Any> = get_control_history(controlId)

    // =========================================================================
    // Core Tool 3: get_remediation_status
    // =========================================================================

    /**
     * Retrieves remediation commitments and tracking statuses for audit findings,
     * highlighting overdue items, completion percentages, assigned owners, and root causes of delays.
     */
    suspend fun get_remediation_status(
        controlId: String? = null,
        isOverdueOnly: Boolean = false
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val allRemediations = repository.allRemediations.first()
        val filtered = allRemediations.filter { rem ->
            val matchesControl = controlId.isNullOrBlank() || rem.controlId.equals(controlId, ignoreCase = true)
            val matchesOverdue = !isOverdueOnly || rem.isOverdue
            matchesControl && matchesOverdue
        }

        val overdueCount = filtered.count { it.isOverdue }
        val avgCompletion = if (filtered.isNotEmpty()) {
            filtered.map { it.completionPercentage }.average().toInt()
        } else 0

        mapOf(
            "totalRemediations" to filtered.size,
            "overdueCount" to overdueCount,
            "isOverdueFilter" to isOverdueOnly,
            "targetControl" to (controlId ?: "All Controls"),
            "averageCompletionPercentage" to "$avgCompletion%",
            "remediations" to filtered,
            "overdueSummary" to filtered.filter { it.isOverdue }.map { rem ->
                mapOf(
                    "id" to rem.id,
                    "title" to rem.title,
                    "owner" to rem.owner,
                    "deadline" to rem.deadline,
                    "completion" to "${rem.completionPercentage}%",
                    "blockers" to (rem.verificationNotes ?: "Remediation missed committed deadline")
                )
            }
        )
    }

    suspend fun getRemediationStatus(isOverdueOnly: Boolean = false): List<RemediationCommitment> {
        val res = get_remediation_status(isOverdueOnly = isOverdueOnly)
        @Suppress("UNCHECKED_CAST")
        return res["remediations"] as? List<RemediationCommitment> ?: emptyList()
    }

    // =========================================================================
    // Core Tool 4: find_recurring_findings
    // =========================================================================

    /**
     * Scans the 3-year audit history and returns all findings that have persisted
     * or recurred across consecutive audit cycles (2024, 2025, 2026), including
     * recurrence counts, predecessor finding references, and systemic root causes.
     */
    suspend fun find_recurring_findings(): Map<String, Any> = withContext(Dispatchers.IO) {
        val recurring = repository.recurringFindings.first()
        val allFindings = repository.allFindings.first()

        val groupedByControl = recurring.groupBy { it.controlId }
        val recurrenceClusters = groupedByControl.map { (ctrlId, findingsList) ->
            val control = repository.getControlById(ctrlId)
            val decisions = repository.getDecisionsForFindingOrControl(null, ctrlId)
            val allControlFindings = allFindings.filter { it.controlId == ctrlId }

            mapOf(
                "controlId" to ctrlId,
                "controlName" to (control?.name ?: findingsList.firstOrNull()?.controlName ?: "Control $ctrlId"),
                "severity" to (findingsList.maxByOrNull { it.severity.ordinal }?.severity?.name ?: "HIGH"),
                "totalOccurrences" to allControlFindings.size,
                "auditYears" to allControlFindings.map { it.auditYear }.distinct().sorted(),
                "findings" to allControlFindings.map { f ->
                    mapOf(
                        "id" to f.id,
                        "year" to f.auditYear,
                        "title" to f.title,
                        "isRecurring" to f.isRecurring,
                        "recurringFromId" to (f.recurringFromId ?: "Initial Finding")
                    )
                },
                "decisionsGranted" to decisions.map { it.title },
                "currentStatus" to (decisions.firstOrNull()?.currentRealStatus ?: "Recurring audit deficiency")
            )
        }

        mapOf(
            "totalRecurringFindingsCount" to recurring.size,
            "systemicClustersCount" to recurrenceClusters.size,
            "recurringFindings" to recurring,
            "clusters" to recurrenceClusters
        )
    }

    suspend fun findRecurringFindings(): List<AuditFinding> {
        return repository.recurringFindings.first()
    }

    // =========================================================================
    // Core Tool 5: compare_audits
    // =========================================================================

    /**
     * Compares two specific audit years (e.g. 2024 vs 2025, or 2025 vs 2026) to analyze
     * delta trajectory, net new vs recurring findings, resolved issues, and risk trends.
     */
    suspend fun compare_audits(year1: Int, year2: Int): Map<String, Any> = withContext(Dispatchers.IO) {
        val all = repository.allFindings.first()
        val findingsY1 = all.filter { it.auditYear == year1 }
        val findingsY2 = all.filter { it.auditYear == year2 }

        val recurringInY2 = findingsY2.filter { it.isRecurring }
        val newInY2 = findingsY2.filter { !it.isRecurring }

        val highRiskY1 = findingsY1.count { it.severity == Severity.HIGH || it.severity == Severity.CRITICAL }
        val highRiskY2 = findingsY2.count { it.severity == Severity.HIGH || it.severity == Severity.CRITICAL }

        // Find controls with issues in Y1 that were resolved in Y2
        val y1ControlIds = findingsY1.map { it.controlId }.toSet()
        val y2ControlIds = findingsY2.map { it.controlId }.toSet()
        val resolvedControlIds = y1ControlIds - y2ControlIds
        val resolvedControls = resolvedControlIds.mapNotNull { repository.getControlById(it)?.name ?: it }

        val trajectory = when {
            recurringInY2.size > 2 -> "DETERIORATING_RECURRENCE: Multiple systemic issues carried over without remediation."
            findingsY2.size < findingsY1.size -> "IMPROVING: Overall finding volume decreased, though recurring root causes persist."
            else -> "STABLE_DEFICIT: Compliance posture unchanged across comparative cycles."
        }

        mapOf(
            "year1" to year1,
            "year2" to year2,
            "findingsCountYear1" to findingsY1.size,
            "findingsCountYear2" to findingsY2.size,
            "recurringInYear2Count" to recurringInY2.size,
            "newInYear2Count" to newInY2.size,
            "resolvedInYear2Count" to resolvedControls.size,
            "resolvedControls" to resolvedControls,
            "highRiskCountYear1" to highRiskY1,
            "highRiskCountYear2" to highRiskY2,
            "riskTrajectory" to trajectory,
            "recurringFindings" to recurringInY2,
            "newFindings" to newInY2
        )
    }

    suspend fun compareAudits(year1: Int, year2: Int): Map<String, Any> = compare_audits(year1, year2)

    // =========================================================================
    // Autonomous Execution Dispatcher & Tool Declarations
    // =========================================================================

    /**
     * Executes any tool by name with arguments, enabling the LLM agent to invoke tools dynamically.
     */
    suspend fun executeTool(toolName: String, arguments: Map<String, Any?> = emptyMap()): Map<String, Any> {
        return when (toolName.lowercase()) {
            "search_audit_history", "searchaudithistory" -> {
                val query = arguments["query"]?.toString() ?: ""
                val year = arguments["year"]?.toString()?.toIntOrNull()
                val category = arguments["category"]?.toString()
                val severity = arguments["severity"]?.toString()
                val status = arguments["status"]?.toString()
                search_audit_history(query, year, category, severity, status)
            }
            "get_control_history", "getcontrolhistory" -> {
                val controlId = arguments["controlId"]?.toString()
                    ?: arguments["control_id"]?.toString()
                    ?: "C-17"
                get_control_history(controlId)
            }
            "get_remediation_status", "getremediationstatus" -> {
                val controlId = arguments["controlId"]?.toString() ?: arguments["control_id"]?.toString()
                val isOverdueOnly = arguments["isOverdueOnly"]?.toString()?.toBooleanStrictOrNull()
                    ?: arguments["is_overdue_only"]?.toString()?.toBooleanStrictOrNull()
                    ?: false
                get_remediation_status(controlId, isOverdueOnly)
            }
            "find_recurring_findings", "findrecurringfindings" -> {
                find_recurring_findings()
            }
            "compare_audits", "compareaudits" -> {
                val year1 = arguments["year1"]?.toString()?.toIntOrNull() ?: 2025
                val year2 = arguments["year2"]?.toString()?.toIntOrNull() ?: 2026
                compare_audits(year1, year2)
            }
            else -> mapOf(
                "error" to "Unknown tool '$toolName'",
                "availableTools" to listOf(
                    "search_audit_history",
                    "get_control_history",
                    "get_remediation_status",
                    "find_recurring_findings",
                    "compare_audits"
                )
            )
        }
    }

    /**
     * Returns tool declarations schema for LLM function calling.
     */
    fun getToolDeclarations(): List<AgentToolDefinition> {
        return listOf(
            AgentToolDefinition(
                name = "search_audit_history",
                description = "Searches historical audit findings across 2024, 2025, and 2026 by query, audit year, category, or severity.",
                parameters = listOf(
                    AgentToolParameter("query", "string", "Keywords such as 'bastion', 'MFA', 'vendor', 'retention', 'contractor'"),
                    AgentToolParameter("year", "integer", "Audit year: 2024, 2025, or 2026"),
                    AgentToolParameter("category", "string", "Category: Access Control, Vendor Security, Data Retention, etc."),
                    AgentToolParameter("severity", "string", "CRITICAL, HIGH, MEDIUM, or LOW")
                )
            ),
            AgentToolDefinition(
                name = "get_control_history",
                description = "Retrieves the full multi-year history of a compliance control across 2024-2026, including linked findings, failure years, remediations, and executive decisions.",
                parameters = listOf(
                    AgentToolParameter("controlId", "string", "Control ID e.g. 'C-17', 'C-03', 'C-05', 'C-09'", required = true)
                )
            ),
            AgentToolDefinition(
                name = "get_remediation_status",
                description = "Retrieves remediation commitments, completion progress percentages, deadlines, and overdue alerts for controls.",
                parameters = listOf(
                    AgentToolParameter("controlId", "string", "Optional control ID to filter"),
                    AgentToolParameter("isOverdueOnly", "boolean", "Set to true to return only overdue remediation commitments")
                )
            ),
            AgentToolDefinition(
                name = "find_recurring_findings",
                description = "Identifies all findings that have persisted or recurred across multiple audit cycles (2024, 2025, 2026), with recurrence counts and root causes.",
                parameters = emptyList()
            ),
            AgentToolDefinition(
                name = "compare_audits",
                description = "Compares two specific audit years (e.g. 2024 vs 2025, or 2025 vs 2026) to analyze findings delta, recurring items, and risk trajectory.",
                parameters = listOf(
                    AgentToolParameter("year1", "integer", "First audit year e.g. 2025", required = true),
                    AgentToolParameter("year2", "integer", "Second audit year e.g. 2026", required = true)
                )
            )
        )
    }

    // =========================================================================
    // Supporting Policy & Prep Methods
    // =========================================================================

    suspend fun analyzePolicyChange(
        oldRequirement: String,
        newRequirement: String
    ): PolicyImpactAnalysis {
        val controls = repository.allControls.first()
        val findings = repository.allFindings.first()

        val query = "$oldRequirement $newRequirement".lowercase()

        val affectedControls = controls.filter { control ->
            when {
                query.contains("access") || query.contains("recertification") || query.contains("review") ->
                    control.id in listOf("C-03", "C-17")
                query.contains("vendor") || query.contains("third-party") || query.contains("soc") ->
                    control.id == "C-05"
                query.contains("retention") || query.contains("purge") || query.contains("pii") ->
                    control.id == "C-09"
                query.contains("key") || query.contains("rotation") || query.contains("crypto") ->
                    control.id == "C-11"
                else -> control.name.lowercase().split(" ").any { word -> word.length > 4 && query.contains(word) }
            }
        }

        val affectedControlIds = affectedControls.map { it.id }
        val affectedFindings = findings.filter { it.controlId in affectedControlIds }

        val recalled = hindsight.recall_memory(
            query = "$oldRequirement $newRequirement",
            limit = 6
        )

        val targetControl = affectedControls.firstOrNull()?.id ?: "C-17"
        val reflection = hindsight.reflect_memory(
            question = "What is the historical impact and risk of updating policy to: $newRequirement?",
            targetControlId = targetControl
        )

        val summary = "Updating requirement from '$oldRequirement' to '$newRequirement' directly impacts ${affectedControls.size} primary controls and intersects with ${affectedFindings.size} historical findings across 2024-2026."

        val actionItems = listOf(
            "Update control test procedures in POL-IAM-02 and notify responsible control owners.",
            "Recalibrate audit sampling frequency to align with shorter review windows.",
            "Assess backlog of past findings under the tightened criteria to prevent immediate audit non-compliance.",
            "Schedule preliminary walkthrough with Internal Audit team prior to formal fieldwork."
        )

        return PolicyImpactAnalysis(
            policyTitle = "Policy Requirement Update Analysis",
            changeSummary = summary,
            affectedControlsCount = affectedControls.size,
            affectedHistoricalFindingsCount = affectedFindings.size,
            affectedControls = affectedControls,
            affectedFindings = affectedFindings,
            impactAssessment = "HIGH IMPACT: Accelerating review cadence will immediately flag existing manual processes that already struggle to meet the current threshold. Automation is urgently required.",
            actionItems = actionItems,
            recalledMemories = recalled,
            reflectionInsight = reflection.answer
        )
    }

    suspend fun generateEvidenceRequest(targetId: String): List<String> {
        val evidenceRecords = repository.getEvidenceForFindingOrControl(targetId, targetId)
        val defaultChecklist = listOf(
            "Current live system configuration export demonstrating control enforcement.",
            "Historical log extract covering the past 90 consecutive operating days.",
            "Sign-off evidence and ticket timestamps for the most recent execution cycle.",
            "Verification of secondary reviewer (four-eyes principle) approval where applicable."
        )
        return if (evidenceRecords.isNotEmpty()) {
            evidenceRecords.map { "Document Ref: ${it.documentRef} (${it.type}) - Verified by ${it.verifiedBy}" } + defaultChecklist
        } else {
            defaultChecklist
        }
    }

    suspend fun createAuditSummary(year: Int): String {
        val findings = repository.allFindings.first().filter { it.auditYear == year }
        val criticalCount = findings.count { it.severity == Severity.CRITICAL }
        val highCount = findings.count { it.severity == Severity.HIGH }
        val recurringCount = findings.count { it.isRecurring }

        return """
            NovaBank $year Audit Summary:
            - Total Findings: ${findings.size}
            - Critical Severity: $criticalCount
            - High Severity: $highCount
            - Recurring Findings: $recurringCount
            - Primary High-Risk Areas: Access Control (C-17, C-03), Vendor Risk (C-05), Data Retention (C-09)
        """.trimIndent()
    }

    /**
     * Synthesizes audit findings, overdue items, and recent policy changes into an actionable investigation checklist
     * using the agent's core research tools (find_recurring_findings, get_remediation_status, search_audit_history, get_control_history).
     */
    suspend fun synthesizeInvestigationChecklist(
        recentPolicyContext: String = "Stricter quarterly access reviews, zero-day vendor SOC 2 enforcement, and mandatory automated PII retention purges"
    ): List<InvestigationPlanItem> = withContext(Dispatchers.IO) {
        // 1. Research recurring findings
        val recurringData = find_recurring_findings()
        @Suppress("UNCHECKED_CAST")
        val recurringFindings = recurringData["recurringFindings"] as? List<AuditFinding> ?: emptyList()

        // 2. Research overdue remediations
        val overdueData = get_remediation_status(isOverdueOnly = true)
        @Suppress("UNCHECKED_CAST")
        val overdueList = overdueData["remediations"] as? List<RemediationCommitment> ?: emptyList()

        // 3. Research policy impact with Hindsight reflection
        val policyAnalysis = analyzePolicyChange(
            oldRequirement = "Annual risk review and manual compensating reviews",
            newRequirement = recentPolicyContext
        )

        val checklistItems = mutableListOf<InvestigationPlanItem>()

        // Synthesize Item 1: Privileged Access (C-17)
        val c17Finding = recurringFindings.firstOrNull { it.controlId == "C-17" }
        val c17Remediation = overdueList.firstOrNull { it.controlId == "C-17" }
        checklistItems.add(
            InvestigationPlanItem(
                priority = "CRITICAL",
                category = "Privileged Access Management",
                controlCode = "C-17 (AC-02.1)",
                task = "Audit Oracle Core Banking Bastion (10.200.4.12) for hardware MFA bypasses and inspect auth.log for unauthenticated root logins.",
                historicalBasis = "Failed in 2024 (F-104), 2025 (F-204), and 2026 (F-301). Risk waiver DEC-2024-01 expired. CyberArk PAM remediation (${c17Remediation?.title ?: "R-22"}) stalled at 35%. Policy update mandates hardware tokens.",
                requestedEvidence = "Live terminal physical MFA challenge demo + extract of /var/log/secure & auth.log for the past 14 operating days.",
                targetTeam = "IT Security & Architecture (Marcus Vance - CISO)"
            )
        )

        // Synthesize Item 2: User Access & Contractor Recertification (C-03)
        val c03Finding = recurringFindings.firstOrNull { it.controlId == "C-03" }
        val c03Remediation = overdueList.firstOrNull { it.controlId == "C-03" }
        checklistItems.add(
            InvestigationPlanItem(
                priority = "CRITICAL",
                category = "Identity & Access Governance",
                controlCode = "C-03 (AC-06.3)",
                task = "Reconcile Active Directory VPN security groups against Workday HR offboarding records to identify orphaned contractor accounts.",
                historicalBasis = "Failed 3 consecutive audit cycles. 35 departed contractors retain active VPN access > 60 days post-termination. Automated SCIM remediation (${c03Remediation?.title ?: "R-15"}) is overdue. Policy changes require automated 24-hr deprovisioning.",
                requestedEvidence = "LDAP query export of CN=VPN-Users + Workday offboarding delta report (last 90 days) + Okta deactivation logs.",
                targetTeam = "Identity & Access Management (Sarah Lin)"
            )
        )

        // Synthesize Item 3: Vendor Risk Assessment & SOC 2 (C-05)
        val c05Finding = recurringFindings.firstOrNull { it.controlId == "C-05" }
        checklistItems.add(
            InvestigationPlanItem(
                priority = "HIGH",
                category = "Third-Party Vendor Governance",
                controlCode = "C-05 (VR-01.2)",
                task = "Inspect independent SOC 2 Type II audit reports and bridge letters for all 19 Tier-1 SaaS payment and credit processors.",
                historicalBasis = "Grew from 8 unreviewed vendors (2024) to 14 (2025) and 19 (2026). Vendor risk portal canceled; temporary waiver #PW-2025-09 expired without replacement. Intersects with new third-party policy mandates.",
                requestedEvidence = "Vendor Risk Register, signed vendor SOC 2 Type II packages, bridge letters, and annual risk tier re-evaluation forms.",
                targetTeam = "Third-Party Risk Management (David Ross)"
            )
        )

        // Synthesize Item 4: Data Retention & GDPR/GLBA PII Purge (C-09)
        val c09Finding = recurringFindings.firstOrNull { it.controlId == "C-09" }
        val c09Remediation = overdueList.firstOrNull { it.controlId == "C-09" }
        checklistItems.add(
            InvestigationPlanItem(
                priority = "HIGH",
                category = "Data Retention & Privacy",
                controlCode = "C-09 (DR-04.1)",
                task = "Verify database partition automated purge script execution on customer transaction tables to clear records exceeding the 7-year ceiling.",
                historicalBasis = "6.94M customer PII records remain unpurged. Oracle foreign key constraint locks (ORA-02292) caused purge job failures. Remediation (${c09Remediation?.title ?: "R-20"}) stalled pending 2027 refactoring. Direct regulatory exposure under GDPR Art 17.",
                requestedEvidence = "SELECT MIN(transaction_date), COUNT(*) FROM customer_transactions result + DBA purge cron standard error output log.",
                targetTeam = "Data Governance & Core Database Ops (Priya Sharma)"
            )
        )

        // Synthesize Item 5: Cryptographic Key Lifecycle (C-11)
        checklistItems.add(
            InvestigationPlanItem(
                priority = "MEDIUM",
                category = "Cryptographic Hygiene",
                controlCode = "C-11 (CR-05.1)",
                task = "Audit HashiCorp Vault token and master key rotation intervals for Open Banking API gateway endpoints.",
                historicalBasis = "Mobile payment token signing key exceeded 90-day rotation SLA (active for 134 days). Policy mandates automated zero-downtime rotation every 90 days.",
                requestedEvidence = "HashiCorp Vault key metadata JSON output + automated rotation cronjob execution timestamps.",
                targetTeam = "Infrastructure & Platform Engineering (Alex Chen)"
            )
        )

        // Synthesize Item 6: Incident Response & Ransomware Tabletop (C-07)
        checklistItems.add(
            InvestigationPlanItem(
                priority = "MEDIUM",
                category = "Resilience & Incident Readiness",
                controlCode = "C-07 (IR-02.4)",
                task = "Review executive sign-off and action-item tracking from the Q1 ransomware containment and regulator notification simulation drill.",
                historicalBasis = "Previous audit identified outdated emergency notification contacts and missing regulatory liaison protocols. Tabletop drill conducted but findings tracking unresolved.",
                requestedEvidence = "Post-drill executive after-action report + Jira risk ticket references for communications playbook updates.",
                targetTeam = "Security Operations & Legal Compliance (Elena Rostova)"
            )
        )

        checklistItems
    }
}

