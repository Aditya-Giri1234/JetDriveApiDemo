package com.example.jetdrivedemoapi.common.custom_class

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.NavType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class CustomNavType< T : Parcelable>(
    private val clazz: Class<T>,
    private val serializer: KSerializer<T>,
    private val json : Json = Json
) : NavType<T>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): T? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, clazz) as T
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }

    override fun put(bundle: Bundle, key: String, value: T) =
        bundle.putParcelable(key, value)

    override fun parseValue(value: String): T = json.decodeFromString(serializer,value)

    override fun serializeAsValue(value: T): String = json.encodeToString(serializer, value)

    override val name: String = clazz.name

}

inline fun <reified T : Parcelable> getCustomNavTypeMap(serializer: KSerializer<T>): Pair<KType, CustomNavType<T>> = typeOf<T>() to CustomNavType(T::class.java, serializer)

inline fun  <reified T : Parcelable> getCustomNavTypeMap() = typeOf<T>() to NavType.mapper<T>()

inline fun <reified T : Parcelable> parcelableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelable(key, T::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelable(key)
        }

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: Bundle, key: String, value: T) = bundle.putParcelable(key, value)
}


inline fun <reified T : Any> serializableType(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let<String, T>(json::decodeFromString)

    override fun parseValue(value: String): T = json.decodeFromString(value)

    override fun serializeAsValue(value: T): String = json.encodeToString(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }
}

inline fun <reified T : Parcelable> NavType.Companion.mapper(): NavType<T> {
    return object : NavType<T>(
        isNullableAllowed = false
    ) {
        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putParcelable(key, value)
        }

        override fun get(bundle: Bundle, key: String): T? {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                @Suppress("DEPRECATION")
                bundle.getParcelable(key)
            } else {
                bundle.getParcelable(key, T::class.java)
            }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString<T>(value)
        }

        override fun serializeAsValue(value: T): String {
            return Json.encodeToString(value)
        }

        override val name = T::class.java.name
    }
}