package com.ibnadm.kartinilite.utils

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Comment
import com.ibnadm.kartinilite.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.comment_row.view.*
import kotlinx.android.synthetic.main.post_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class CommentItem(val comment: Comment, val Uid: String): Item<ViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.comment_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.tv_text_comment_row.text = comment.text
        val mdPatterns = SimpleDateFormat("MMMM dd")
        val timePatterns = SimpleDateFormat("h:mm a")
        val postDate = Date(comment.timestamp)
        viewHolder.itemView.tv_date_comment.text = mdPatterns.format(postDate) + " at " + timePatterns.format(postDate)
        val ref = FirebaseDatabase.getInstance().reference.child("users/$Uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("CommentItem",p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java) ?: return
                Picasso.get()
                    .load(user.photoUrl)
                    .into(viewHolder.itemView.iv_profile_post_author_comment)
                viewHolder.itemView.tv_name_post_author_comment.text = user.fullName
            }

        })

    }
}