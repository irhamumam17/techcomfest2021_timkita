package com.ibnadm.kartinilite.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
class User (
    var uid: String = "",
    var fullName: String = "",
    var email: String = "",
    var address: String = "",
    var sex: String = "",
    var nik: String = "",
    var photoUrl: String = "",
    var phoneNumber: String = "",
    var trustedContact: String = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "address" to address,
            "sex" to sex,
            "nik" to nik,
            "photoUrl" to photoUrl,
            "phoneNumber" to phoneNumber,
            "trustedContact" to trustedContact
        )
    }
}