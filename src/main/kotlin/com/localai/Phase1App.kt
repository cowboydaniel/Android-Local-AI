package com.localai

/**
 * Entry point for phase 1 deliverables of the on-device AI Android app.
 * It captures product definition and UX foundations as structured Kotlin data.
 */
fun main() {
    val phase1 = Phase1Roadmap.definitions()
    phase1.personas.forEach { persona ->
        println("Persona: ${persona.name} — ${persona.deviceProfile}")
    }
    println("\nUse cases and success metrics:")
    phase1.useCases.forEach { useCase ->
        println("- ${useCase.name}: ${useCase.description}")
        useCase.successMetrics.forEach { metric ->
            println("  • $metric")
        }
    }
    println("\nFirst-run experience:")
    phase1.firstRunExperience.stages.forEach { stage ->
        println("- ${stage.name}: ${stage.description}")
        stage.steps.forEach { step -> println("  • $step") }
    }
    println("\nCompose flows:")
    phase1.composeFlows.forEach { flow ->
        println("- ${flow.flowName}: ${flow.description}")
        flow.screens.forEach { screen -> println("  • $screen") }
    }

    val phase2 = Phase2Roadmap.definitions()
    println("\nPhase 2: Model Strategy & Packaging Rules")
    println("Supported formats and tiers:")
    phase2.modelStrategy.formats.forEach { format ->
        println("- ${format.name}: optimized for ${format.optimizedFor.joinToString()}")
        format.recommendedTiers.forEach { tier ->
            println("  • ${tier.tierName}: ${tier.parameters} params, ${tier.expectedLatency}, ${tier.storageFootprint}")
        }
    }
    println("\nPackaging rules:")
    phase2.packagingRules.bundledModels.forEach { model ->
        println("- Bundled: ${model.id} (${model.format}) → ${model.deliveryMode.description}")
    }
    phase2.packagingRules.onDemandModels.forEach { model ->
        println("- Downloaded: ${model.id} (${model.format}) → ${model.deliveryMode.description}")
    }
    println("Storage plan: ${phase2.packagingRules.storagePlan.appAssetsPath}, ${phase2.packagingRules.storagePlan.onDemandPath}, ${phase2.packagingRules.storagePlan.userImportPath}")
    println("Integrity checks: ${phase2.packagingRules.integrityChecks.joinToString()}")

    println("\nImport/export policy:")
    println("Allowed formats: ${phase2.importExportPolicy.allowedFormats.joinToString()}")
    phase2.importExportPolicy.validationSteps.forEach { step -> println("- $step") }
    println("Licensing rules: ${phase2.importExportPolicy.licensingRules.joinToString()}")
    println("Attribution stored in ${phase2.importExportPolicy.attributionStorage.manifestFile}")
}

/**
 * Container for the phase 1 product definition and UX foundations.
 */
