package com.geovnn.vapetools.helper

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


fun safeStringToDouble(input: String): String? {
    return if(input=="") {
        input
    }else if (input.toDoubleOrNull()!=null) {
        input
    } else {
        null
    }
}

fun formatMillisToDate(milliseconds: Long?): String {
    return if (milliseconds!=null) {
        val instant = Instant.ofEpochMilli(milliseconds)
        val zoneId = ZoneId.systemDefault()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyy").withZone(zoneId)
        formatter.format(instant)
    } else {
        ""
    }
}

fun getDaysSinceDateInMillis(dateInMillis: Long?): String {
    return if (dateInMillis!=null) {
        val currentDateMillis = System.currentTimeMillis()
        val differenceInMillis = currentDateMillis - dateInMillis
        val daysSinceDate = differenceInMillis / 86_400_000
        daysSinceDate.toString()
    } else {
        ""
    }
}

fun <T1, T2, T3, T4, T5, T6, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            t6
        )
    }
}

fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    f7: Flow<T7>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        array + t6
    }.combine(f7) { array, t7 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            array[5] as T6,
            t7
        )
    }
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    f7: Flow<T7>,
    f8: Flow<T8>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        array + t6
    }.combine(f7) { array, t7 ->
        array + t7
    }.combine(f8) { array, t8 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            array[5] as T6,
            array[6] as T7,
            t8
        )
    }
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    f7: Flow<T7>,
    f8: Flow<T8>,
    f9: Flow<T9>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        array + t6
    }.combine(f7) { array, t7 ->
        array + t7
    }.combine(f8) { array, t8 ->
        array + t8
    }.combine(f9) { array, t9 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            array[5] as T6,
            array[6] as T7,
            array[7] as T8,
            t9
        )
    }
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    f7: Flow<T7>,
    f8: Flow<T8>,
    f9: Flow<T9>,
    f10: Flow<T10>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        array + t6
    }.combine(f7) { array, t7 ->
        array + t7
    }.combine(f8) { array, t8 ->
        array + t8
    }.combine(f9) { array, t9 ->
        array + t9
    }.combine(f10) { array, t10 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            array[5] as T6,
            array[6] as T7,
            array[7] as T8,
            array[8] as T9,
            t10
        )
    }
}

fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    f7: Flow<T7>,
    f8: Flow<T8>,
    f9: Flow<T9>,
    f10: Flow<T10>,
    f11: Flow<T11>,
    transform: suspend (
        T1, T2, T3, T4, T5,
        T6, T7, T8, T9, T10, T11
    ) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        array + t6
    }.combine(f7) { array, t7 ->
        array + t7
    }.combine(f8) { array, t8 ->
        array + t8
    }.combine(f9) { array, t9 ->
        array + t9
    }.combine(f10) { array, t10 ->
        array + t10
    }.combine(f11) { array, t11 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            array[5] as T6,
            array[6] as T7,
            array[7] as T8,
            array[8] as T9,
            array[9] as T10,
            t11
        )
    }
}


fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> combine(
    f1: Flow<T1>,
    f2: Flow<T2>,
    f3: Flow<T3>,
    f4: Flow<T4>,
    f5: Flow<T5>,
    f6: Flow<T6>,
    f7: Flow<T7>,
    f8: Flow<T8>,
    f9: Flow<T9>,
    f10: Flow<T10>,
    f11: Flow<T11>,
    f12: Flow<T12>,
    transform: suspend (
        T1, T2, T3, T4, T5,
        T6, T7, T8, T9, T10,
        T11, T12
    ) -> R
): Flow<R> {
    return combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 ->
        arrayOf(t1, t2, t3, t4, t5)
    }.combine(f6) { array, t6 ->
        array + t6
    }.combine(f7) { array, t7 ->
        array + t7
    }.combine(f8) { array, t8 ->
        array + t8
    }.combine(f9) { array, t9 ->
        array + t9
    }.combine(f10) { array, t10 ->
        array + t10
    }.combine(f11) { array, t11 ->
        array + t11
    }.combine(f12) { array, t12 ->
        @Suppress("UNCHECKED_CAST")
        transform(
            array[0] as T1,
            array[1] as T2,
            array[2] as T3,
            array[3] as T4,
            array[4] as T5,
            array[5] as T6,
            array[6] as T7,
            array[7] as T8,
            array[8] as T9,
            array[9] as T10,
            array[10] as T11,
            t12
        )
    }
}

fun Double.roundTo1DecimalPlaces(): String {
    return String.format(Locale.US, "%.1f", this)
}

fun Double.roundTo2DecimalPlaces(): String {
    return String.format(Locale.US, "%.2f", this)
}

fun millisToLocalDate(millis: Long?): LocalDate? {
    return if (millis != null) {
        val instant = Instant.ofEpochMilli(millis)
        val zoneId = ZoneId.systemDefault()
        val localDate = instant.atZone(zoneId).toLocalDate()
        localDate
    } else {
        null
    }
}

fun LocalDate.toFormattedString(): String {
    val formatter = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(Locale.getDefault())

    return this.format(formatter)
}