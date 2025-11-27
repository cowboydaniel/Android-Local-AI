package com.localai.runtime.inference

import com.localai.runtime.InferenceRequest
import com.localai.runtime.session.StreamingOutputWriter
import com.localai.runtime.session.StreamingSession

/** Supported execution engines. */
enum class BackendType { NNAPI, GPU, NDK }

/** Metadata describing a model artifact and how it should be executed. */
data class ModelDescriptor(
    val id: String,
    val format: String,
    val path: String,
    val preferredBackend: BackendType,
    val enforceBackgroundExecution: Boolean
)

/** Represents a loaded model bound to a specific backend. */
data class ModelHandle(
    val descriptor: ModelDescriptor,
    val handleId: String,
    val backendType: BackendType
)

/** Base interface for all inference backends. */
interface InferenceBackend {
    val type: BackendType
    suspend fun initialize(model: ModelDescriptor): ModelHandle
    suspend fun warmUp(model: ModelHandle)
    suspend fun execute(request: InferenceRequest, model: ModelHandle, output: StreamingOutputWriter): StreamingSession
    fun supportsBackgroundExecution(): Boolean
}

/** Backend that integrates with Android's Neural Networks API. */
interface NnapiBackend : InferenceBackend {
    val supportedDevices: List<String>
    fun allowsSustainedPerformanceMode(): Boolean
}

/** Backend that runs on GPU delegates (e.g., via TensorFlow Lite or other providers). */
interface GpuBackend : InferenceBackend {
    val supportedPrecisions: List<String>
    fun preferLowPowerMode(): Boolean
}

/** Backend using native (NDK) execution, often CPU-focused. */
interface NativeBackend : InferenceBackend {
    val supportsQuantizationLevels: List<String>
    fun canShareWeightsAcrossProcesses(): Boolean
}
