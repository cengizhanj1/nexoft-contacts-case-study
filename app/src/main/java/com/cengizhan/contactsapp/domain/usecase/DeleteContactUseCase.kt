package com.cengizhan.contactsapp.domain.usecase

import com.cengizhan.contactsapp.domain.repository.ContactsRepository

class DeleteContactUseCase(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteContact(id)
    }
}
