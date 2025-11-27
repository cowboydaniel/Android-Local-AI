package com.localai.diagnostics

import com.localai.runtime.inference.BackendType

/** Log destination used for developer-visible traces. */
interface LogSink {
    fun logDebug(message: String)
    fun logError(message: String, throwable: Throwable? = null)
}

/** Provides structured performance measurements per backend and model. */
data class PerformanceTrace(
    val sessionId: String,
    val backendType: BackendType,
    val modelId: String,
    val firstTokenMillis: Long,
    val totalMillis: Long,
    val tokensGenerated: Int
)

/** Reports diagnostics to observers and stores aggregate summaries. */
interface DiagnosticsReporter {
    fun recordTrace(trace: PerformanceTrace)
    fun recordFailure(sessionId: String, backend: BackendType, reason: String)
    fun healthSummary(): HealthSnapshot
}

data class HealthSnapshot(
    val lastSuccessfulBackend: BackendType?,
    val averageFirstTokenMillis: Long,
    val activeAlerts: List<String>
)
