package com.example.service.export

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AuditExportData(
    val auditYear: Int,
    val auditTitle: String,
    val generatedDate: String,
    val findings: List<AuditFinding>,
    val remediations: List<RemediationCommitment>,
    val decisions: List<AuditDecision>,
    val memories: List<HindsightMemory>,
    val executiveSummary: String
)

data class PdfExportResult(
    val file: File,
    val pageCount: Int,
    val fileSizeFormatted: String,
    val auditYear: Int
)

class AuditPdfExportService(private val context: Context) {

    suspend fun generateAuditPdf(data: AuditExportData): PdfExportResult = withContext(Dispatchers.IO) {
        val document = PdfDocument()

        val pageWidth = 595 // A4 standard pt width at 72dpi
        val pageHeight = 842 // A4 standard pt height
        var pageNumber = 1

        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val fileName = "NovaBank_Audit_Report_${data.auditYear}_${System.currentTimeMillis()}.pdf"
        val outputFile = File(reportsDir, fileName)

        // Paints
        val titlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42) // Slate 900
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.rgb(14, 116, 144) // Cyan 700
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val sectionPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val boldBodyPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val regularBodyPaint = Paint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 9f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val lightDetailPaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 8f
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val bgHeaderPaint = Paint().apply {
            color = Color.rgb(241, 245, 249) // Slate 100
        }

        val criticalBadgePaint = Paint().apply {
            color = Color.rgb(220, 38, 38) // Red 600
        }

        val highBadgePaint = Paint().apply {
            color = Color.rgb(217, 119, 6) // Amber 600
        }

        val goldBadgePaint = Paint().apply {
            color = Color.rgb(180, 83, 9) // Gold/Amber
        }

