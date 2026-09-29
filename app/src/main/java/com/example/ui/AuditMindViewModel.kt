package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AgentResponse
import com.example.ai.AgentTools
import com.example.ai.AuditMindAgent
import com.example.auth.AuthStatus
import com.example.auth.FirebaseAuthManager
import com.example.data.cloud.CloudSqlQueryResult
import com.example.data.cloud.CloudSqlService
import com.example.data.cloud.CloudSqlSyncResult
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.AuditRepository
import com.example.data.seed.NovaBankDataSeeder
import com.example.data.seed.NovaBankSeedData
import com.example.hindsight.HindsightService
import com.example.service.export.AuditExportData
import com.example.service.export.AuditPdfExportService
import com.example.service.export.PdfExportResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen {
    LOGIN,
    DASHBOARD,
    RISK_RADAR,
    TIMELINE,
    AI_ANALYST,
    FINDINGS,
    CONTROLS,
    REMEDIATION,
    EVIDENCE,
    POLICY_IMPACT,
    AUDIT_PREP,
    MEMORY_HUB,
    SETTINGS
}

data class UserProfile(
    val name: String,
    val email: String,
    val role: String,
    val department: String,
    val avatarInitials: String,
    val firebaseUid: String? = null
)

data class WhyModalState(
    val isOpen: Boolean = false,
    val targetName: String = "",
    val finding: AuditFinding? = null,
    val decision: AuditDecision? = null,
    val remediation: RemediationCommitment? = null,
    val aiSynthesis: String = "",
    val reasoningChain: DecisionReasoningChain? = null
)

class AuditMindViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AuditRepository(database.auditDao())
    val hindsightService = HindsightService(repository)
    private val agentTools = AgentTools(repository, hindsightService)
    val agent = AuditMindAgent(repository, hindsightService, agentTools)

    // Firebase Auth Integration
    val firebaseAuthManager = FirebaseAuthManager(application)
    val authStatus: StateFlow<AuthStatus> = firebaseAuthManager.authStatus

    // Google Cloud SQL Integration
    val cloudSqlService = CloudSqlService(repository)
    val isCloudSqlConnected: StateFlow<Boolean> = cloudSqlService.isConnected
    val cloudSqlLastSync: StateFlow<String> = cloudSqlService.lastSyncSummary
    val isCloudSqlSyncing: StateFlow<Boolean> = cloudSqlService.isSyncing

    // Current User Session
    private val _currentUser = MutableStateFlow<UserProfile?>(
        UserProfile(
            name = "Marcus Vance",
            email = "m.vance@novabank.com",
            role = "Chief Information Security Officer (CISO)",
            department = "Information Security & Risk",
            avatarInitials = "MV",
            firebaseUid = "fb-usr-mvance-ciso-01"
        )
    )
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    fun login(email: String, name: String, role: String, department: String) {
        val initials = name.split(" ")
            .filter { it.isNotBlank() }
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
        
        firebaseAuthManager.signInWithEmail(
            email = email,
            pass = "NovaBankCompliance2026!",
            onSuccess = { uid ->
                _currentUser.value = UserProfile(
                    name = name,
                    email = email,
                    role = role,
                    department = department,
                    avatarInitials = initials.ifEmpty { "NB" },
                    firebaseUid = uid
                )
                _currentScreen.value = Screen.DASHBOARD
            },
            onError = {
                _currentUser.value = UserProfile(
                    name = name,
                    email = email,
                    role = role,
                    department = department,
                    avatarInitials = initials.ifEmpty { "NB" },
                    firebaseUid = "fb-fallback-session"
                )
                _currentScreen.value = Screen.DASHBOARD
            }
        )
    }

    fun loginAnonymously() {
        firebaseAuthManager.signInAnonymously(
            onSuccess = { uid ->
                _currentUser.value = UserProfile(
                    name = "Guest External Auditor",
                    email = "guest.auditor@external-review.com",
                    role = "Independent OCC Regulatory Examiner",
                    department = "External Regulatory Oversight",
                    avatarInitials = "GA",
                    firebaseUid = uid
                )
                _currentScreen.value = Screen.DASHBOARD
            },
            onError = {
                _currentUser.value = UserProfile(
                    name = "Guest External Auditor",
                    email = "guest.auditor@external-review.com",
                    role = "Independent OCC Regulatory Examiner",
                    department = "External Regulatory Oversight",
                    avatarInitials = "GA",
                    firebaseUid = "anon-fallback"
                )
                _currentScreen.value = Screen.DASHBOARD
            }
        )
    }

    fun logout() {
        firebaseAuthManager.signOut()
        _currentUser.value = null
        _currentScreen.value = Screen.LOGIN
    }

    fun syncWithCloudSql(onComplete: ((CloudSqlSyncResult) -> Unit)? = null) {
        viewModelScope.launch {
            val result = cloudSqlService.syncWithCloudSql()
            onComplete?.invoke(result)
        }
    }

    fun testCloudSqlConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = cloudSqlService.testConnection()
            onResult(res.first, res.second)
        }
    }

    fun executeCloudSqlQuery(sql: String, onResult: (CloudSqlQueryResult) -> Unit) {
        viewModelScope.launch {
            val res = cloudSqlService.executeAuditQuery(sql)
            onResult(res)
        }
    }


    // Current Screen
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Data Flows
    val allFindings: StateFlow<List<AuditFinding>> = repository.allFindings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recurringFindings: StateFlow<List<AuditFinding>> = repository.recurringFindings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allControls: StateFlow<List<ComplianceControl>> = repository.allControls
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRemediations: StateFlow<List<RemediationCommitment>> = repository.allRemediations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overdueRemediations: StateFlow<List<RemediationCommitment>> = repository.overdueRemediations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvidence: StateFlow<List<EvidenceRecord>> = repository.allEvidence
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDecisions: StateFlow<List<AuditDecision>> = repository.allDecisions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMemories: StateFlow<List<HindsightMemory>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timeline control selection
    private val _selectedTimelineControlId = MutableStateFlow("C-17")
    val selectedTimelineControlId: StateFlow<String> = _selectedTimelineControlId.asStateFlow()

    private val _timelineNodes = MutableStateFlow<List<MemoryEvidenceNode>>(emptyList())
    val timelineNodes: StateFlow<List<MemoryEvidenceNode>> = _timelineNodes.asStateFlow()

    // "WHY?" Dialog State
    private val _whyState = MutableStateFlow(WhyModalState())
    val whyState: StateFlow<WhyModalState> = _whyState.asStateFlow()

    // AI Analyst Chat State
    private val _chatMessages = MutableStateFlow<List<AgentResponse>>(emptyList())
    val chatMessages: StateFlow<List<AgentResponse>> = _chatMessages.asStateFlow()

    private val _isAgentThinking = MutableStateFlow(false)
    val isAgentThinking: StateFlow<Boolean> = _isAgentThinking.asStateFlow()

    // Theme Mode (Dark by default, user-toggleable)
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Uploaded Policies List
    private val _uploadedPolicies = MutableStateFlow<List<PolicyDocument>>(
        listOf(
            PolicyDocument(
                id = "POL-DOC-01",
                title = "Quarterly Privileged Access & Bastion Recertification Mandate",
                code = "POL-SEC-04 v3.0",
                version = "3.0",
                department = "IT Security",
                effectiveDate = "2026-10-01",
                rawText = "All interactive root or DBA access to production databases must utilize hardware MFA tokens (FIDO2/WebAuthn). Temporary waivers shall not exceed 60 calendar days and must have verified automated compensating controls.",
                keyRequirements = listOf(
                    "Mandatory hardware MFA on all bastions",
                    "60-day maximum limit on risk waivers",
                    "Automated session recording and credential vaulting"
                )
            )
        )
    )
    val uploadedPolicies: StateFlow<List<PolicyDocument>> = _uploadedPolicies.asStateFlow()

    // Policy Impact State
    private val _policyImpactResult = MutableStateFlow<PolicyImpactAnalysis?>(null)
    val policyImpactResult: StateFlow<PolicyImpactAnalysis?> = _policyImpactResult.asStateFlow()

    // Audit Preparation Plan State
    private val _investigationPlan = MutableStateFlow<List<InvestigationPlanItem>>(emptyList())
    val investigationPlan: StateFlow<List<InvestigationPlanItem>> = _investigationPlan.asStateFlow()

    // PDF Export State
    val pdfExportService = AuditPdfExportService(application)
    private val _isExportingPdf = MutableStateFlow(false)
    val isExportingPdf: StateFlow<Boolean> = _isExportingPdf.asStateFlow()

    private val _lastPdfExport = MutableStateFlow<PdfExportResult?>(null)
    val lastPdfExport: StateFlow<PdfExportResult?> = _lastPdfExport.asStateFlow()

    private val _selectedAuditYearForExport = MutableStateFlow(2026)
    val selectedAuditYearForExport: StateFlow<Int> = _selectedAuditYearForExport.asStateFlow()

    fun setSelectedAuditYearForExport(year: Int) {
        _selectedAuditYearForExport.value = year
    }

    init {
        // Initialize with default seed data
        seedNovaBankData()
        selectTimelineControl("C-17")
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    val dataSeeder = NovaBankDataSeeder(repository)

    fun seedNovaBankData() {
        viewModelScope.launch {
            dataSeeder.seedDatabase(repository)
            selectTimelineControl(_selectedTimelineControlId.value)
        }
    }

    private val _selectedTimelineTargetType = MutableStateFlow("CONTROL") // "CONTROL" or "FINDING"
    val selectedTimelineTargetType: StateFlow<String> = _selectedTimelineTargetType.asStateFlow()

    fun selectTimelineControl(controlId: String) {
        _selectedTimelineTargetType.value = "CONTROL"
        _selectedTimelineControlId.value = controlId
        viewModelScope.launch {
            val nodes = hindsightService.getMemoryEvidenceForControl(controlId)
            _timelineNodes.value = nodes
        }
    }

    fun selectTimelineFinding(findingId: String) {
        _selectedTimelineTargetType.value = "FINDING"
        _selectedTimelineControlId.value = findingId
        viewModelScope.launch {
            val nodes = hindsightService.getMemoryEvidenceForFinding(findingId)
            _timelineNodes.value = nodes
        }
    }

    fun reconstructWhyForDecision(decision: AuditDecision) {
        viewModelScope.launch {
            val chain = hindsightService.reconstructReasoningChainForDecision(decision)
            _whyState.value = WhyModalState(
                isOpen = true,
                targetName = "${decision.id}: ${decision.title}",
                finding = chain.targetFinding,
                decision = decision,
                remediation = chain.remediation,
                aiSynthesis = chain.rootCauseHypothesis,
                reasoningChain = chain
            )
        }
    }

    fun openWhyModal(targetId: String) {
        viewModelScope.launch {
            // If targetId is directly a decision ID
            if (targetId.startsWith("DEC-") || targetId.startsWith("EW-") || targetId.startsWith("PW-") || targetId.startsWith("MEM-")) {
                val decisions = repository.getDecisionsForFindingOrControl(null, null)
                val targetDecision = decisions.firstOrNull { it.id.equals(targetId, ignoreCase = true) }
                    ?: decisions.firstOrNull { targetId.contains(it.id, ignoreCase = true) }
                    ?: decisions.firstOrNull()
                if (targetDecision != null) {
                    reconstructWhyForDecision(targetDecision)
                    return@launch
                }
            }

            val controlId = if (targetId.startsWith("C-")) targetId else {
                repository.getFindingById(targetId)?.controlId ?: "C-17"
            }
            val control = repository.getControlById(controlId)
            val findings = repository.getFindingsForControl(controlId)
            val decisions = repository.getDecisionsForFindingOrControl(null, controlId)
            val remediations = repository.getRemediationsForControl(controlId)

            val linkedDecision = decisions.firstOrNull()
            if (linkedDecision != null) {
                val chain = hindsightService.reconstructReasoningChainForDecision(linkedDecision)
                _whyState.value = WhyModalState(
                    isOpen = true,
                    targetName = "${control?.name ?: targetId} ($controlId)",
                    finding = findings.firstOrNull(),
                    decision = linkedDecision,
                    remediation = remediations.firstOrNull(),
                    aiSynthesis = chain.rootCauseHypothesis,
                    reasoningChain = chain
                )
            } else {
                val whyResult = agent.reconstructWhy(controlId)
                val summary = whyResult["whySummary"] as? String ?: "Historical recurrence due to missed remediation and repeated risk acceptance."
                _whyState.value = WhyModalState(
                    isOpen = true,
                    targetName = "${control?.name ?: targetId} ($controlId)",
                    finding = findings.firstOrNull(),
                    decision = decisions.firstOrNull(),
                    remediation = remediations.firstOrNull(),
                    aiSynthesis = summary
                )
            }
        }
    }

    fun closeWhyModal() {
        _whyState.value = _whyState.value.copy(isOpen = false)
    }

    fun askAnalyst(prompt: String, useMemory: Boolean = true) {
        viewModelScope.launch {
            _isAgentThinking.value = true
            val response = agent.askAgent(prompt, useMemory)
            _chatMessages.value = _chatMessages.value + response
            _isAgentThinking.value = false
        }
    }

    fun runPolicyImpactAnalysis(oldReq: String, newReq: String) {
        viewModelScope.launch {
            _policyImpactResult.value = agentTools.analyzePolicyChange(oldReq, newReq)
        }
    }

    fun generateInvestigationPlan() {
        viewModelScope.launch {
            _isAgentThinking.value = true
            // Synthesize using agent research tools
            val checklist = agentTools.synthesizeInvestigationChecklist()
            _investigationPlan.value = checklist
            _isAgentThinking.value = false
        }
    }

    fun exportAuditPdf(year: Int = _selectedAuditYearForExport.value, onComplete: ((PdfExportResult) -> Unit)? = null) {
        viewModelScope.launch {
            _isExportingPdf.value = true

            val findings = repository.allFindings.first().filter { it.auditYear == year }
            val remediations = repository.allRemediations.first()
            val decisions = repository.getDecisionsForFindingOrControl(null, null).filter { it.year == year }
            val memories = repository.allMemories.first().filter { it.temporalYear == year }

            val executiveSummary = when (year) {
                2026 -> "The 2026 pre-audit cycle identified critical recurring exposures in Privileged Access Bastions (C-17) and Contractor Offboarding (C-03). While infrastructure controls improved, unrevoked risk acceptances from 2024 and 2025 have created compounding regulatory vulnerabilities under OCC and SOC 2 frameworks."
                2025 -> "The 2025 comprehensive review showed 31 total findings, with multiple temporary risk waivers granted for core banking migrations. Compensating manual controls failed to be maintained following team turnover."
                else -> "The 2024 baseline audit established foundational compliance metrics with initial findings identified across database bastions and third-party vendor assessments."
            }

            val exportData = AuditExportData(
                auditYear = year,
                auditTitle = "NovaBank Annual Security & Regulatory Audit ($year)",
                generatedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()),
                findings = findings,
                remediations = remediations,
                decisions = decisions,
                memories = memories,
                executiveSummary = executiveSummary
            )

            val result = pdfExportService.generateAuditPdf(exportData)
            _lastPdfExport.value = result
            _isExportingPdf.value = false
            onComplete?.invoke(result)
        }
    }

    fun shareAuditPdf(result: PdfExportResult = _lastPdfExport.value ?: error("No PDF exported yet")) {
        pdfExportService.sharePdf(result.file, result.auditYear)
    }

    fun testRecall(query: String, onResult: (List<HindsightMemory>) -> Unit) {
        viewModelScope.launch {
            val result = hindsightService.recallMemory(query)
            onResult(result)
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
    }

    fun uploadAndAnalyzePolicy(
        title: String,
        code: String,
        version: String,
        department: String,
        effectiveDate: String,
        policyText: String
    ) {
        viewModelScope.launch {
            _isAgentThinking.value = true

            // 1. RETAIN in Hindsight persistent memory
            hindsightService.retain_memory(
                text = "POLICY UPDATE: $code ($title v$version) effective $effectiveDate. Scope: $policyText",
                tags = listOf("type:policy", "policy:$code", "dept:${department.lowercase().replace(" ", "_")}", "audit:2026"),
                temporalYear = 2026,
                entities = listOf(code, title, department),
                memoryType = MemoryType.POLICY
            )

            // 2. RECALL & REFLECT using Agent tools
            val impact = agentTools.analyzePolicyChange(
                oldRequirement = "Previous standard policy requirements for $department",
                newRequirement = policyText
            )
            _policyImpactResult.value = impact

            // 3. Add to uploaded policy store
            val newDoc = PolicyDocument(
                id = "POL-${System.currentTimeMillis().toString().takeLast(4)}",
                title = title,
                code = code,
                version = version,
                department = department,
                effectiveDate = effectiveDate,
                rawText = policyText,
                keyRequirements = policyText.split("\n", ".").filter { it.isNotBlank() }.take(4)
            )
            _uploadedPolicies.value = listOf(newDoc) + _uploadedPolicies.value

            _isAgentThinking.value = false
        }
    }

    fun retainCustomMemory(text: String, tags: String, year: Int, entity: String) {
        viewModelScope.launch {
            val tagList = tags.split(",").map { it.trim() }
            hindsightService.retainMemory(
                text = text,
                tags = tagList,
                temporalYear = year,
                entities = listOf(entity),
                memoryType = MemoryType.OBSERVATION
            )
        }
    }
}
