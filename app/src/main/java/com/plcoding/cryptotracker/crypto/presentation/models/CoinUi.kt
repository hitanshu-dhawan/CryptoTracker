package com.plcoding.cryptotracker.crypto.presentation.models

import android.icu.text.NumberFormat
import androidx.annotation.DrawableRes
import com.plcoding.cryptotracker.crypto.domain.Coin
import com.plcoding.cryptotracker.core.presentation.util.getDrawableIdForCoin
import com.plcoding.cryptotracker.crypto.presentation.coin_detail.chart.DataPoint
import java.util.Locale

data class CoinUi(
    val id: String,
    val name: String,
    val symbol: String,
    @DrawableRes val iconRes: Int,
    val rank: Int,
    val marketCapUsd: DisplayableNumber,
    val priceUsd: DisplayableNumber,
    val changePercent24Hr: DisplayableNumber,
    val coinPriceHistory: List<DataPoint> = emptyList()
)

/**
 * A wrapper class that holds a numerical value along with its formatted representation
 * for display purposes in the UI.
 *
 * @property value The actual numeric value.
 * @property formatted The formatted string representation of the value for display.
 */
data class DisplayableNumber(
    val value: Double,
    val formatted: String
)

/**
 * Converts a Coin domain model to a CoinUi presentation model.
 */
fun Coin.toCoinUi(): CoinUi {
    return CoinUi(
        id = id,
        name = name,
        symbol = symbol,
        iconRes = getDrawableIdForCoin(symbol),
        rank = rank,
        marketCapUsd = marketCapUsd.toDisplayableNumber(),
        priceUsd = priceUsd.toDisplayableNumber(),
        changePercent24Hr = changePercent24Hr.toDisplayableNumber()
    )
}

fun Double.toDisplayableNumber(): DisplayableNumber {
    val formatter = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    return DisplayableNumber(
        value = this,
        formatted = formatter.format(this)
    )
}
