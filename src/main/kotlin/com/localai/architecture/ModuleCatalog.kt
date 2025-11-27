package com.localai.architecture

/**
 * Describes the modular decomposition of the Local AI app.
 * Each module lists responsibilities and the primary interfaces that define its boundary.
 */
data class ModuleSpecification(
    val name: String,
    val responsibilities: List<String>,
    val contracts: List<String>,
    val primaryInterfaces: List<String>
)

object ModuleCatalog {
    fun modules(): List<ModuleSpecification> = listOf(
        ModuleSpecification(
            name = "UI",
            responsibilities = listOf(
                "Render chat, summarization, and model management flows",
                "Emit events for prompts, cancellations, and file imports",
                "Show streaming output, progress, and errors from runtime services"
            ),
            contracts = listOf(
                "Consumes UiState snapshots from domain",
                "Delegates prompt execution and cancellation to the UseCaseDispatcher",
                "Shows background execution status from DiagnosticsReporter"
            ),
            primaryInterfaces = listOf("UiEventSink", "UiStatePresenter", "UiStateRenderer")
        ),
        ModuleSpecification(
            name = "Domain / Use-cases",
            responsibilities = listOf(
                "Owns prompt/session lifecycle and maps UI intents to runtime requests",
                "Orchestrates retries, token streaming, and cancellation semantics",
                "Translates storage/networking responses into user-ready state"
            ),
            contracts = listOf(
                "Calls InferenceRuntime for execution",
                "Persists session summaries via ModelStorage",
                "Requests downloads and updates through ModelCatalogClient"
            ),
            primaryInterfaces = listOf("UseCaseDispatcher", "UseCaseExecutor", "UseCaseObserver")
        ),
        ModuleSpecification(
            name = "Runtime Service",
            responsibilities = listOf(
                "Owns inference session scheduling and background execution",
                "Streams incremental tokens to observers and honors cancellation",
                "Selects inference backend based on capability and user preference"
            ),
            contracts = listOf(
                "Accepts InferenceRequest and returns StreamingSession",
                "Chooses between NNAPI, GPU, or NDK backends",
                "Delegates token emission to StreamingOutputWriter implementations"
            ),
            primaryInterfaces = listOf("InferenceRuntime", "InferenceBackend", "StreamingSession")
        ),
        ModuleSpecification(
            name = "Storage",
            responsibilities = listOf(
                "Persists model metadata, checkpoints, and attribution",
                "Manages disk quotas and background cleanup",
                "Exposes integrity results and cached assets to domain layer"
            ),
            contracts = listOf(
                "Provides model paths to runtime services",
                "Stores provenance and licensing for imported models",
                "Surfaces diagnostics about space usage to UI"
            ),
            primaryInterfaces = listOf("ModelStorage", "ModelMetadataStore", "StorageDiagnostics")
        ),
        ModuleSpecification(
            name = "Networking",
            responsibilities = listOf(
                "Fetches catalog metadata, model binaries, and update manifests",
                "Supports resumable downloads with checksum verification",
                "Reports progress for UI and telemetry for diagnostics"
            ),
            contracts = listOf(
                "Notifies Storage about completed downloads",
                "Accepts cancellation from the domain layer",
                "Provides cached catalog data for offline usage"
            ),
            primaryInterfaces = listOf("ModelCatalogClient", "DownloadService", "NetworkReachability")
        ),
        ModuleSpecification(
            name = "Diagnostics",
            responsibilities = listOf(
                "Captures latency, errors, and device capability signals",
                "Provides human-readable health summaries for UI",
                "Aggregates per-backend performance for routing decisions"
            ),
            contracts = listOf(
                "Receives events from runtime, storage, and networking",
                "Surfaces alerts back to UI and domain",
                "Publishes lightweight traces for debugging"
            ),
            primaryInterfaces = listOf("DiagnosticsReporter", "PerformanceTrace", "LogSink")
        )
    )
}
