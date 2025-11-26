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
