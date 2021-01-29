package com.ibnadm.kartinilite.utils

import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Post
import com.ibnadm.kartinilite.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.post_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class PostItem(val post: Post): Item<ViewHolder>() {
    var author: User? = null
    override fun getLayout(): Int {
        return R.layout.post_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Log.d("PostWithImgItem", "post from id = ${post.fromUid}")
        val mdPatterns = SimpleDateFormat("MMMM dd")
        val timePatterns = SimpleDateFormat("h:mm a")
        val postDate = Date(post.timestamp)
        viewHolder.itemView.tv_text.text = post.text
        viewHolder.itemView.tv_tgl_post_row.text = mdPatterns.format(postDate) + " at " + timePatterns.format(postDate)
        viewHolder.itemView.tv_comment_count.text = post.comment.toString()
        if (post.imgUrl.isEmpty()){
            viewHolder.itemView.iv_img_post.visibility = View.GONE
        } else {
            Picasso.get()
                .load(post.imgUrl)
                .into(viewHolder.itemView.iv_img_post)
        }
        val fromUid = post.fromUid
        val ref = FirebaseDatabase.getInstance().reference.child("users/$fromUid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("PostWithImgItem", p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                author = p0.getValue(User::class.java)
                Log.d("PostWithImgItem", "uid = ${author?.uid}")
                Picasso.get()
                    .load(author!!.photoUrl)
                    .into(viewHolder.itemView.iv_profile_post_author)
                viewHolder.itemView.tv_name_post_author.text = author!!.fullName
            }

        })
    }
}