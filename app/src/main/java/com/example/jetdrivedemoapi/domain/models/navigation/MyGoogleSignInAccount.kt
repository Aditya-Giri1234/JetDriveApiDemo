package com.example.jetdrivedemoapi.domain.models.navigation

import android.os.Parcelable
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import java.nio.charset.StandardCharsets

@Serializable
@Parcelize
data class MyGoogleSignInAccount(
    @Serializable(with = GoogleSignInAccountSerializer::class)
    val account: GoogleSignInAccount
) : Parcelable


private object GoogleSignInAccountSerializer : KSerializer<GoogleSignInAccount> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("GoogleSignInAccount" , PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GoogleSignInAccount) {
        val gson = Gson()
        val encodeData = gson.toJson(value)
        val sanitized = encodeData
            .replace("\"", "%22")  // Replace double quotes with "%22".
            .replace("'", "%27")
        encoder.encodeString(sanitized)
    }

    override fun deserialize(decoder: Decoder): GoogleSignInAccount {
        val gson = Gson()
        val decodeData = gson.fromJson<GoogleSignInAccount>(decoder.decodeString() , object : TypeToken<GoogleSignInAccount>(){}.type)
        return decodeData
    }
}
