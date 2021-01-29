package com.ibnadm.kartinilite.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Comment
import com.ibnadm.kartinilite.models.Post
import com.ibnadm.kartinilite.models.User
import com.ibnadm.kartinilite.utils.CommentItem
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_detail_post.*
import java.text.SimpleDateFormat
import java.util.*

class DetailPostActivity : AppCompatActivity() {
    val TAG = "DetailPostActivity"
    private var mAuth: FirebaseAuth? = null
    var authorUser: User? = null
    val adapter = GroupAdapter<ViewHolder>()
    var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)
        mAuth = FirebaseAuth.getInstance()
        rv_comment.adapter = adapter
        rv_comment.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        post = intent.getParcelableExtra<Post>(ForumActivity.POST_KEY)
        btn_backtopost.setOnClickListener {
            val intent = Intent(this, ForumActivity::class.java)
            startActivity(intent)
        }
        listenPost()
        listenComment()
        btn_send_comment.setOnClickListener {
            insertComment()
        }
    }

    private fun listenPost() {
        val idPost = post!!.id
        val ref = FirebaseDatabase.getInstance().reference.child("posts/$idPost")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onDataChange(p0: DataSnapshot) {
                val fillPost = p0.getValue(Post::class.java) ?: return
                val mdPatterns = SimpleDateFormat("MMMM dd")
                val timePatterns = SimpleDateFormat("h:mm a")
                val postDate = Date(fillPost.timestamp)
                if (fillPost.imgUrl.isEmpty()) {
                    iv_img_detail_post.visibility = View.GONE
                    tv_text_comment.text = fillPost.text
                    tv_date_post_detail.text = mdPatterns.format(postDate) + " at " + timePatterns.format(postDate)
                    tv_comment_count_detail.text = fillPost.comment.toString()
                    val fromUid = fillPost.fromUid
                    val userRef = FirebaseDatabase.getInstance().reference.child("users/$fromUid")
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d(TAG, p0.toException().toString())
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val user = p0.getValue(User::class.java) ?: return
                            tv_name_post_author_detail.text = user.fullName
                            Picasso.get()
                                .load(user.photoUrl)
                                .into(iv_profile_post_author_detail)
                        }

                    })

                } else {
                    tv_text_comment.text = fillPost.text
                    tv_date_post_detail.text = mdPatterns.format(postDate) + " at " + timePatterns.format(postDate)
                    tv_comment_count_detail.text = fillPost.comment.toString()
                    Picasso.get()
                        .load(fillPost.imgUrl)
                        .into(iv_img_detail_post)
                    val fromUid = fillPost.fromUid
                    val userRef = FirebaseDatabase.getInstance().reference.child("users/$fromUid")
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d(TAG, p0.toException().toString())
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val user = p0.getValue(User::class.java) ?: return
                            tv_name_post_author_detail.text = user.fullName
                            Picasso.get()
                                .load(user.photoUrl)
                                .into(iv_profile_post_author_detail)
                        }

                    })
                }
            }

        })
    }

    private fun listenComment() {
        val idPost = post!!.id
        val ref = FirebaseDatabase.getInstance().reference.child("posts-comments/$idPost")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, p0.toException().toString())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val comment = p0.getValue(Comment::class.java) ?: return
                Log.d(TAG, "Try to fetch comment with id comment : ${comment.id}")
                adapter.add(CommentItem(comment, comment.fromUid))
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val comment = p0.getValue(Comment::class.java) ?: return
                Log.d(TAG, "Try to fetch comment with id comment : ${comment.id}")
                adapter.add(CommentItem(comment, comment.fromUid))
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun insertComment() {
        val fromUid = mAuth!!.currentUser!!.uid
        val idPost = post!!.id
        val ref = FirebaseDatabase.getInstance().reference.child("posts-comments/$idPost").push()
        val updateCommentRef = FirebaseDatabase.getInstance().reference.child("posts/$idPost")
        val postUpdate = Post()
        postUpdate.id = post!!.id
        postUpdate.imgUrl = post!!.imgUrl
        postUpdate.fromUid = post!!.fromUid
        postUpdate.comment = (post!!.comment + 1)
        postUpdate.text = post!!.text
        postUpdate.timestamp = post!!.timestamp
        val comment = Comment()
        comment.id = ref.key!!
        comment.fromUid = fromUid
        comment.text = et_enter_comment.text.toString().trim()
        comment.timestamp = System.currentTimeMillis()
        ref.setValue(comment).addOnSuccessListener {
            updateCommentRef.setValue(postUpdate)
            Log.d(TAG, "Saved our comment : ${ref.key}")
            et_enter_comment.text.clear()
            rv_comment.scrollToPosition(adapter.itemCount - 1)
        }
    }

    fun popMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.show()
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_reply -> {
                    val idPost = post!!.id
                    val ref = FirebaseDatabase.getInstance().reference.child("posts/$idPost")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Log.d(TAG, p0.toException().toString())
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val fillPost = p0.getValue(Post::class.java) ?: return
                                val fromUid = fillPost.fromUid
                                val userRef = FirebaseDatabase.getInstance().reference.child("users/$fromUid")
                                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                        Log.d(TAG, p0.toException().toString())
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        authorUser = p0.getValue(User::class.java) ?: return
                                        val intent = Intent(this@DetailPostActivity, ChatLogActivity::class.java)
                                        intent.putExtra(NewMessageActivity.USER_KEY, authorUser)
                                        startActivity(intent)
                                    }

                                })

                            }
                    })
                    return@setOnMenuItemClickListener true
                }
                else -> throw IllegalArgumentException("Unknown Type")
            }
        }
    }
}