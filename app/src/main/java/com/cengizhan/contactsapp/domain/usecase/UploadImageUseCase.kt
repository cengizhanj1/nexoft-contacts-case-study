package com.cengizhan.contactsapp.domain.usecase

import com.cengizhan.contactsapp.domain.repository.ContactsRepository

class UploadImageUseCase(
    private val repository: ContactsRepository
) {
    /**
     * @param imageBytes  -> seçilen görselin byte dizisi
     * @param fileName    -> örn. "avatar.jpg"
     * @return server'dan dönen tam imageUrl
     */
    suspend operator fun invoke(
        imageBytes: ByteArray,
        fileName: String
    ): String {
        return repository.uploadImage(imageBytes, fileName)
    }
}
