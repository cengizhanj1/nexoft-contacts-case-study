package com.cengizhan.contactsapp.presentation.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cengizhan.contactsapp.domain.usecase.CreateContactUseCase
import com.cengizhan.contactsapp.domain.usecase.UpdateContactUseCase

class AddEditContactViewModelFactory(
    private val createContactUseCase: CreateContactUseCase,
    private val updateContactUseCase: UpdateContactUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditContactViewModel::class.java)) {
            return AddEditContactViewModel(
                createContactUseCase,
                updateContactUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
