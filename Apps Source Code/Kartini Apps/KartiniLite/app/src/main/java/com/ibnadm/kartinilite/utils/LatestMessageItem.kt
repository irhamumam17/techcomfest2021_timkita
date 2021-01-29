package com.ibnadm.kartinilite.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Message
import com.ibnadm.kartinilite.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageItem(val message:Message): Item<ViewHolder>(){
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tv_latest_msg.text = message.text
        val chatPartnerUid:String
        if (message.fromUid == FirebaseAuth.getInstance().currentUser!!.uid) {
            chatPartnerUid = message.toUid
        } else {
            chatPartnerUid = message.fromUid
        }
        val ref = FirebaseDatabase.getInstance().reference.child("users/$chatPartnerUid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("LatestMessageItem", p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                Log.d("LatestMessageItem", "uid = ${chatPartnerUser!!.uid}")
                Picasso.get()
                    .load(chatPartnerUser!!.photoUrl)
                    .into(viewHolder.itemView.iv_profile_lastest_msg)
                viewHolder.itemView.tv_name_latestmsg.text = chatPartnerUser!!.fullName
            }

        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}