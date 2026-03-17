package at.eventful.messless.schema.utils

enum class UserRole {
    Admin,
    CompanyAdmin,
    Manager,
    Worker,
    StageHand;

    fun asInt(): Int = when {
        this == Admin -> 5
        this == CompanyAdmin -> 4
        this == Manager -> 3
        this == Worker -> 2
        else -> 1
    }
}