package com.example.reis.other

object Constants {

    const val DEFAULT_PROFILE_PICTURE_URL =
            "https://firebasestorage.googleapis.com/v0/b/reis-35057.appspot.com/o/img_avatar.png?alt=media&token=3e604e2f-36f6-4801-8540-dccf978c78ec"

    const val MIN_USERNAME_LENGTH = 4
    const val MAX_USERNAME_LENGTH = 16
    const val MIN_PASSWORD_LENGTH = 8

    const val SEARCH_TIME_DELAY = 500L

    val SPECIAL_CHARACTERS_REGEX = Regex("(?=.*?[#?!@\$%^&*-.,])")
    val UPPER_CASE_LETTER_REGEX = Regex("(?=.*?[A-Z])")
    val LOWER_CASE_LETTER_REGEX = Regex("(?=.*?[a-z])")
}