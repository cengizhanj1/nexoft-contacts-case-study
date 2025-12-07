package com.cengizhan.contactsapp.util

import android.Manifest
import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import com.cengizhan.contactsapp.domain.model.Contact

class DeviceContactsHelper(private val context: Context) {

    // Ortak permission kontrolü
    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Verilen telefon numarası cihaz rehberinde var mı?
     * Hata veya izin yoksa => false döner, ASLA CRASH YOK.
     */
    fun isContactInDevice(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrBlank()) return false
        if (!hasPermission(Manifest.permission.READ_CONTACTS)) return false

        return try {
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            val selection = "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?"
            val selectionArgs = arrayOf(phoneNumber)

            context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
        } catch (e: SecurityException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Kişiyi cihaz rehberine kaydet.
     * İzin yoksa veya hata olursa => false, CRASH YOK.
     */
    fun saveContactToDevice(contact: Contact): Boolean {
        val firstName = contact.firstName ?: ""
        val lastName  = contact.lastName ?: ""
        val phone     = contact.phoneNumber ?: ""

        if (phone.isBlank()) return false
        if (!hasPermission(Manifest.permission.WRITE_CONTACTS)) return false

        return try {
            val ops = ArrayList<ContentProviderOperation>()

            // 1) RawContact
            val rawIndex = ops.size
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build()
            )

            // 2) İsim
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        rawIndex
                    )
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        firstName
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        lastName
                    )
                    .build()
            )

            // 3) Telefon
            ops.add(
                ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(
                        ContactsContract.Data.RAW_CONTACT_ID,
                        rawIndex
                    )
                    .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        phone
                    )
                    .withValue(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                    .build()
            )

            context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
            true
        } catch (e: SecurityException) {
            false
        } catch (e: OperationApplicationException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
