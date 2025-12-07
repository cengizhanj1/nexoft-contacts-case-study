package com.cengizhan.contactsapp.presentation.contacts

import com.cengizhan.contactsapp.domain.model.Contact

data class ContactsUiState(
    val isLoading: Boolean = false,
    val contacts: List<Contact> = emptyList(),
    val error: String? = null,

    // 🔍 Arama
    val searchQuery: String = "",
    val searchHistory: List<String> = emptyList(),

    // 📱 Cihaz rehberinde kayıtlı numaralar (telefon numarası set'i)
    val deviceContacts: Set<String> = emptySet()
)
