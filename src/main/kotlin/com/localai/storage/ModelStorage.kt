package com.localai.storage

import com.localai.runtime.inference.BackendType
import com.localai.runtime.inference.ModelDescriptor

/** Represents a model persisted locally with metadata for attribution and integrity. */
data class StoredModel(
    val id: String,
    val descriptor: ModelDescriptor,
    val checksum: String,
    val license: String,
    val lastVerifiedMillis: Long
)

/** Interface for reading/writing model binaries and metadata. */
interface ModelStorage {
    suspend fun persistModel(source: StorageSource): StoredModel
    suspend fun fetchModel(id: String): StoredModel?
    suspend fun removeModel(id: String): Boolean
    suspend fun availableBytes(): Long
}

/**
 * Metadata-only store for quick lookups that can power UI listings without touching disk.
 */
interface ModelMetadataStore {
    suspend fun upsert(model: StoredModel)
    suspend fun list(): List<StoredModel>
    suspend fun markVerified(id: String, checksum: String)
}

/** Exposes diagnostic data related to storage usage and integrity checks. */
interface StorageDiagnostics {
    fun reportLowSpace(thresholdBytes: Long): Boolean
    fun lastIntegrityCheck(modelId: String): Long?
}

sealed class StorageSource {
    data class Downloaded(val id: String, val path: String, val checksum: String) : StorageSource()
    data class Imported(val uri: String, val license: String) : StorageSource()
}

class FileModelStorage : ModelStorage, ModelMetadataStore, StorageDiagnostics {
    private val inMemory = mutableMapOf<String, StoredModel>()

    override suspend fun persistModel(source: StorageSource): StoredModel {
        val stored = when (source) {
            is StorageSource.Downloaded -> StoredModel(
                id = source.id,
                descriptor = ModelDescriptor(
                    source.id,
                    format = "gguf",
                    path = source.path,
                    preferredBackend = BackendType.NDK,
                    enforceBackgroundExecution = true
                ),
                checksum = source.checksum,
                license = "",
                lastVerifiedMillis = System.currentTimeMillis()
            )
            is StorageSource.Imported -> StoredModel(
                id = source.uri,
                descriptor = ModelDescriptor(
                    source.uri,
                    format = "unknown",
                    path = source.uri,
                    preferredBackend = BackendType.NDK,
                    enforceBackgroundExecution = false
                ),
                checksum = "pending",
                license = source.license,
                lastVerifiedMillis = 0L
            )
        }
        inMemory[stored.id] = stored
        return stored
    }

    override suspend fun fetchModel(id: String): StoredModel? = inMemory[id]

    override suspend fun removeModel(id: String): Boolean = inMemory.remove(id) != null

    override suspend fun availableBytes(): Long = Long.MAX_VALUE

    override suspend fun upsert(model: StoredModel) {
        inMemory[model.id] = model
    }

    override suspend fun list(): List<StoredModel> = inMemory.values.toList()

    override suspend fun markVerified(id: String, checksum: String) {
        inMemory[id]?.let { existing ->
            inMemory[id] = existing.copy(checksum = checksum, lastVerifiedMillis = System.currentTimeMillis())
        }
    }

    override fun reportLowSpace(thresholdBytes: Long): Boolean = false

    override fun lastIntegrityCheck(modelId: String): Long? = inMemory[modelId]?.lastVerifiedMillis
}
