package com.localai.ui

import com.localai.domain.UseCaseId
import com.localai.domain.UseCaseRequest
import com.localai.domain.UseCaseDispatcher
import com.localai.domain.ProgressStatus
import com.localai.runtime.InferenceRequest
import com.localai.runtime.inference.BackendType
import com.localai.runtime.session.StreamingSession

/** UI-facing representation of state needed for chat and management screens. */
data class UiState(
    val activeSessionId: String?,
    val lastMessage: String?,
    val progress: ProgressStatus,
    val availableModels: List<String>,
    val diagnosticsSummary: String?
)

interface UiEventSink {
    fun submitPrompt(prompt: String, modelId: String)
    suspend fun cancel(sessionId: String)
}

interface UiStateRenderer {
    fun render(state: UiState)
}

interface UiStatePresenter {
    val dispatcher: UseCaseDispatcher
    fun bind(renderer: UiStateRenderer)
    fun observe(streamingSession: StreamingSession)
}

/**
 * Basic presenter that wires UI events to the use case dispatcher and listens for streaming tokens.
 */
class ChatUiPresenter(override val dispatcher: UseCaseDispatcher) : UiEventSink, UiStatePresenter {
    private var renderer: UiStateRenderer? = null

    override fun bind(renderer: UiStateRenderer) {
        this.renderer = renderer
    }

    override fun submitPrompt(prompt: String, modelId: String) {
        val request = UseCaseRequest(
            id = UseCaseId("chat"),
            request = InferenceRequest(
                sessionId = "session-${System.currentTimeMillis()}",
                prompt = prompt,
                modelId = modelId,
                preferredBackend = BackendType.NDK
            ),
            allowBackgroundExecution = true,
            clientTag = "ui"
        )
        val session = dispatcher.execute(request)
        observe(session)
    }

    override fun observe(streamingSession: StreamingSession) {
        renderer?.render(
            UiState(
                activeSessionId = streamingSession.id,
                lastMessage = null,
                progress = ProgressStatus.STREAMING,
                availableModels = emptyList(),
                diagnosticsSummary = null
            )
        )
    }

    override suspend fun cancel(sessionId: String) {
        dispatcher.cancel(sessionId)
        renderer?.render(
            UiState(
                activeSessionId = sessionId,
                lastMessage = null,
                progress = ProgressStatus.CANCELLED,
                availableModels = emptyList(),
                diagnosticsSummary = null
            )
        )
    }
}
