package com.cengizhan.contactsapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cengizhan.contactsapp.data.remote.RetrofitClient
import com.cengizhan.contactsapp.data.repository.ContactsRepositoryImpl
import com.cengizhan.contactsapp.domain.usecase.CreateContactUseCase
import com.cengizhan.contactsapp.domain.usecase.DeleteContactUseCase
import com.cengizhan.contactsapp.domain.usecase.GetContactsUseCase
import com.cengizhan.contactsapp.domain.usecase.UpdateContactUseCase
import com.cengizhan.contactsapp.presentation.add_edit.AddEditContactScreen
import com.cengizhan.contactsapp.presentation.add_edit.AddEditContactViewModel
import com.cengizhan.contactsapp.presentation.add_edit.AddEditContactViewModelFactory
import com.cengizhan.contactsapp.presentation.add_edit.DoneScreen
import com.cengizhan.contactsapp.presentation.contacts.ContactsEvent
import com.cengizhan.contactsapp.presentation.contacts.ContactsScreen
import com.cengizhan.contactsapp.presentation.contacts.ContactsViewModel
import com.cengizhan.contactsapp.presentation.contacts.ContactsViewModelFactory
import com.cengizhan.contactsapp.presentation.profile.ProfileScreen
import com.cengizhan.contactsapp.ui.theme.ContactsAppTheme
import com.cengizhan.contactsapp.util.DeviceContactsHelper

private const val ROUTE_CONTACTS = "contacts"
private const val ROUTE_ADD_EDIT = "add_edit"
private const val ROUTE_DONE = "done"
private const val ROUTE_PROFILE = "profile/{contactId}"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ContactsRepositoryImpl(RetrofitClient.api)

        val getContactsUseCase = GetContactsUseCase(repository)
        val deleteContactUseCase = DeleteContactUseCase(repository)
        val updateContactUseCase = UpdateContactUseCase(repository)
        val createContactUseCase = CreateContactUseCase(repository)

        // 👉 Cihaz rehberi helper
        val deviceContactsHelper = DeviceContactsHelper(applicationContext)

        val contactsViewModelFactory = ContactsViewModelFactory(
            getContactsUseCase = getContactsUseCase,
            deleteContactUseCase = deleteContactUseCase,
            updateContactUseCase = updateContactUseCase,
            deviceContactsHelper = deviceContactsHelper
        )

        val addEditViewModelFactory = AddEditContactViewModelFactory(
            createContactUseCase = createContactUseCase,
            updateContactUseCase = updateContactUseCase
        )

        setContent {
            ContactsAppTheme {
                AppNavHost(
                    contactsViewModelFactory = contactsViewModelFactory,
                    addEditViewModelFactory = addEditViewModelFactory,
                    deviceContactsHelper = deviceContactsHelper
                )
            }
        }
    }
}

@Composable
private fun AppNavHost(
    contactsViewModelFactory: ContactsViewModelFactory,
    addEditViewModelFactory: AddEditContactViewModelFactory,
    deviceContactsHelper: DeviceContactsHelper
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val contactsViewModel: ContactsViewModel =
        viewModel(factory = contactsViewModelFactory)

    val addEditViewModel: AddEditContactViewModel =
        viewModel(factory = addEditViewModelFactory)

    NavHost(
        navController = navController,
        startDestination = ROUTE_CONTACTS
    ) {
        // 📜 Kişi listesi
        composable(ROUTE_CONTACTS) {

            // 🔥 EKRAN AÇILDIĞINDA OTOMATİK KİŞİLERİ YÜKLE
            LaunchedEffect(Unit) {
                contactsViewModel.onEvent(ContactsEvent.LoadContacts)
            }

            ContactsScreen(
                state = contactsViewModel.state,
                onEvent = contactsViewModel::onEvent,
                onAddClick = { navController.navigate(ROUTE_ADD_EDIT) },
                onContactClick = { contactId ->
                    navController.navigate("profile/$contactId")
                }
            )
        }

        // ➕ Add / Edit ekranı
        composable(ROUTE_ADD_EDIT) {
            val uiState = addEditViewModel.state

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    navController.popBackStack()
                    navController.navigate(ROUTE_DONE)
                    contactsViewModel.onEvent(ContactsEvent.LoadContacts)
                }
            }

            AddEditContactScreen(
                state = uiState,
                onEvent = addEditViewModel::onEvent,
                onBackClick = { navController.popBackStack() }
            )
        }

        // ✅ Lottie Done ekranı
        composable(ROUTE_DONE) {
            DoneScreen(
                onFinish = {
                    navController.popBackStack(
                        ROUTE_CONTACTS,
                        inclusive = false
                    )
                }
            )
        }

        // 👤 Profil ekranı
        composable(
            route = ROUTE_PROFILE,
            arguments = listOf(
                navArgument("contactId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val contactId = backStackEntry.arguments?.getString("contactId")
            val contact = contactsViewModel.state.contacts.find { it.id == contactId }

            if (contact != null) {
                ProfileScreen(
                    contact = contact,
                    onEvent = contactsViewModel::onEvent,
                    onBackClick = { navController.popBackStack() },
                    onSaveToPhoneClick = { deviceContact ->
                        val success =
                            deviceContactsHelper.saveContactToDevice(deviceContact)

                        val message = if (success) {
                            "Kişi telefon rehberine kaydedildi"
                        } else {
                            "Rehbere kaydedilirken bir hata oluştu"
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
    }
}