        val whiteTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240) // Slate 200
            strokeWidth = 1f
        }

        // --- PAGE 1: Executive Overview & Findings ---
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        // Header Background
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, bgHeaderPaint)
        canvas.drawLine(0f, 95f, pageWidth.toFloat(), 95f, linePaint)

        // Header Titles
        canvas.drawText("NOVABANK COMPLIANCE & REGULATORY INTELLIGENCE", 36f, 32f, subtitlePaint)
        canvas.drawText("Executive Audit Report & Memory Justifications: ${data.auditYear}", 36f, 54f, titlePaint)
        canvas.drawText("Generated: ${data.generatedDate} | Classification: STRICTLY CONFIDENTIAL - OCC & SOC 2 COMPLIANCE", 36f, 74f, lightDetailPaint)

        var y = 120f

        // Section: Executive Summary
        canvas.drawText("1. EXECUTIVE SUMMARY & RECURRENCE EXPOSURE", 36f, y, sectionPaint)
        y += 16f
        val summaryLines = wrapText(data.executiveSummary, 520f, regularBodyPaint)
        for (line in summaryLines) {
            canvas.drawText(line, 36f, y, regularBodyPaint)
            y += 13f
        }
        y += 12f

        // Section: Audit Findings
        canvas.drawText("2. VERIFIED AUDIT FINDINGS (${data.findings.size})", 36f, y, sectionPaint)
        y += 16f

        for (finding in data.findings) {
            if (y > pageHeight - 110) {
                // Footer
                drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 45f
            }

            // Finding box background
            canvas.drawRoundRect(36f, y, 559f, y + 54f, 4f, 4f, bgHeaderPaint)

            // Severity Badge
            val badgePaint = if (finding.severity == Severity.CRITICAL) criticalBadgePaint else highBadgePaint
            canvas.drawRoundRect(44f, y + 8f, 100f, y + 22f, 3f, 3f, badgePaint)
            canvas.drawText(finding.severity.name, 48f, y + 18f, whiteTextPaint)

            if (finding.isRecurring) {
                canvas.drawRoundRect(106f, y + 8f, 172f, y + 22f, 3f, 3f, goldBadgePaint)
                canvas.drawText("RECURRING", 110f, y + 18f, whiteTextPaint)
            }

            canvas.drawText("${finding.id} • Control ${finding.controlId}: ${finding.controlName}", 180f, y + 19f, boldBodyPaint)
            canvas.drawText(finding.title, 44f, y + 36f, boldBodyPaint)

            val shortDesc = if (finding.description.length > 105) finding.description.take(105) + "..." else finding.description
            canvas.drawText(shortDesc, 44f, y + 48f, regularBodyPaint)

            y += 62f
        }

        // Section: Remediation Commitments
        y += 8f
        if (y > pageHeight - 130) {
            drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = 45f
        }

        canvas.drawText("3. REMEDIATION COMMITMENTS & OVERDUE ACTIONS", 36f, y, sectionPaint)
        y += 16f

        for (rem in data.remediations) {
            if (y > pageHeight - 90) {
                drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 45f
            }

            val statusStr = if (rem.isOverdue) "[OVERDUE] " else "[IN PROGRESS] "
            val remTitle = "$statusStr${rem.id}: ${rem.title} (${rem.completionPercentage}% Complete)"
            canvas.drawText(remTitle, 36f, y, boldBodyPaint)
            y += 12f
            canvas.drawText("Owner: ${rem.owner} | Committed: ${rem.committedDate} | Deadline: ${rem.deadline}", 46f, y, lightDetailPaint)
            y += 11f
            if (!rem.verificationNotes.isNullOrBlank()) {
                canvas.drawText("Verification Notes: ${rem.verificationNotes}", 46f, y, regularBodyPaint)
                y += 12f
            }
            y += 6f
        }

        // Section: Memory-Backed Historical Justifications & Decisions
        y += 8f
        if (y > pageHeight - 130) {
            drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = 45f
        }

        canvas.drawText("4. HINDSIGHT MEMORY-BACKED JUSTIFICATIONS & WAIVERS", 36f, y, sectionPaint)
        y += 16f

        for (dec in data.decisions) {
            if (y > pageHeight - 110) {
                drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 45f
            }

            canvas.drawText("DECISION ${dec.id} (${dec.year}): ${dec.title}", 36f, y, boldBodyPaint)
            y += 12f
            canvas.drawText("Approved by: ${dec.decisionMaker} | Type: ${dec.decisionType} | Target Control: ${dec.controlId}", 46f, y, lightDetailPaint)
            y += 11f
            canvas.drawText("Stated Rationale: ${dec.rationale}", 46f, y, regularBodyPaint)
            y += 11f
            canvas.drawText("Compensating Control: ${dec.compensatingControl}", 46f, y, regularBodyPaint)
            y += 11f
            canvas.drawText("Operational Reality: ${dec.currentRealStatus}", 46f, y, boldBodyPaint)
            y += 16f
        }

        // Add Memories
        if (data.memories.isNotEmpty()) {
            if (y > pageHeight - 110) {
                drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 45f
            }

            canvas.drawText("5. INSTITUTIONAL MEMORY CITATIONS (${data.memories.size})", 36f, y, sectionPaint)
            y += 16f

            for (mem in data.memories.take(4)) {
                if (y > pageHeight - 65) {
                    drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
                    document.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    y = 45f
                }

                canvas.drawText("• [${mem.temporalYear}] ${mem.memoryType.name}: ${mem.text.take(95)}...", 36f, y, regularBodyPaint)
                y += 13f
            }
        }

        drawFooter(canvas, pageNumber, pageWidth.toFloat(), lightDetailPaint, linePaint)
        document.finishPage(page)

        // Write to File
        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        val fileSizeKb = outputFile.length() / 1024
        val sizeStr = if (fileSizeKb > 1024) "${fileSizeKb / 1024} MB" else "$fileSizeKb KB"

        PdfExportResult(
            file = outputFile,
            pageCount = pageNumber,
            fileSizeFormatted = sizeStr,
            auditYear = data.auditYear
        )
    }

    private fun drawFooter(canvas: android.graphics.Canvas, pageNum: Int, pageWidth: Float, paint: Paint, linePaint: Paint) {
        val y = 815f
        canvas.drawLine(36f, y, pageWidth - 36f, y, linePaint)
        canvas.drawText("NovaBank Internal Audit & Regulatory Compliance Portal — Page $pageNum", 36f, y + 14f, paint)
        canvas.drawText("CONFIDENTIAL & PRIVILEGED", pageWidth - 160f, y + 14f, paint)
    }

    private fun wrapText(text: String, maxWidth: Float, paint: Paint): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    fun sharePdf(file: File, auditYear: Int) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "NovaBank $auditYear Audit Report & Memory Justifications")
            putExtra(
                Intent.EXTRA_TEXT,
                "Attached is the structured audit report for NovaBank ($auditYear), including verified findings, overdue remediations, and Hindsight memory justifications."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share Audit Report PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
