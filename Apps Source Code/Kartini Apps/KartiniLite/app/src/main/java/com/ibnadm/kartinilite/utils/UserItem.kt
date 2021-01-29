package com.ibnadm.kartinilite.utils

import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class UserItem(val user: User): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tv_name_newmsg.text = user.fullName
        Picasso.get()
            .load(user.photoUrl)
            .into(viewHolder.itemView.iv_profile_newmsg)
    }
}