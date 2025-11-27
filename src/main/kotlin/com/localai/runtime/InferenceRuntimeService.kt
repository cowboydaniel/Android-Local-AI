package com.localai.runtime

import com.localai.runtime.inference.BackendType
import com.localai.runtime.inference.InferenceBackend
import com.localai.runtime.inference.ModelDescriptor
import com.localai.runtime.session.DefaultStreamingSession
import com.localai.runtime.session.StreamingSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/** Parameters required to start an inference request. */
data class InferenceRequest(
    val sessionId: String,
    val prompt: String,
    val modelId: String,
    val preferredBackend: BackendType,
    val allowBackgroundExecution: Boolean = true,
    val clientTag: String = "ui"
)

/** Interface used by the domain layer to start or cancel inference sessions. */
interface InferenceRuntime {
    fun startStreaming(request: InferenceRequest): StreamingSession
    suspend fun cancel(sessionId: String)
}

/**
 * Default implementation that selects a backend and launches the job in a background scope.
 * Actual backend invocation is delegated to backend-specific implementations.
 */
class InferenceRuntimeService(
    private val backends: Map<BackendType, InferenceBackend>,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : InferenceRuntime {

    override fun startStreaming(request: InferenceRequest): StreamingSession {
        val backend = backends[request.preferredBackend]
            ?: error("Backend ${request.preferredBackend} not registered")
        val session = DefaultStreamingSession(request.sessionId)
        session.launchBackground {
            val descriptor = ModelDescriptor(
                id = request.modelId,
                format = "gguf",
                path = "", // to be provided by Storage once wired
                preferredBackend = request.preferredBackend,
                enforceBackgroundExecution = request.allowBackgroundExecution
            )
            val handle = backend.initialize(descriptor)
            backend.warmUp(handle)
            backend.execute(request, handle, this)
            markCompleted("Streaming finished for ${request.sessionId}")
        }
        return session
    }

    override suspend fun cancel(sessionId: String) {
        // Backends can implement cooperative cancellation. For now we rely on the session handle.
        // In a concrete implementation this would propagate a cancellation token.
    }
}
