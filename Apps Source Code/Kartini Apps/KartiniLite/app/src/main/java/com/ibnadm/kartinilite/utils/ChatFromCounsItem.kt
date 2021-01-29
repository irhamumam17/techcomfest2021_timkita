package com.ibnadm.kartinilite.utils

import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Counselor
import com.ibnadm.kartinilite.models.Message
import com.ibnadm.kartinilite.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatFromCounsItem(val message: Message, val counselor: Counselor): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tv_from_row.text = message.text
        val timePatterns = SimpleDateFormat("hh:mm")
        val postMessage = Date(message.timestamp)
        viewHolder.itemView.tv_time_from.text = timePatterns.format(postMessage)
        Picasso.get()
            .load(counselor.photoUrl)
            .into(viewHolder.itemView.iv_profile_chat1)
    }
}