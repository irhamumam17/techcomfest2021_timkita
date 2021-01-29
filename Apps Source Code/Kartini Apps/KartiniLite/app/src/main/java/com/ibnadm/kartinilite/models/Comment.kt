package com.ibnadm.kartinilite.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Comment (
    var id:String = "",
    var fromUid:String = "",
    var toPostId: String = "",
    var text:String = "",
    var timestamp: Long = -1
){
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "fromUid" to fromUid,
            "toPostId" to toPostId,
            "text" to text,
            "timestamp" to timestamp
        )
    }
}