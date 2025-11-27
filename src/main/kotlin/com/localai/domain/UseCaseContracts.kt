package com.localai.domain

import com.localai.runtime.InferenceRequest
import com.localai.runtime.InferenceRuntime
import com.localai.runtime.session.StreamingSession

/** Identifies a user-facing flow such as chat, summarization, or import/export. */
data class UseCaseId(val value: String)

/**
 * Contract representing a request from the UI layer. It can be fulfilled by an inference session
 * or a storage/networking interaction depending on the use case.
 */
data class UseCaseRequest(
    val id: UseCaseId,
    val request: InferenceRequest,
    val allowBackgroundExecution: Boolean,
    val clientTag: String
)

/** Captures high-level progress used to populate UI state. */
data class UseCaseProgress(
    val sessionId: String,
    val status: ProgressStatus,
    val message: String? = null
)

enum class ProgressStatus { STARTED, STREAMING, COMPLETED, FAILED, CANCELLED }

/** Observer that can subscribe to domain progress updates. */
interface UseCaseObserver {
    fun onProgress(progress: UseCaseProgress)
}

/**
 * Executes a request and surfaces streaming tokens plus cancellation semantics.
 * Implementations will translate from domain types into runtime-level InferenceRequest objects.
 */
interface UseCaseExecutor {
    fun execute(request: UseCaseRequest, observer: UseCaseObserver? = null): StreamingSession
    suspend fun cancel(sessionId: String)
}

/**
 * Dispatcher that routes UI intents to the appropriate executor while keeping the UI layer
 * free of implementation details.
 */
class UseCaseDispatcher(private val runtime: InferenceRuntime) : UseCaseExecutor {
    private val observers = mutableSetOf<UseCaseObserver>()

    fun addObserver(observer: UseCaseObserver) {
        observers += observer
    }

    override fun execute(request: UseCaseRequest, observer: UseCaseObserver?): StreamingSession {
        observer?.let { observers += it }
        observers.forEach { it.onProgress(UseCaseProgress(request.request.sessionId, ProgressStatus.STARTED)) }
        val session = runtime.startStreaming(request.request)
        observers.forEach { it.onProgress(UseCaseProgress(request.request.sessionId, ProgressStatus.STREAMING)) }
        return session
    }

    override suspend fun cancel(sessionId: String) {
        runtime.cancel(sessionId)
        observers.forEach { it.onProgress(UseCaseProgress(sessionId, ProgressStatus.CANCELLED)) }
    }
}
