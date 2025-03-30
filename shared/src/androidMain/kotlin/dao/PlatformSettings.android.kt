package dao

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import models.ModelProvider
import utils.KeyType

actual class PlatformSettings {
    private lateinit var context: Context
    fun init(androidContext: Context) {
        context = androidContext
    }

    private val masterKey by lazy {
        MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
    }

    private val encryptedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual fun getApiKey(provider: ModelProvider, type: KeyType): String {
        val key = when(type) {
            KeyType.API_KEY -> "${provider.name}_API_KEY"
            KeyType.CLIENT_ID -> "${provider.name}_CLIENT_ID"
            KeyType.SECRET -> "${provider.name}_SECRET"
        }
        return encryptedPrefs.getString(key, "") ?: ""
    }

    actual fun setApiKey(provider: ModelProvider, type: KeyType, value: String) {
        val key = when(type) {
            KeyType.API_KEY -> "${provider.name}_API_KEY"
            KeyType.CLIENT_ID -> "${provider.name}_CLIENT_ID"
            KeyType.SECRET -> "${provider.name}_SECRET"
        }
        encryptedPrefs.edit().putString(key, value).apply()
    }

}