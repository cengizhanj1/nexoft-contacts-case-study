package com.cengizhan.contactsapp.presentation.contacts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.domain.usecase.DeleteContactUseCase
import com.cengizhan.contactsapp.domain.usecase.GetContactsUseCase
import com.cengizhan.contactsapp.domain.usecase.UpdateContactUseCase
import com.cengizhan.contactsapp.util.DeviceContactsHelper
import kotlinx.coroutines.launch

class ContactsViewModel(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val deviceContactsHelper: DeviceContactsHelper
) : ViewModel() {

    var state by mutableStateOf(ContactsUiState())
        private set

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.LoadContacts -> {
                loadContacts()
            }

            is ContactsEvent.DeleteContact -> {
                deleteContact(event.userId)
            }

            is ContactsEvent.UpdateContact -> {
                updateContact(event.contact)
            }

            is ContactsEvent.SearchQueryChanged -> {
                val newQuery = event.query

                // 🔍 Arama geçmişini güncelle (maks 5 kayıt, tekrarları temizle)
                val newHistory = if (newQuery.isNotBlank()) {
                    (listOf(newQuery) + state.searchHistory.filterNot {
                        it.equals(newQuery, ignoreCase = true)
                    }).take(5)
                } else {
                    state.searchHistory
                }

                state = state.copy(
                    searchQuery = newQuery,
                    searchHistory = newHistory
                )
            }

            ContactsEvent.ClearSearchHistory -> {
                state = state.copy(searchHistory = emptyList())
            }
        }
    }

    // -----------------------------------------------------------------------
    // Kişileri API'den getir
    // -----------------------------------------------------------------------
    private fun loadContacts() {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val contacts = getContactsUseCase()
                val deviceSet = computeDeviceContacts(contacts)

                state = state.copy(
                    isLoading = false,
                    contacts = contacts,
                    deviceContacts = deviceSet,
                    error = null
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Kişiler yüklenirken bir hata oluştu"
                )
            }
        }
    }

    // -----------------------------------------------------------------------
    // Kişi sil
    // -----------------------------------------------------------------------
    private fun deleteContact(userId: String) {
        viewModelScope.launch {
            try {
                deleteContactUseCase(userId)

                val newList = state.contacts.filterNot { it.id == userId }
                val deviceSet = computeDeviceContacts(newList)

                state = state.copy(
                    contacts = newList,
                    deviceContacts = deviceSet
                )
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Kişi silinirken bir hata oluştu"
                )
            }
        }
    }

    // -----------------------------------------------------------------------
    // Kişi güncelle
    // -----------------------------------------------------------------------
    private fun updateContact(contact: Contact) {
        viewModelScope.launch {
            try {
                val updated = updateContactUseCase(contact)

                val newList = state.contacts.map {
                    if (it.id == updated.id) updated else it
                }
                val deviceSet = computeDeviceContacts(newList)

                state = state.copy(
                    contacts = newList,
                    deviceContacts = deviceSet
                )
            } catch (e: Exception) {
                state = state.copy(
                    error = e.message ?: "Kişi güncellenirken bir hata oluştu"
                )
            }
        }
    }

    // -----------------------------------------------------------------------
    // Cihaz rehberine kaydet (ProfileScreen’deki butondan tetiklenecek)
    // -----------------------------------------------------------------------
    fun saveContactToDevice(contact: Contact) {
        viewModelScope.launch {
            val success = deviceContactsHelper.saveContactToDevice(contact)
            if (success) {
                val deviceSet = computeDeviceContacts(state.contacts)
                state = state.copy(deviceContacts = deviceSet)
            }
        }
    }

    // -----------------------------------------------------------------------
    // Yardımcı: listedeki kişilere göre deviceContacts set'ini hesaplar
    // -----------------------------------------------------------------------
    private fun computeDeviceContacts(contacts: List<Contact>): Set<String> {
        return contacts.mapNotNull { contact ->
            val phone = contact.phoneNumber
            if (phone.isNotBlank() && deviceContactsHelper.isContactInDevice(phone)) {
                phone
            } else {
                null
            }
        }.toSet()
    }
}
