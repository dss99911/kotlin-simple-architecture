package kim.jeonghyeon.pergist

import kotlinx.coroutines.flow.Flow

actual class Preference {
    actual fun has(key: String): Boolean {
        TODO("Not yet implemented")
    }

    actual fun getString(key: String): String? {
        TODO("Not yet implemented")
    }

    actual fun getString(key: String, defValue: String): String {
        TODO("Not yet implemented")
    }

    actual fun getStringFlow(key: String): Flow<String?> {
        TODO("Not yet implemented")
    }

    actual fun setString(key: String, value: String?) {
    }

}