package dao

import models.ModelProvider
import utils.KeyType

actual class PlatformSettings {
    actual fun getApiKey(provider: ModelProvider, type: KeyType): String {
        TODO("Not yet implemented")
    }

    actual fun setApiKey(provider: ModelProvider, type: KeyType, value: String) {
    }
}