data class Phase1Roadmap(
    val personas: List<UserPersona>,
    val useCases: List<UseCase>,
    val firstRunExperience: FirstRunExperience,
    val composeFlows: List<ComposeFlow>
) {
    companion object {
        fun definitions(): Phase1Roadmap = Phase1Roadmap(
            personas = defaultPersonas(),
            useCases = coreUseCases(),
            firstRunExperience = firstRunExperience(),
            composeFlows = composeFlows()
        )

        private fun defaultPersonas(): List<UserPersona> = listOf(
            UserPersona(
                name = "Privacy-Focused Traveler",
                goals = listOf("Use chat and translation offline", "Summarize notes and tickets"),
                frustrations = listOf("Unreliable roaming connectivity", "Data usage caps"),
                deviceProfile = "Mid-range device, prefers offline-first and battery-friendly experiences"
            ),
            UserPersona(
                name = "Field Researcher",
                goals = listOf("Capture voice notes", "Generate quick image captions", "Organize findings on-device"),
                frustrations = listOf("No trust in cloud uploads", "Sparse connectivity"),
                deviceProfile = "Rugged device with ample storage; values integrity checks and auditability"
            ),
            UserPersona(
                name = "Everyday Helper",
                goals = listOf("Daily chat assistant", "Summaries of articles", "Voice interactions"),
                frustrations = listOf("Doesn't want accounts", "Needs low-latency responses"),
                deviceProfile = "Modern consumer phone with GPU delegate availability"
            )
        )

        private fun coreUseCases(): List<UseCase> = listOf(
            UseCase(
                name = "Chat",
                description = "Conversational assistant with short-memory recall and prompt presets",
                successMetrics = listOf(
                    "<1.5s first token on mid-tier devices",
                    "No outbound network during inference",
                    "Clear streaming progress indicator"
                )
            ),
            UseCase(
                name = "Summarization",
                description = "On-device summarization for clipboard, files, and shared text",
                successMetrics = listOf(
                    "Handles 4–8k token inputs with graceful truncation messaging",
                    "Editable summary templates and retry affordances",
                    "Completes under 10s for 2k token inputs on mid-tier devices"
                )
            ),
            UseCase(
                name = "Image/Voice Tasks",
                description = "Offline captioning, transcription, and lightweight vision QA",
                successMetrics = listOf(
                    "Local processing only with explicit user consent for microphone/camera",
                    "Retrial flows for noisy audio with suggested fixes",
                    "Battery-aware capture with timeout and cancel controls"
                )
            )
        )

        private fun firstRunExperience(): FirstRunExperience = FirstRunExperience(
            stages = listOf(
                FirstRunStage(
                    name = "Permissions & Expectations",
                    description = "Explain offline inference, request microphone/camera only when needed",
                    steps = listOf(
                        "Show offline-first value prop and privacy stance",
                        "Gate mic/camera permissions behind clear calls-to-action",
                        "Offer help link and skip option"
                    )
                ),
                FirstRunStage(
                    name = "Model Selection",
                    description = "Guide users to a size-tiered model with storage and latency hints",
                    steps = listOf(
                        "Detect device class and suggest default tier",
                        "Display size, expected latency, and storage impact",
                        "Allow manual override and change later in settings"
                    )
                ),
                FirstRunStage(
                    name = "Download Consent",
                    description = "Confirm network preferences and space requirements before fetching",
                    steps = listOf(
                        "Offer Wi‑Fi only vs. cellular toggle with estimated time",
                        "Surface storage requirement and space check",
                        "Provide cancel, pause, and resume entry points"
                    )
                ),
                FirstRunStage(
                    name = "Offline Expectations",
                    description = "Set clear behavior for offline usage and updates",
                    steps = listOf(
                        "Explain what works without connectivity",
                        "Describe how updates and integrity checks run",
                        "Link to settings for data and telemetry controls"
                    )
                )
            )
        )

        private fun composeFlows(): List<ComposeFlow> = listOf(
            ComposeFlow(
                flowName = "Home",
                description = "Entry point with chat shortcuts, recent summaries, and quick mic access",
                screens = listOf(
                    "Conversation list with pinned prompts",
                    "Floating action button for new chat or capture",
                    "Status pill for selected model and connectivity"
                )
            ),
            ComposeFlow(
                flowName = "Model Management",
                description = "Model catalog, downloads, imports, and storage diagnostics",
                screens = listOf(
                    "Model picker by tier with latency/storage hints",
                    "Download queue with pause/resume and checksum states",
                    "Import flow with license acknowledgment"
                )
            ),
            ComposeFlow(
                flowName = "Inference Session",
                description = "Streaming chat and summarization experiences",
                screens = listOf(
                    "Message thread with streaming tokens and cancel",
                    "Context length indicator and retry controls",
                    "Audio input with waveform, levels, and noise hints"
                )
            ),
            ComposeFlow(
                flowName = "Settings",
                description = "Privacy, telemetry, model defaults, and offline help",
                screens = listOf(
                    "Privacy controls with on-device only toggle",
                    "Network preferences and download consent",
                    "Help & FAQ with offline availability messaging"
                )
            )
        )
    }
}

/** Persona targeted by the app, including needs and constraints. */
data class UserPersona(
    val name: String,
    val goals: List<String>,
    val frustrations: List<String>,
    val deviceProfile: String
)

/** Core use case with associated success metrics. */
data class UseCase(
    val name: String,
    val description: String,
    val successMetrics: List<String>
)

/** First-run experience definition. */
data class FirstRunExperience(
    val stages: List<FirstRunStage>
)

/** One stage in onboarding with granular steps. */
data class FirstRunStage(
    val name: String,
    val description: String,
    val steps: List<String>
)

/** Jetpack Compose navigation flow with key screens. */
data class ComposeFlow(
    val flowName: String,
    val description: String,
    val screens: List<String>
)

/**
 * Phase 2 roadmap capturing model strategy, packaging, and import/export plans.
 */
