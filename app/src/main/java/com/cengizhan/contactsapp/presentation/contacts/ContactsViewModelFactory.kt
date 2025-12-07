package com.cengizhan.contactsapp.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cengizhan.contactsapp.domain.usecase.DeleteContactUseCase
import com.cengizhan.contactsapp.domain.usecase.GetContactsUseCase
import com.cengizhan.contactsapp.domain.usecase.UpdateContactUseCase
import com.cengizhan.contactsapp.util.DeviceContactsHelper

class ContactsViewModelFactory(
    private val getContactsUseCase: GetContactsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase,
    private val deviceContactsHelper: DeviceContactsHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(
                getContactsUseCase = getContactsUseCase,
                deleteContactUseCase = deleteContactUseCase,
                updateContactUseCase = updateContactUseCase,
                deviceContactsHelper = deviceContactsHelper
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
