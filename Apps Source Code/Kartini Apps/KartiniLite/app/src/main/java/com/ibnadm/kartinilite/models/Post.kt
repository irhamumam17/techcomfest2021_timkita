package com.ibnadm.kartinilite.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
class Post (
    var id:String = "",
    var fromUid:String = "",
    var text:String = "",
    var imgUrl: String = "",
    var timestamp: Long = -1,
    var like: Int = 0,
    var comment: Int = 0

) : Parcelable{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "fromUid" to fromUid,
            "text" to text,
            "imgUrl" to imgUrl,
            "timestamp" to timestamp,
            "like" to like,
            "comment" to comment
        )
    }
}