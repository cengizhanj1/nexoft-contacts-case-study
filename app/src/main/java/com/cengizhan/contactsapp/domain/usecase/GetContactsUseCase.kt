package com.cengizhan.contactsapp.domain.usecase

import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.domain.repository.ContactsRepository

class GetContactsUseCase(
    private val repository: ContactsRepository
) {
    suspend operator fun invoke(): List<Contact> {
        return repository.getContacts()
    }
}
