package com.cengizhan.contactsapp.domain.usecase

import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.domain.repository.ContactsRepository

class UpdateContactUseCase(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(contact: Contact): Contact {
        return repository.updateContact(contact)
    }
}
