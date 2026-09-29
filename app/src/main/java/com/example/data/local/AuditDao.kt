package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditDao {
    // Findings
    @Query("SELECT * FROM findings ORDER BY auditYear DESC, id ASC")
    fun getAllFindings(): Flow<List<FindingEntity>>

    @Query("SELECT * FROM findings WHERE auditYear = :year ORDER BY id ASC")
    fun getFindingsByYear(year: Int): Flow<List<FindingEntity>>

    @Query("SELECT * FROM findings WHERE isRecurring = 1 ORDER BY auditYear DESC")
    fun getRecurringFindings(): Flow<List<FindingEntity>>

    @Query("SELECT * FROM findings WHERE controlId = :controlId ORDER BY auditYear ASC")
    suspend fun getFindingsForControl(controlId: String): List<FindingEntity>

    @Query("SELECT * FROM findings WHERE id = :id")
    suspend fun getFindingById(id: String): FindingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFindings(findings: List<FindingEntity>)

    // Controls
    @Query("SELECT * FROM controls ORDER BY recurrenceRiskLevel DESC, id ASC")
    fun getAllControls(): Flow<List<ControlEntity>>

    @Query("SELECT * FROM controls WHERE id = :id")
    suspend fun getControlById(id: String): ControlEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertControls(controls: List<ControlEntity>)

    // Remediations
    @Query("SELECT * FROM remediations ORDER BY isOverdue DESC, deadline ASC")
    fun getAllRemediations(): Flow<List<RemediationEntity>>

    @Query("SELECT * FROM remediations WHERE isOverdue = 1")
    fun getOverdueRemediations(): Flow<List<RemediationEntity>>

    @Query("SELECT * FROM remediations WHERE controlId = :controlId")
    suspend fun getRemediationsForControl(controlId: String): List<RemediationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemediations(remediations: List<RemediationEntity>)

    // Evidence
    @Query("SELECT * FROM evidence ORDER BY year DESC")
    fun getAllEvidence(): Flow<List<EvidenceEntity>>

    @Query("SELECT * FROM evidence WHERE findingId = :findingId OR controlId = :controlId")
    suspend fun getEvidenceForFindingOrControl(findingId: String?, controlId: String?): List<EvidenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidence(evidenceList: List<EvidenceEntity>)

    // Decisions
    @Query("SELECT * FROM decisions ORDER BY year DESC")
    fun getAllDecisions(): Flow<List<DecisionEntity>>

    @Query("SELECT * FROM decisions WHERE controlId = :controlId OR findingId = :findingId")
    suspend fun getDecisionsForFindingOrControl(findingId: String?, controlId: String?): List<DecisionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecisions(decisions: List<DecisionEntity>)

    // Hindsight Memories
    @Query("SELECT * FROM hindsight_memories ORDER BY temporalYear DESC, timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM hindsight_memories WHERE text LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR entities LIKE '%' || :query || '%'")
    suspend fun recallMemoriesByKeyword(query: String): List<MemoryEntity>

    @Query("SELECT * FROM hindsight_memories WHERE temporalYear = :year")
    suspend fun recallMemoriesByYear(year: Int): List<MemoryEntity>

    @Query("SELECT * FROM hindsight_memories WHERE controlId = :controlId OR findingId = :findingId")
    suspend fun recallMemoriesByTarget(controlId: String?, findingId: String?): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<MemoryEntity>)

    @Query("DELETE FROM findings")
    suspend fun clearFindings()

    @Query("DELETE FROM controls")
    suspend fun clearControls()

    @Query("DELETE FROM remediations")
    suspend fun clearRemediations()

    @Query("DELETE FROM evidence")
    suspend fun clearEvidence()

    @Query("DELETE FROM decisions")
    suspend fun clearDecisions()

    @Query("DELETE FROM hindsight_memories")
    suspend fun clearMemories()
}
