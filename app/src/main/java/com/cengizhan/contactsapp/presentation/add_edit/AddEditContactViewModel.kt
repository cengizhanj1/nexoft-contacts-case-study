package com.cengizhan.contactsapp.presentation.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.domain.usecase.CreateContactUseCase
import com.cengizhan.contactsapp.domain.usecase.UpdateContactUseCase
import kotlinx.coroutines.launch

class AddEditContactViewModel(
    private val createContactUseCase: CreateContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase
) : ViewModel() {

    var state by mutableStateOf(AddEditContactUiState())
        private set

    // null = yeni kayıt, dolu = güncelleme
    private var editingContactId: String? = null

    /** Yeni kişi ekleme moduna geçirir. */
    fun startCreate() {
        editingContactId = null
        state = AddEditContactUiState()
    }

    /** Kart tıklanınca çağrılacak: mevcut kişiyi düzenleme moduna alır. */
    fun startEdit(contact: Contact) {
        editingContactId = contact.id
        state = AddEditContactUiState(
            firstName = contact.firstName,
            lastName = contact.lastName,
            phoneNumber = contact.phoneNumber,
            profileImageUrl = contact.profileImageUrl ?: "",
            isLoading = false,
            error = null,
            isSaved = false
        )
    }

    fun onEvent(event: AddEditContactEvent) {
        when (event) {
            is AddEditContactEvent.FirstNameChanged ->
                state = state.copy(firstName = event.value)

            is AddEditContactEvent.LastNameChanged ->
                state = state.copy(lastName = event.value)

            is AddEditContactEvent.PhoneNumberChanged ->
                state = state.copy(phoneNumber = event.value)

            is AddEditContactEvent.ProfileImageUrlChanged ->
                state = state.copy(profileImageUrl = event.value)

            AddEditContactEvent.SaveClicked -> saveContact()
        }
    }

    private fun saveContact() {
        // basit validation
        if (state.firstName.isBlank() || state.lastName.isBlank() || state.phoneNumber.isBlank()) {
            state = state.copy(error = "Lütfen ad, soyad ve telefonu doldurun.")
            return
        }

        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true, error = null)

                val contact = Contact(
                    id = editingContactId,
                    firstName = state.firstName.trim(),
                    lastName = state.lastName.trim(),
                    phoneNumber = state.phoneNumber.trim(),
                    profileImageUrl = state.profileImageUrl.trim().ifBlank { null }
                )

                if (editingContactId == null) {
                    // yeni kişi
                    createContactUseCase(contact)
                } else {
                    // güncelleme
                    updateContactUseCase(contact)
                }

                state = state.copy(isLoading = false, isSaved = true)
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Bir hata oluştu"
                )
            }
        }
    }
}
