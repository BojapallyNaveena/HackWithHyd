# AuditMind AI
### *"The AI compliance agent that remembers why."*

> Turn years of audit history, risk decisions, evidence, and remediation commitments into persistent institutional intelligence.

---

## 1. Problem
Organizations repeatedly face critical compliance questions across multi-year audit cycles:
- *"Have we seen this access-control issue before?"*
- *"Why was this risk accepted last year?"*
- *"Who owned the remediation and was it actually completed?"*
- *"Did the same control fail in previous audits?"*
- *"What changed since our last audit?"*
- *"Which controls deserve investigation first?"*

Traditional compliance and GRC software stores static documents, PDF reports, and checklist rows. When compliance personnel or auditors leave, institutional knowledge is permanently lost. New audits fail on the exact same controls that were previously given temporary risk waivers or unverified remediation commitments.

---

## 2. Solution: AuditMind AI
AuditMind AI is an autonomous Compliance & Audit Intelligence Agent powered by **Hindsight persistent memory**. Instead of treating past audits as dead documents, AuditMind remembers the historical context, management decisions, compensating controls, and temporal evolution behind every finding and control.

### Core Value Drivers:
1. **Recurrence Detection**: Discovers systemic control failures across 2024, 2025, and 2026.
2. **"WHY?" Action**: Reconstructs the historical reasoning behind past risk acceptances, differentiating hard historical facts from AI interpretation.
3. **Audit Memory Timeline**: Visually traces control lifecycles from Discovery → Risk Acceptance → Remediation Promise → Deadline Missed → Recurrence Alert.
4. **Memory Transparency**: Exposes exact Hindsight memory sources, tags, confidence levels, and temporal evidence chains for every insight.
5. **Policy Change Impact Analyzer**: Forecasts affected controls and previous findings when regulatory frequencies tighten (e.g. 12-month to 6-month access reviews).
6. **Actionable Pre-Audit Preparation**: Generates prioritized investigation checklists for upcoming field audits.

---

## 3. Why Hindsight is Central (Not Just a Vector DB)

AuditMind **visibly depends on Hindsight’s 3 core primitives**:

```
 ┌────────────────────────────────────────────────────────────────────────┐
 │                     HINDSIGHT PERSISTENT MEMORY                        │
 ├──────────────────┬──────────────────────┬──────────────────────────────┤
 │     RETAIN       │        RECALL        │           REFLECT            │
 ├──────────────────┼──────────────────────┼──────────────────────────────┤
 │ Extracts durable │ Multi-dimensional    │ Synthesizes root causes      │
 │ facts, entities, │ semantic, keyword,   │ across multi-year audit      │
 │ tags, decisions, │ tag, temporal, and   │ timelines rather than simple │
 │ and deadlines    │ graph retrieval      │ chunk matching               │
 └──────────────────┴──────────────────────┴──────────────────────────────┘
```

- **RETAIN**: Continuous capture of audit findings, control descriptions, management waivers, compensating controls, and evidence references tagged by audit year (`audit:2024`, `audit:2025`, `audit:2026`), entity, and department.
- **RECALL**: Multi-dimensional retrieval filtering across time, control IDs, and entities before answering auditor queries.
- **REFLECT**: Deep synthesis answering complex questions such as *"Why does this bank repeatedly fail access-control audits?"* by connecting management waivers with postponed cloud migrations and departed personnel.

### The Memory Mission:
> *"Regulatory requirements, audit findings, control failures, remediation commitments, deadlines, responsible entities, management decisions, evidence, recurring risks, relationships between controls and findings, historical changes, and the reasoning behind compliance decisions. Preserve temporal context and distinguish historical facts from current status."*

---

## 4. Architecture Diagram

```
                    ┌──────────────────────────────────────────┐
                    │        Android Jetpack Compose UI        │
                    │   Dashboard • Risk Radar • Timeline      │
                    │   Findings • Controls • AI Analyst       │
                    └────────────────────┬─────────────────────┘
                                         │
                                         ▼
                    ┌──────────────────────────────────────────┐
                    │            AuditMind Agent               │
                    │   • Multi-Turn Reasoning (Thinking Mode) │
                    │   • Tool Calling & Grounding Engine      │
                    │   • Generic vs Memory-Aware Comparator   │
                    └────────────────────┬─────────────────────┘
                                         │
                 ┌───────────────────────┼───────────────────────┐
                 │                       │                       │
                 ▼                       ▼                       ▼
    ┌─────────────────────────┐  ┌──────────────┐  ┌─────────────────────────┐
    │   Hindsight Memory      │  │  LLM Engine  │  │  Local Room Persistence │
    │   • Retain Primitive    │  │  • Gemini    │  │  • 36 Findings          │
    │   • Recall Primitive    │  │    3.5 Flash │  │  • 12 Controls          │
    │   • Reflect Primitive   │  │  • Groq API  │  │  • Remediations & Logs  │
    │   • Evidence Chain      │  │  • Thinking  │  │  • Decisions & Evidence │
    └─────────────────────────┘  └──────────────┘  └─────────────────────────┘
```

