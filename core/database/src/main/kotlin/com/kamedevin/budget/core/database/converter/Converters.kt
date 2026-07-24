package com.kamedevin.budget.core.database.converter

import androidx.room.TypeConverter
import com.kamedevin.budget.core.model.AccountType
import com.kamedevin.budget.core.model.Bucket
import com.kamedevin.budget.core.model.TransactionSource
import com.kamedevin.budget.core.model.TransactionType
import java.time.Instant

class Converters {
    @TypeConverter
    fun instantToEpochMillis(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(epochMillis: Long?): Instant? = epochMillis?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun bucketToString(bucket: Bucket?): String? = bucket?.name

    @TypeConverter
    fun stringToBucket(value: String?): Bucket? = value?.let(Bucket::valueOf)

    @TypeConverter
    fun transactionTypeToString(type: TransactionType): String = type.name

    @TypeConverter
    fun stringToTransactionType(value: String): TransactionType = TransactionType.valueOf(value)

    @TypeConverter
    fun transactionSourceToString(source: TransactionSource): String = source.name

    @TypeConverter
    fun stringToTransactionSource(value: String): TransactionSource = TransactionSource.valueOf(value)

    @TypeConverter
    fun accountTypeToString(type: AccountType): String = type.name

    @TypeConverter
    fun stringToAccountType(value: String): AccountType = AccountType.valueOf(value)
}
