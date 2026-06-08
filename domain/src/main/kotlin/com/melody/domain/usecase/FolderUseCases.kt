package com.melody.domain.usecase

import com.melody.domain.model.FolderNode
import com.melody.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val repository: SongRepository
) {
    operator fun invoke(): Flow<List<FolderNode>> = repository.getFolders()
}

class PinFolderUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(folderPath: String, pin: Boolean) {
        repository.pinFolder(folderPath, pin)
    }
}

class ExcludeFolderUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(folderPath: String, exclude: Boolean) {
        repository.excludeFolder(folderPath, exclude)
    }
}