---

## 5. Multi-Year Demo Dataset: NovaBank (2024 - 2026)

The application includes a realistic 3-year enterprise dataset with 36 findings, 12 controls, remediation commitments, and executive waivers:

### Primary Recurrence Chains:
1. **Control C-17 (Privileged Access Management & Bastion MFA)**:
   - **2024 (F-104)**: Root DB bastion lacked MFA. CISO Marcus Vance accepted risk temporarily (DEC-2024-01) due to scheduled cloud migration ("Project Horizon"). Compensating control: Weekly manual review.
   - **2025 (F-204)**: Remediation R-22 stalled (CyberArk failed on AIX). Manual reviews ceased after analyst left. Second waiver (EW-2025-04) granted.
   - **2026 (F-301)**: Triple recurrence flagged by AuditMind. 89 unauthenticated root logins in 2026.
2. **Control C-03 (Quarterly User Access & Orphaned Contractor Accounts)**:
   - 42 accounts (2024) → 28 accounts (2025) → 35 accounts (2026) due to postponed Okta SCIM integration.
3. **Control C-05 (Vendor Risk Assessment & SOC 2 Reviews)**:
   - 8 unreviewed vendors (2024) → 14 (2025) → 19 (2026) after vendor portal canceled due to budget cuts.
4. **Control C-09 (PII & Transaction Data Retention)**:
   - 4.2M records (2024) → 5.8M (2025) → 6.94M (2026) unpurged due to foreign key lock crashes in cron job.

---

## 6. 60-Second Demo Walkthrough

1. **Scene 1 — The Memory Contrast**:
   - Tap **"Query: Is this access-control finding new?"**
   - Toggle **Without Memory**: Generic AI returns *"This appears to be an access-control issue. More historical context is needed."*
   - Toggle **With AuditMind Memory**: Agent returns *"This issue is NOT new. It has appeared in 2024 and 2025 audits. The 2025 remediation commitment was not fully verified, and the control remains unresolved."*
2. **Scene 2 — The "WHY?" Action**:
   - Tap **"WHY? Reconstruct Historical Reasoning"** on Control C-17.
   - AuditMind reconstructs the full evidence chain: Original Finding → Management Rationale → Compensating Control → Remediation Deadline → Operational Reality.
3. **Scene 3 — Audit Memory Timeline**:
   - Inspect the visual chronological step nodes (2024 → 2025 → 2026).
4. **Scene 4 — Policy Change Simulator**:
   - Test changing access review frequency from 12 months to 6 months to forecast affected historical findings.
5. **Scene 5 — Actionable Investigation Plan**:
   - Tap **"Prepare Investigation Plan"** to generate tomorrow's field audit inspection checklist.

---

## 7. Technology Stack
- **Frontend / Platform**: Android Jetpack Compose, Kotlin 2.2, Material Design 3 (M3).
- **Local Persistence**: Android Room Database with KSP and SQLite reactive `Flow`.
- **Memory Intelligence**: Hindsight Persistent Memory primitives (Retain, Recall, Reflect).
- **AI Models**: Google Gemini 3.5 Flash & Gemini 3.1 Pro Preview with High Thinking Mode; optional Groq API support.
- **Networking**: Retrofit 2 & OkHttp 4 with 60-second timeouts.

---

## 8. Setup Instructions

1. Configure secrets via `.env` (or the AI Studio Secrets panel):
   ```
   GEMINI_API_KEY=your_gemini_api_key_here
   HINDSIGHT_API_KEY=optional_hindsight_cloud_key
   HINDSIGHT_ENDPOINT=https://api.hindsight.cloud
   ```
2. Build and launch:
   ```bash
   gradle assembleDebug
   ```
3. Tap **"Settings & Demo"** → **"Reseed Data"** to reload NovaBank's multi-year audit baseline at any time.
