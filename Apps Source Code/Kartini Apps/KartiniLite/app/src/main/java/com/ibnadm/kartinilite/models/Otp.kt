package com.ibnadm.kartinilite.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
class Otp (
    var id:String = "",
    var fromUid:String = "",
    var text:String = ""

) : Parcelable{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "fromUid" to fromUid,
            "text" to text
        )
    }
}