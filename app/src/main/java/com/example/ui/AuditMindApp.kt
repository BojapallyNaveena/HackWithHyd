package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.WhyReasoningDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditMindApp(viewModel: AuditMindViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val whyState by viewModel.whyState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    // Handle back button when on sub-screens
    BackHandler(enabled = currentScreen != Screen.DASHBOARD && currentScreen != Screen.LOGIN) {
        viewModel.navigateTo(Screen.DASHBOARD)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Navy900,
                drawerContentColor = Slate50,
                modifier = Modifier.width(300.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Brand Header in Drawer
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TechCyanDark,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Shield,
                                contentDescription = "Logo",
                                tint = TechCyanLight,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AUDITMIND AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "The agent that remembers why",
                            style = MaterialTheme.typography.labelSmall,
                            color = TechCyanLight,
                            fontSize = 10.sp
                        )
                    }
                }

                HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 12.dp))

                // Navigation Items
                val navItems = listOf(
                    NavigationItem("Dashboard", Screen.DASHBOARD, Icons.Default.Dashboard),
                    NavigationItem("Risk Radar", Screen.RISK_RADAR, Icons.Default.Radar),
                    NavigationItem("Audit Timeline", Screen.TIMELINE, Icons.Default.Timeline),
                    NavigationItem("AI Analyst", Screen.AI_ANALYST, Icons.Default.AutoAwesome),
                    NavigationItem("Audit Findings", Screen.FINDINGS, Icons.Default.Assessment),
                    NavigationItem("Controls Catalog", Screen.CONTROLS, Icons.Default.VerifiedUser),
                    NavigationItem("Remediations", Screen.REMEDIATION, Icons.Default.Schedule),
                    NavigationItem("Evidence Vault", Screen.EVIDENCE, Icons.Default.Description),
                    NavigationItem("Policy Impact", Screen.POLICY_IMPACT, Icons.Default.CompareArrows),
                    NavigationItem("Audit Preparation", Screen.AUDIT_PREP, Icons.Default.PlaylistAddCheck),
                    NavigationItem("Hindsight Memory", Screen.MEMORY_HUB, Icons.Default.Psychology),
                    NavigationItem("Sign In / Profile", Screen.LOGIN, Icons.Default.AccountCircle),
                    NavigationItem("Settings & Demo", Screen.SETTINGS, Icons.Default.Settings)
                )

                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) TechCyanLight else Slate400
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Slate200
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            viewModel.navigateTo(item.screen)
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = TechCyanDark.copy(alpha = 0.4f),
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (currentUser != null) {
                    Surface(
                        color = Slate800,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(TechCyanDark, androidx.compose.foundation.shape.CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = currentUser!!.avatarInitials,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentUser!!.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentUser!!.role,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active Session",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SuccessEmerald,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = "Sign Out",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CriticalRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.clickable {
                                        viewModel.logout()
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.navigateTo(Screen.LOGIN)
                            scope.launch { drawerState.close() }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TechCyanDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In to Portal", fontSize = 12.sp)
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "AuditMind AI",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = when (currentScreen) {
                                    Screen.LOGIN -> "NovaBank Compliance Portal"
                                    Screen.DASHBOARD -> "NovaBank Compliance Intelligence"
                                    Screen.RISK_RADAR -> "Recurring Risk Radar"
                                    Screen.TIMELINE -> "Audit Memory Timeline"
                                    Screen.AI_ANALYST -> "AI Compliance Analyst"
                                    Screen.FINDINGS -> "Findings Repository"
                                    Screen.CONTROLS -> "Controls Catalog"
                                    Screen.REMEDIATION -> "Remediation Commitments"
                                    Screen.EVIDENCE -> "Evidence Vault"
                                    Screen.POLICY_IMPACT -> "Policy Impact Analyzer"
                                    Screen.AUDIT_PREP -> "Audit Preparation Plan"
                                    Screen.MEMORY_HUB -> "Hindsight Persistent Memory"
                                    Screen.SETTINGS -> "Platform Settings & Demo"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = TechCyanLight,
                                fontSize = 10.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.toggleTheme() }) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "Toggle Light/Dark Theme",
                                tint = if (isDarkTheme) GoldAccent else TechCyanLight
                            )
                        }
                        IconButton(onClick = { viewModel.navigateTo(Screen.AI_ANALYST) }) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Analyst", tint = TechCyanLight)
                        }
                        IconButton(onClick = { viewModel.navigateTo(Screen.SETTINGS) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Slate400)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy900)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Navy900,
                    contentColor = Slate200
                ) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.DASHBOARD,
                        onClick = { viewModel.navigateTo(Screen.DASHBOARD) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanLight,
                            selectedTextColor = TechCyanLight,
                            indicatorColor = TechCyanDark.copy(alpha = 0.4f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.RISK_RADAR,
                        onClick = { viewModel.navigateTo(Screen.RISK_RADAR) },
                        icon = { Icon(Icons.Default.Radar, contentDescription = "Radar") },
                        label = { Text("Risk Radar", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CriticalRed,
                            selectedTextColor = CriticalRed,
                            indicatorColor = CriticalRedBg
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.TIMELINE,
                        onClick = { viewModel.navigateTo(Screen.TIMELINE) },
                        icon = { Icon(Icons.Default.Timeline, contentDescription = "Timeline") },
                        label = { Text("Timeline", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanLight,
                            selectedTextColor = TechCyanLight,
                            indicatorColor = TechCyanDark.copy(alpha = 0.4f)
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.AI_ANALYST,
                        onClick = { viewModel.navigateTo(Screen.AI_ANALYST) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Analyst") },
                        label = { Text("Analyst", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFC4B5FD),
                            selectedTextColor = Color(0xFFC4B5FD),
                            indicatorColor = MemoryPurpleBg
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == Screen.FINDINGS,
                        onClick = { viewModel.navigateTo(Screen.FINDINGS) },
                        icon = { Icon(Icons.Default.Assessment, contentDescription = "Findings") },
                        label = { Text("Findings", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TechCyanLight,
                            selectedTextColor = TechCyanLight,
                            indicatorColor = TechCyanDark.copy(alpha = 0.4f)
                        )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (currentScreen) {
                    Screen.LOGIN -> LoginScreen(viewModel = viewModel)
                    Screen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    Screen.RISK_RADAR -> RiskRadarScreen(viewModel = viewModel)
                    Screen.TIMELINE -> TimelineScreen(viewModel = viewModel)
                    Screen.AI_ANALYST -> AiAnalystScreen(viewModel = viewModel)
                    Screen.FINDINGS -> FindingsScreen(viewModel = viewModel)
                    Screen.CONTROLS -> ControlsScreen(viewModel = viewModel)
                    Screen.REMEDIATION -> RemediationScreen(viewModel = viewModel)
                    Screen.EVIDENCE -> EvidenceScreen(viewModel = viewModel)
                    Screen.POLICY_IMPACT -> PolicyImpactScreen(viewModel = viewModel)
                    Screen.AUDIT_PREP -> AuditPrepScreen(viewModel = viewModel)
                    Screen.MEMORY_HUB -> MemoryHubScreen(viewModel = viewModel)
                    Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }

    // "WHY?" Institutional Reasoning Modal Dialog
    if (whyState.isOpen) {
        WhyReasoningDialog(
            targetName = whyState.targetName,
            finding = whyState.finding,
            decision = whyState.decision,
            remediation = whyState.remediation,
            aiSynthesis = whyState.aiSynthesis,
            reasoningChain = whyState.reasoningChain,
            onDismiss = { viewModel.closeWhyModal() }
        )
    }
}

private data class NavigationItem(
    val title: String,
    val screen: Screen,
    val icon: ImageVector
)
