package com.ibnadm.kartinilite.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
class Report (
    var id:String = "",
    var uid:String = "",
    var title:String = "",
    var text:String = "",
    var status:Int = 1,
    var instance:Int = 1,
    var statusTimestamp:Long = -1,
    var verify: Int = 1,
    var proses:Int = 1,
    var timestamp:Long = -1,
    var uri:String = ""
): Parcelable{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "uid" to uid,
            "title" to title,
            "text" to text,
            "status" to status,
            "instance" to instance,
            "statusTimestamp" to statusTimestamp,
            "verify" to verify,
            "proses" to proses,
            "timestamp" to timestamp,
            "uri" to uri
        )
    }
}