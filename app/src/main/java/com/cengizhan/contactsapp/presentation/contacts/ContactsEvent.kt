package com.cengizhan.contactsapp.presentation.contacts

import com.cengizhan.contactsapp.domain.model.Contact

sealed class ContactsEvent {

    // Kişileri API'den çek
    object LoadContacts : ContactsEvent()

    // Kişi sil
    data class DeleteContact(val userId: String) : ContactsEvent()

    // Kişi güncelle
    data class UpdateContact(val contact: Contact) : ContactsEvent()

    // Arama kutusu değiştiğinde
    data class SearchQueryChanged(val query: String) : ContactsEvent()

    // Arama geçmişini tamamen temizle
    object ClearSearchHistory : ContactsEvent()
}
