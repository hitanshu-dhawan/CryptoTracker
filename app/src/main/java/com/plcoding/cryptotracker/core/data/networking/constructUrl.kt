package com.plcoding.cryptotracker.core.data.networking

import com.plcoding.cryptotracker.BuildConfig

/**
 * Constructs a full URL by ensuring it is prefixed with the base URL from [BuildConfig].
 *
 * @param url The relative or full URL to be processed.
 * @return A properly formatted URL with the base URL prefixed if necessary.
 */
fun constructUrl(url: String): String {
    return when {
        // If the URL already contains the base URL, return it as is
        url.contains(BuildConfig.BASE_URL) -> url

        // If the URL starts with '/', remove the leading '/' and append it to the base URL
        url.startsWith("/") -> BuildConfig.BASE_URL + url.drop(1)

        // Otherwise, simply append the URL to the base URL
        else -> BuildConfig.BASE_URL + url
    }
}
