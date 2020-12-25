package kim.jeonghyeon.auth

data class UserSession(val userId: Long, val extra: Map<String, String>)