package com.cengizhan.contactsapp.presentation.contacts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.presentation.components.ContactListItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ContactsScreen(
    state: ContactsUiState,
    onEvent: (ContactsEvent) -> Unit,
    onAddClick: () -> Unit,
    onContactClick: (String) -> Unit
) {
    val searchQuery = state.searchQuery
    val searchHistory = state.searchHistory

    // Search alanı focus’ta mı?
    var isSearchFocused by remember { mutableStateOf(false) }

    // Silinecek kişi & bottom sheet görünürlüğü
    var contactToDelete by remember { mutableStateOf<Contact?>(null) }
    var isDeleteSheetOpen by remember { mutableStateOf(false) }

    // 🔎 Filtrelenmiş liste
    val filteredContacts = remember(state.contacts, searchQuery) {
        if (searchQuery.isBlank()) {
            state.contacts
        } else {
            state.contacts.filter { contact ->
                val fullName = "${contact.firstName} ${contact.lastName}"
                fullName.contains(searchQuery, ignoreCase = true) ||
                        contact.phoneNumber.contains(searchQuery)
            }
        }
    }

    // A-Z gruplama
    val grouped = remember(filteredContacts) {
        filteredContacts.groupBy { contact ->
            contact.firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
        }.toSortedMap()
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                },
                actions = {
                    FloatingActionButton(
                        onClick = onAddClick,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add contact"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // 🔍 Arama alanı
            TextField(
                value = searchQuery,
                onValueChange = { query ->
                    onEvent(ContactsEvent.SearchQueryChanged(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .onFocusChanged { focusState ->
                        isSearchFocused = focusState.isFocused
                    },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                placeholder = { Text("Search by name") },
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            // 🕘 Arama geçmişi – sadece fokus varken
            if (searchHistory.isNotEmpty() && isSearchFocused) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search history",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Clear all",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable {
                                onEvent(ContactsEvent.ClearSearchHistory)
                            }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    searchHistory.forEach { item ->
                        AssistChip(
                            onClick = {
                                onEvent(ContactsEvent.SearchQueryChanged(item))
                            },
                            label = {
                                Text(
                                    text = item,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==== EKRAN DURUMLARI ====
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // 1) HİÇ KİŞİ YOK → "No Contacts" (Figma: No Contact)
                state.contacts.isEmpty() && searchQuery.isBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(96.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No Contacts",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Contacts you’ve added will appear here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Create New Contact",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onAddClick() }
                            )
                        }
                    }
                }

                // 2) ARAMA VAR ama sonuc yok → "No Results"
                filteredContacts.isEmpty() && searchQuery.isNotBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.extraLarge,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(96.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "No Results",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "The user you are looking for could not be found.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // 3) Normal liste
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        grouped.forEach { (initial, contactsForLetter) ->

                            // Header
                            item(key = "header_$initial") {
                                Text(
                                    text = initial,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Her bir kişi
                            items(
                                items = contactsForLetter,
                                key = { it.id ?: it.hashCode().toString() }
                            ) { contact ->
                                val isFromDevice =
                                    state.deviceContacts.contains(contact.phoneNumber)

                                // Bu contact için swipe state
                                val dismissState = rememberDismissState(
                                    confirmStateChange = { value ->
                                        if (value == DismissValue.DismissedToStart) {
                                            // kart sona kadar kaydırılınca sheet aç
                                            contactToDelete = contact
                                            isDeleteSheetOpen = true
                                            false
                                        } else {
                                            true
                                        }
                                    }
                                )

                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(DismissDirection.EndToStart),
                                    background = {
                                        // Arkadaki “Edit / Delete” alanı
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    MaterialTheme.colorScheme.surfaceVariant
                                                )
                                                .padding(horizontal = 16.dp),
                                            horizontalArrangement = Arrangement.End,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    contact.id?.let { onContactClick(it) }
                                                }
                                            ) {
                                                Text("Edit")
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            TextButton(
                                                onClick = {
                                                    contactToDelete = contact
                                                    isDeleteSheetOpen = true
                                                }
                                            ) {
                                                Text(
                                                    text = "Delete",
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    },
                                    dismissContent = {
                                        ContactListItem(
                                            contact = contact,
                                            isFromDevice = isFromDevice,
                                            onClick = {
                                                contact.id?.let { onContactClick(it) }
                                            },
                                            onDeleteClick = {
                                                contactToDelete = contact
                                                isDeleteSheetOpen = true
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // === DELETE CONTACT BOTTOM SHEET ===
        if (isDeleteSheetOpen && contactToDelete != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    isDeleteSheetOpen = false
                    contactToDelete = null
                },
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp, bottom = 12.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.extraLarge
                            )
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Delete Contact",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Are you sure you want to delete this contact?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // NO
                        OutlinedButton(
                            onClick = {
                                isDeleteSheetOpen = false
                                contactToDelete = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                        ) {
                            Text("No")
                        }

                        // YES
                        Button(
                            onClick = {
                                val id = contactToDelete?.id
                                if (id != null) {
                                    onEvent(ContactsEvent.DeleteContact(id))
                                }
                                isDeleteSheetOpen = false
                                contactToDelete = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onBackground,
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text("Yes")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
