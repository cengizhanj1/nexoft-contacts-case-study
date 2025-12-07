package com.cengizhan.contactsapp.presentation.profile

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.cengizhan.contactsapp.domain.model.Contact
import com.cengizhan.contactsapp.presentation.contacts.ContactsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    contact: Contact,
    onEvent: (ContactsEvent) -> Unit,
    onBackClick: () -> Unit,
    onSaveToPhoneClick: (Contact) -> Unit
) {
    var firstName by remember(contact.id) { mutableStateOf(contact.firstName) }
    var lastName by remember(contact.id) { mutableStateOf(contact.lastName) }
    var phone by remember(contact.id) { mutableStateOf(contact.phoneNumber) }
    var photoUrl by remember(contact.id) { mutableStateOf(contact.profileImageUrl ?: "") }

    val context = LocalContext.current

    // Fotoğrafın baskın rengi – başlangıçta hafif gri
    var dominantColor by remember(contact.id) {
        mutableStateOf(Color(0xFFDDDDDD))
    }

    // Overflow menü (3 nokta) & delete sheet state
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isDeleteSheetOpen by remember { mutableStateOf(false) }

    // Change photo bottom sheet state
    var isPhotoSheetOpen by remember { mutableStateOf(false) }

    // MaterialTheme'den default renk
    val defaultDominantColorArgb = MaterialTheme.colorScheme.primary.toArgb()

    // Fotoğraf değiştikçe baskın rengi hesapla
    LaunchedEffect(photoUrl, defaultDominantColorArgb) {
        val effectiveUrl = photoUrl.ifBlank { contact.profileImageUrl ?: "" }
        if (effectiveUrl.isNotBlank()) {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(effectiveUrl)
                    .allowHardware(false)
                    .build()

                val result = imageLoader.execute(request)
                val drawable = result.drawable as? BitmapDrawable
                val bitmap = drawable?.bitmap

                if (bitmap != null) {
                    val palette = Palette.from(bitmap).generate()
                    val colorInt = palette.getDominantColor(defaultDominantColorArgb)
                    dominantColor = Color(colorInt)
                }
            } catch (_: Exception) {
                // Hata olursa mevcut rengi koru
            }
        }
    }

    // ----------------- GALERİ LAUNCHER (Change Photo) -----------------
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Şimdilik seçilen uri'yi direkt imageUrl gibi kullanıyoruz
            photoUrl = it.toString()
        }
        isPhotoSheetOpen = false
    }

    // ----------------- REHBER İZİNLERİ -----------------
    var hasContactsPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_CONTACTS
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val readGranted = results[Manifest.permission.READ_CONTACTS] == true
        val writeGranted = results[Manifest.permission.WRITE_CONTACTS] == true
        hasContactsPermission = readGranted && writeGranted
        // Not: izin verildikten sonra kullanıcı butona tekrar basarak kaydedebilir.
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contact") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // 3 nokta menü
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        // Edit – alanlar zaten editable olduğu için şimdilik sadece menüyü kapatıyoruz
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                isMenuExpanded = false
                            }
                        )

                        // Delete – Figma’daki gibi bottom sheet aç
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                isMenuExpanded = false
                                isDeleteSheetOpen = true
                            }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔵 Profil fotoğrafı + glow
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = dominantColor.copy(alpha = 0.25f),
                        shape = CircleShape
                    )
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = photoUrl.ifBlank { contact.profileImageUrl },
                    contentDescription = "Profile photo",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Change Photo",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable {
                    isPhotoSheetOpen = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = { Text("Profile image URL (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🟢 Save to phone contact
            OutlinedButton(
                onClick = {
                    if (!hasContactsPermission) {
                        // İlk tıklamada izin iste, kullanıcı izin verince tekrar basması yeterli
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS
                            )
                        )
                    } else {
                        val deviceContact = contact.copy(
                            firstName = firstName,
                            lastName = lastName,
                            phoneNumber = phone,
                            profileImageUrl = photoUrl.ifBlank { null }
                        )
                        onSaveToPhoneClick(deviceContact)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save to My Phone Contact")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "This contact is already saved on your phone.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔵 Backend update (Done)
            Button(
                onClick = {
                    val updated = contact.copy(
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phone,
                        profileImageUrl = photoUrl.ifBlank { null }
                    )
                    onEvent(ContactsEvent.UpdateContact(updated))
                    onBackClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Done")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 🔴 Delete butonu – artık direkt silmiyor, sadece bottom sheet açıyor
            OutlinedButton(
                onClick = { isDeleteSheetOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Contact")
            }
        }
    }

    // === CHANGE PHOTO BOTTOM SHEET ===
    if (isPhotoSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isPhotoSheetOpen = false },
            containerColor = MaterialTheme.colorScheme.surface,
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
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Change Photo",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Şimdilik camera yok, case için opsiyonel.
                        // İstersen daha sonra ayrı launcher ekleyebiliriz.
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Gallery")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { isPhotoSheetOpen = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Cancel")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // === DELETE CONTACT BOTTOM SHEET ===
    if (isDeleteSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isDeleteSheetOpen = false },
            containerColor = MaterialTheme.colorScheme.surface,
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
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Delete Contact",
                    style = MaterialTheme.typography.titleMedium
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
                        onClick = { isDeleteSheetOpen = false },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("No")
                    }

                    // YES
                    Button(
                        onClick = {
                            contact.id?.let { id ->
                                onEvent(ContactsEvent.DeleteContact(id))
                            }
                            isDeleteSheetOpen = false
                            onBackClick()
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
