package com.localai.runtime.session

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/** Token emitted during streaming inference. */
data class InferenceToken(
    val sessionId: String,
    val text: String,
    val index: Int,
    val isFinal: Boolean
)

data class StreamingMetadata(
    val sessionId: String,
    val modelId: String,
    val backendType: String,
    val startedAtMillis: Long = System.currentTimeMillis()
)

enum class CompletionStatus { SUCCESS, CANCELLED, FAILED }

data class StreamingCompletion(
    val sessionId: String,
    val status: CompletionStatus,
    val message: String? = null
)

/** Exposes streaming output as Flow and allows cancellation and completion tracking. */
interface StreamingSession {
    val id: String
    val output: Flow<InferenceToken>
    suspend fun cancel()
    suspend fun awaitCompletion(): StreamingCompletion
}

/** Writer used by backends to emit tokens or signal completion. */
interface StreamingOutputWriter {
    suspend fun emit(token: InferenceToken)
    suspend fun markCompleted(message: String? = null)
    suspend fun markFailed(error: Throwable)
}

/** Default implementation wiring token emission to a shared Flow. */
class DefaultStreamingSession(
    override val id: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : StreamingSession, StreamingOutputWriter {
    private val _tokens = MutableSharedFlow<InferenceToken>(extraBufferCapacity = 32)
    private val completion = CompletableDeferred<StreamingCompletion>()

    override val output: Flow<InferenceToken> = _tokens.asSharedFlow()

    override suspend fun emit(token: InferenceToken) {
        _tokens.emit(token)
    }

    override suspend fun markCompleted(message: String?) {
        if (!completion.isCompleted) {
            completion.complete(StreamingCompletion(id, CompletionStatus.SUCCESS, message))
        }
    }

    override suspend fun markFailed(error: Throwable) {
        if (!completion.isCompleted) {
            completion.complete(StreamingCompletion(id, CompletionStatus.FAILED, error.message))
        }
    }

    override suspend fun cancel() {
        if (!completion.isCompleted) {
            completion.complete(StreamingCompletion(id, CompletionStatus.CANCELLED, "Cancelled by user"))
        }
    }

    override suspend fun awaitCompletion(): StreamingCompletion = completion.await()

    fun launchBackground(block: suspend StreamingOutputWriter.() -> Unit) {
        scope.launch { block(this@DefaultStreamingSession) }
    }
}