data class Phase2Roadmap(
    val modelStrategy: ModelFormatStrategy,
    val packagingRules: PackagingRuleSet,
    val importExportPolicy: ImportExportPolicy
) {
    companion object {
        fun definitions(): Phase2Roadmap = Phase2Roadmap(
            modelStrategy = ModelFormatStrategy(
                formats = listOf(
                    ModelFormatSpec(
                        name = "GGUF",
                        optimizedFor = listOf("CPU-first chat", "Quantized long-context"),
                        recommendedTiers = listOf(
                            ModelSizeTier(
                                tierName = "Entry", parameters = "3B-4B", expectedLatency = "<2.5s first token",
                                storageFootprint = "~1.5–2GB quantized"
                            ),
                            ModelSizeTier(
                                tierName = "Balanced", parameters = "7B", expectedLatency = "~1.5s first token",
                                storageFootprint = "~3.5–4GB quantized"
                            )
                        )
                    ),
                    ModelFormatSpec(
                        name = "TFLite",
                        optimizedFor = listOf("On-device speech", "Low-latency mobile vision"),
                        recommendedTiers = listOf(
                            ModelSizeTier(
                                tierName = "Mobile", parameters = "Base encoder/decoder", expectedLatency = "<1s streaming",
                                storageFootprint = "<500MB with 8-bit quantization"
                            )
                        )
                    ),
                    ModelFormatSpec(
                        name = "ONNX",
                        optimizedFor = listOf("GPU/NNAPI accelerated chat", "Interop with desktop exports"),
                        recommendedTiers = listOf(
                            ModelSizeTier(
                                tierName = "Performance", parameters = "7B-13B", expectedLatency = "<1s first token on high-tier devices",
                                storageFootprint = "6–10GB with weights split"
                            )
                        )
                    )
                )
            ),
            packagingRules = PackagingRuleSet(
                bundledModels = listOf(
                    PackagedModel(
                        id = "starter-chat-gguf", format = "GGUF", deliveryMode = DeliveryMode.APK_ASSET,
                        storageLocation = "assets/models/starter_chat.gguf",
                        rationale = "Guarantees offline chat immediately after install"
                    ),
                    PackagedModel(
                        id = "speech-base-tflite", format = "TFLite", deliveryMode = DeliveryMode.APK_ASSET,
                        storageLocation = "assets/models/speech_base.tflite",
                        rationale = "Keeps speech capture responsive without downloads"
                    )
                ),
                onDemandModels = listOf(
                    PackagedModel(
                        id = "balanced-chat-gguf", format = "GGUF", deliveryMode = DeliveryMode.PLAY_ASSET_DELIVERY,
                        storageLocation = "pad/models/balanced_chat.gguf",
                        rationale = "Reduces base APK size while keeping mid-tier option available"
                    ),
                    PackagedModel(
                        id = "performance-chat-onnx", format = "ONNX", deliveryMode = DeliveryMode.ON_DEMAND_DOWNLOAD,
                        storageLocation = "downloads/models/performance_chat.onnx",
                        rationale = "Large footprint suitable only for capable devices"
                    )
                ),
                storagePlan = StoragePlan(
                    appAssetsPath = "assets/models/", onDemandPath = "Android/data/com.localai/files/models/",
                    userImportPath = "Documents/LocalAI/Imports/", backupPolicy = "Exclude model blobs from cloud backups"
                ),
                integrityChecks = listOf(
                    "SHA-256 checksum verification post-download",
                    "Signature metadata for first-party bundles",
                    "Periodic re-hash on app updates and before inference"
                )
            ),
            importExportPolicy = ImportExportPolicy(
                allowedFormats = listOf(".gguf", ".onnx", ".tflite"),
                validationSteps = listOf(
                    "Confirm extension and magic header matches expected format",
                    "Validate model size against declared tier and available disk",
                    "Store per-file checksum and model provenance",
                    "Run dry-run load to ensure operators are supported before activation"
                ),
                licensingRules = listOf(
                    "Require user acknowledgement for third-party models during import",
                    "Persist license URL/text alongside model metadata",
                    "Block export of models marked as non-redistributable"
                ),
                attributionStorage = AttributionStorage(
                    manifestFile = "metadata/model_manifest.json",
                    requiredFields = listOf("name", "license", "source", "checksum", "format"),
                    retention = "Retain entries for removed models for 30 days for auditability"
                )
            )
        )
    }
}

/** Model formats and their recommended device tiers. */
data class ModelFormatStrategy(
    val formats: List<ModelFormatSpec>
)

/** Description of a model format. */
data class ModelFormatSpec(
    val name: String,
    val optimizedFor: List<String>,
    val recommendedTiers: List<ModelSizeTier>
)

/** Recommended size tier for a device class. */
data class ModelSizeTier(
    val tierName: String,
    val parameters: String,
    val expectedLatency: String,
    val storageFootprint: String
)

/** Packaging strategy for bundled and downloadable models. */
data class PackagingRuleSet(
    val bundledModels: List<PackagedModel>,
    val onDemandModels: List<PackagedModel>,
    val storagePlan: StoragePlan,
    val integrityChecks: List<String>
)

/** One model and its delivery mode. */
data class PackagedModel(
    val id: String,
    val format: String,
    val deliveryMode: DeliveryMode,
    val storageLocation: String,
    val rationale: String
)

/** Storage locations for packaged models. */
data class StoragePlan(
    val appAssetsPath: String,
    val onDemandPath: String,
    val userImportPath: String,
    val backupPolicy: String
)

/** Import/export constraints and licensing requirements. */
data class ImportExportPolicy(
    val allowedFormats: List<String>,
    val validationSteps: List<String>,
    val licensingRules: List<String>,
    val attributionStorage: AttributionStorage
)

/** Location and schema for attribution metadata. */
data class AttributionStorage(
    val manifestFile: String,
    val requiredFields: List<String>,
    val retention: String
)

/** Delivery modes for different model tiers. */
enum class DeliveryMode(val description: String) {
    APK_ASSET("Ships inside APK for guaranteed offline availability"),
    PLAY_ASSET_DELIVERY("Play Asset Delivery split to reduce base install size"),
    ON_DEMAND_DOWNLOAD("Downloaded at runtime with checksum verification")
}
