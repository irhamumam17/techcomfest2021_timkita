package com.ibnadm.kartinilite.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Post
import com.ibnadm.kartinilite.models.Report
import com.ibnadm.kartinilite.utils.PostItem
import com.ibnadm.kartinilite.utils.ReportItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_forum.*
import kotlinx.android.synthetic.main.activity_log_report.*
import kotlinx.android.synthetic.main.activity_report.*

class LogReportActivity : AppCompatActivity() {
    val TAG = "LogReportActivity"
    private var mAuth: FirebaseAuth? = null
    companion object {
        val REPORT_KEY = "REPORT_KEY"
    }

    val adapter = GroupAdapter<ViewHolder>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_report)
        mAuth = FirebaseAuth.getInstance()
        rv_report.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "Try to click adapter")
            val reportItem = item as ReportItem
            val intent = Intent(this, DetailReportActivity::class.java)
            intent.putExtra(LogReportActivity.REPORT_KEY, reportItem.report)
            startActivity(intent)
            finish()
        }
        btn_backlogreport.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        btn_create_rpt.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }
        listenReport()
    }
    private fun listenReport() {
        val uid = mAuth!!.currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().reference.child("reports-user/$uid")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG,p0.toException().toString())
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val report = p0.getValue(Report::class.java) ?: return
                Log.d(TAG, "try to fetch rid = $report.id")
                adapter.add(ReportItem(report))
                rv_report.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val report = p0.getValue(Report::class.java) ?: return
                Log.d(TAG, "try to fetch rid = $report.id")
                adapter.add(ReportItem(report))
                rv_report.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onStart() {
        super.onStart()
        updateUI(mAuth!!.currentUser)
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}