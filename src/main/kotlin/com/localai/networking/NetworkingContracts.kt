package com.localai.networking

import com.localai.storage.StoredModel

/** Reachability abstraction to gate downloads and catalog calls. */
interface NetworkReachability {
    fun isOnline(): Boolean
    fun prefersWifi(): Boolean
}

/** Retrieves catalog metadata and publishes updates to storage. */
interface ModelCatalogClient {
    suspend fun fetchCatalog(): List<ModelManifest>
    suspend fun notifyDownloadComplete(model: StoredModel)
}

/** Handles resumable downloads with integrity hooks. */
interface DownloadService {
    suspend fun queueDownload(request: DownloadRequest, listener: DownloadListener)
    suspend fun cancelDownload(id: String)
}

/** Models a single download item. */
data class DownloadRequest(
    val id: String,
    val url: String,
    val destinationPath: String,
    val checksum: String,
    val allowMetered: Boolean
)

data class ModelManifest(
    val id: String,
    val version: String,
    val downloadUrl: String,
    val license: String,
    val recommendedBackend: String
)

interface DownloadListener {
    fun onProgress(id: String, bytesDownloaded: Long, totalBytes: Long)
    fun onComplete(id: String)
    fun onFailed(id: String, throwable: Throwable)
}
