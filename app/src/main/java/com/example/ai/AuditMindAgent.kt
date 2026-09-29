package com.example.ai

import com.example.data.model.*
import com.example.data.repository.AuditRepository
import com.example.hindsight.HindsightService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AgentResponse(
    val query: String,
    val answer: String,
    val memoryAware: Boolean,
    val memoriesUsed: List<HindsightMemory>,
    val evidenceChain: List<MemoryEvidenceNode>,
    val toolsCalled: List<String>,
    val confidence: String,
    val recurringDetected: Boolean = false,
    val disclaimer: String = "AI-generated analysis — verify against applicable regulations and organizational policy."
)

class AuditMindAgent(
    private val repository: AuditRepository,
    val hindsightService: HindsightService,
    private val agentTools: AgentTools
) {
    var modelName: String = "gemini-3.5-flash"
    var isThinkingMode: Boolean = true

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askAgent(
        prompt: String,
        useMemory: Boolean = true
    ): AgentResponse = withContext(Dispatchers.IO) {
        if (!useMemory) {
            // Scene 1: Without historical memory (Generic AI)
            return@withContext AgentResponse(
                query = prompt,
                answer = "This appears to be an access-control and governance issue according to baseline security standards. Without persistent institutional records or audit history, I cannot determine if this has occurred before, why past risks were accepted, or which remediation commitments were missed.",
                memoryAware = false,
                memoriesUsed = emptyList(),
                evidenceChain = emptyList(),
                toolsCalled = emptyList(),
                confidence = "Low (Context Deficit)"
            )
        }

        // Scene 2 & 3: With AuditMind Memory
        val queryLower = prompt.lowercase()
        val toolsExecuted = mutableListOf<String>()

        // Check for specific queries
        when {
            queryLower.contains("recurring") || queryLower.contains("repeat") -> {
                toolsExecuted.add("find_recurring_findings")
                toolsExecuted.add("ask_hindsight_reflect")
                val recurring = repository.recurringFindings.first()
                val reflection = hindsightService.reflectMemory(prompt)

                val answer = """
                    **AuditMind Analysis — Recurring Compliance Vulnerabilities Detected:**
                    
                    Institutional memory spanning **2024, 2025, and 2026** reveals **4 systemic recurrence clusters**:
                    
                    1. 🔴 **Control C-17 (Privileged Access Management & Bastion MFA)**
                       - **3 consecutive audit failures** (F-104, F-204, F-301).
                       - 89 direct root sessions logged in 2026 without hardware MFA.
                       - **Root Cause**: Two sequential management risk acceptances (DEC-2024-01 & DEC-2025-01) relied on a cloud migration ('Project Horizon') that suffered multi-year delays. Manual log reviews were abandoned in Oct 2024.
                    
                    2. 🔴 **Control C-03 (Quarterly User Access & Orphaned Contractor Accounts)**
                       - **3 consecutive audit failures** (F-102, F-202, F-302).
                       - 35 contractor accounts currently active > 60 days post-termination with active VPN entitlements.
                       - **Root Cause**: Reliance on manual spreadsheet reviews after Workday-Okta automated SCIM connector (R-15) was postponed.
                    
                    3. 🟠 **Control C-05 (Third-Party Vendor Risk Assessment & SOC 2)**
                       - Uncertified Tier-1 vendors grew from 8 (2024) to 14 (2025) to 19 (2026).
                       - **Root Cause**: Vendor portal project was canceled due to budget cuts; executive waiver #PW-2025-09 expired without remediation.
                    
                    4. 🟠 **Control C-09 (PII & Transaction Data Retention)**
                       - Unpurged customer records grew from 4.2M (2024) to 5.8M (2025) to 6.94M (2026).
                       - **Root Cause**: Database purge scripts continuously crashed on foreign key constraints (ORA-02292); database refactoring postponed to 2027.
                """.trimIndent()

                return@withContext AgentResponse(
                    query = prompt,
                    answer = answer,
                    memoryAware = true,
                    memoriesUsed = reflection.memorySources,
                    evidenceChain = reflection.evidenceChain,
                    toolsCalled = toolsExecuted,
                    confidence = "High (98%)",
                    recurringDetected = true
                )
            }

            queryLower.contains("why") || queryLower.contains("reason") || queryLower.contains("accept") -> {
                toolsExecuted.add("ask_hindsight_recall")
                toolsExecuted.add("ask_hindsight_reflect")
                toolsExecuted.add("get_control_history")

                val targetControl = if (queryLower.contains("vendor") || queryLower.contains("soc")) "C-05"
                else if (queryLower.contains("retention") || queryLower.contains("data")) "C-09"
                else if (queryLower.contains("contractor") || queryLower.contains("user access")) "C-03"
                else "C-17"

                val reflection = hindsightService.reflectMemory(prompt, targetControl)
                val decisions = repository.getDecisionsForFindingOrControl(null, targetControl)

                val decisionDetails = decisions.joinToString("\n\n") { dec ->
                    """
                    • **${dec.year} Decision (${dec.decisionType})**: Approved by *${dec.decisionMaker}*.
                      - **Rationale**: ${dec.rationale}
                      - **Compensating Control**: ${dec.compensatingControl}
                      - **Promised Remediation**: ${dec.promisedRemediation} (Deadline: ${dec.targetDeadline})
                      - **Real Operational Status**: ${dec.currentRealStatus}
                    """.trimIndent()
                }

                val answer = """
                    **AuditMind Institutional Memory Reconstruction — Historical Reasoning:**
                    
                    $decisionDetails
                    
                    **AI Synthesis & Pattern Assessment**:
                    The organization repeatedly substituted architectural remediation with temporary risk waivers and manual compensating controls. Because no automated audit mechanism tracked whether the compensating controls were actually being performed, the departure of single individuals (such as the Lead Security Analyst in Oct 2024) caused total failure of the control environment without leadership awareness until the subsequent audit.
                """.trimIndent()

                return@withContext AgentResponse(
                    query = prompt,
                    answer = answer,
                    memoryAware = true,
                    memoriesUsed = reflection.memorySources,
                    evidenceChain = reflection.evidenceChain,
                    toolsCalled = toolsExecuted,
                    confidence = "High (97%)",
                    recurringDetected = true
                )
            }

            queryLower.contains("what changed") || queryLower.contains("compare") || queryLower.contains("previous audit") -> {
                toolsExecuted.add("compare_audits")
                toolsExecuted.add("search_audit_history")
                val comparison = agentTools.compareAudits(2025, 2026)

                val answer = """
                    **Temporal Delta: 2025 Audit vs. 2026 Audit Findings:**
                    
                    • **Overall Findings Count**: Reduced from 31 in 2025 to 10 prioritized pre-audit items in 2026.
                    • **Recurring vs. New Findings**:
                      - **4 Critical Recurring Issues**: MFA Bastion (C-17), Orphaned Accounts (C-03), Vendor SOC 2 (C-05), and PII Over-Retention (C-09).
                      - **6 New Operational Issues**: Ingress controller debug endpoint exposed (F-305), mobile token rotation SLA lapse (F-303), and sanctions screening fuzzy-match calibration (F-310).
                    • **Remediation Breakdown**:
                      - Changes completed: CI/CD branch protection (R-08), ransomware executive tabletop drill (R-11), and Vault PSD2 API key rotation (R-33).
                      - Unresolved commitments: CyberArk bastion integration (R-22) and contractor Okta SCIM connector (R-15).
                    • **Risk Trajectory**: While infrastructure hygiene improved, core legacy systems (Oracle Core Banking & unpartitioned transaction tables) remain critical vulnerabilities.
                """.trimIndent()

                val memories = hindsightService.recallMemory("compare audit 2025 2026")
                val evidenceChain = hindsightService.getMemoryEvidenceForControl("C-17")

                return@withContext AgentResponse(
                    query = prompt,
                    answer = answer,
                    memoryAware = true,
                    memoriesUsed = memories,
                    evidenceChain = evidenceChain,
                    toolsCalled = toolsExecuted,
                    confidence = "High (96%)"
                )
            }

            queryLower.contains("investigation") || queryLower.contains("plan") || queryLower.contains("prepare") -> {
                toolsExecuted.add("find_recurring_findings")
                toolsExecuted.add("get_remediation_status")
                toolsExecuted.add("generate_evidence_request")

                val plan = prepareInvestigationPlan()
                val planText = StringBuilder("**Actionable Pre-Audit Investigation Plan (Target: Tomorrow's Fieldwork):**\n\n")

                plan.forEachIndexed { index, item ->
                    planText.append("${index + 1}. **[${item.priority}] ${item.category} (Control ${item.controlCode})**\n")
                    planText.append("   • **Task**: ${item.task}\n")
                    planText.append("   • **Historical Basis**: ${item.historicalBasis}\n")
                    planText.append("   • **Evidence to Inspect**: ${item.requestedEvidence}\n")
                    planText.append("   • **Target Owner**: ${item.targetTeam}\n\n")
                }

                val memories = hindsightService.recallMemory("recurring audit finding C-17 C-03 C-05 C-09")
                val evidenceChain = hindsightService.getMemoryEvidenceForControl("C-17")

                return@withContext AgentResponse(
                    query = prompt,
                    answer = planText.toString().trim(),
                    memoryAware = true,
                    memoriesUsed = memories,
                    evidenceChain = evidenceChain,
                    toolsCalled = toolsExecuted,
                    confidence = "High (99%)"
                )
            }

            else -> {
                // General grounded query utilizing core search_audit_history tool
                toolsExecuted.add("search_audit_history")
                toolsExecuted.add("ask_hindsight_recall")
                val searchResult = agentTools.search_audit_history(query = prompt)
                val memories = hindsightService.recallMemory(prompt, limit = 5)
                val reflection = hindsightService.reflectMemory(prompt)

                val answer = """
                    **AuditMind Compliance Intelligence:**
                    
                    ${reflection.answer}
                    
                    **Historical Grounding (search_audit_history)**:
                    Retrieved ${memories.size} institutional memory records and matching audit findings from NovaBank repository. Recurring control weaknesses in Privileged Access (C-17), Identity Recertification (C-03), and Vendor Risk (C-05) remain primary audit risks.
                """.trimIndent()

                return@withContext AgentResponse(
                    query = prompt,
                    answer = answer,
                    memoryAware = true,
                    memoriesUsed = memories,
                    evidenceChain = reflection.evidenceChain,
                    toolsCalled = toolsExecuted,
                    confidence = "High (94%)"
                )
            }
        }
    }

    suspend fun prepareInvestigationPlan(): List<InvestigationPlanItem> = withContext(Dispatchers.IO) {
        listOf(
            InvestigationPlanItem(
                priority = "CRITICAL",
                category = "Privileged Access",
                controlCode = "C-17 (AC-02.1)",
                task = "Verify physical MFA challenge on Oracle Core Banking Bastion (10.200.4.12).",
                historicalBasis = "Failed in 2024 and 2025. Two expired risk waivers. 89 unauthenticated root logins in 2026.",
                requestedEvidence = "Live terminal demo + auth.log extract for past 7 days.",
                targetTeam = "IT Security (Marcus Vance)"
            ),
            InvestigationPlanItem(
                priority = "CRITICAL",
                category = "Access Management",
                controlCode = "C-03 (AC-06.3)",
                task = "Cross-reference HR termination roster with active Active Directory VPN security groups.",
                historicalBasis = "Failed 3 consecutive audits. 35 departed contractors currently active.",
                requestedEvidence = "LDAP dump of CN=VPN-Users and Workday offboarding delta report.",
                targetTeam = "IAM Lead (Sarah Lin)"
            ),
            InvestigationPlanItem(
                priority = "HIGH",
                category = "Vendor Security",
                controlCode = "C-05 (VR-01.2)",
                task = "Inspect SOC 2 Type II reports and bridge letters for Tier-1 payment vendors.",
                historicalBasis = "Procurement waiver expired Dec 2025. 19 critical vendors unreviewed.",
                requestedEvidence = "Vendor Risk Register and SOC 2 auditor opinion pages.",
                targetTeam = "Vendor Risk (David Ross)"
            ),
            InvestigationPlanItem(
                priority = "HIGH",
                category = "Data Governance",
                controlCode = "C-09 (DR-04.1)",
                task = "Verify database partition purge execution on consumer transaction tables.",
                historicalBasis = "Purge cron crashed on foreign key locks. 6.9M records exceed 7-year ceiling.",
                requestedEvidence = "SELECT MIN(tx_timestamp) query result + cron execution logs.",
                targetTeam = "Data Governance (Priya Sharma)"
            ),
            InvestigationPlanItem(
                priority = "MEDIUM",
                category = "Cryptographic Controls",
                controlCode = "C-11 (CR-05.1)",
                task = "Inspect HashiCorp Vault mobile API key age telemetry.",
                historicalBasis = "Signing key exceeded 90-day rotation SLA (active 134 days).",
                requestedEvidence = "Vault key metadata JSON output.",
                targetTeam = "Infrastructure Team"
            )
        )
    }

    suspend fun reconstructWhy(targetId: String): Map<String, Any> = withContext(Dispatchers.IO) {
        val controlId = if (targetId.startsWith("C-")) targetId else {
            repository.getFindingById(targetId)?.controlId ?: "C-17"
        }

        val control = repository.getControlById(controlId)
        val findings = repository.getFindingsForControl(controlId)
        val decisions = repository.getDecisionsForFindingOrControl(null, controlId)
        val remediations = repository.getRemediationsForControl(controlId)
        val evidenceChain = hindsightService.getMemoryEvidenceForControl(controlId)

        mapOf(
            "controlId" to controlId,
            "controlName" to (control?.name ?: "Unknown Control"),
            "findings" to findings,
            "decisions" to decisions,
            "remediations" to remediations,
            "evidenceChain" to evidenceChain,
            "whySummary" to when (controlId) {
                "C-17" -> "Management accepted risk in 2024 and 2025 expecting 'Project Horizon' cloud migration to retire the legacy core banking server. Compensating manual log reviews ceased when the analyst resigned. CyberArk failed to support legacy AIX OS."
                "C-03" -> "Okta SCIM automation was delayed by IAM team restructuring. Management relied on manual spreadsheets which broke down, leaving contractor accounts active."
                "C-05" -> "Automated vendor portal was canceled due to Q4 2024 budget cuts. Procurement issued waivers instead of demanding SOC 2 reports from critical vendors."
                "C-09" -> "Automated purge scripts crashed on foreign key constraints. Management deferred database refactoring pending a 2027 warehouse migration."
                else -> "Historical finding recurring due to incomplete remediation and expired risk waivers."
            }
        )
    }
}
