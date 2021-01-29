package com.ibnadm.kartinilite.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Report
import com.ibnadm.kartinilite.models.User
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_detail_post.*
import kotlinx.android.synthetic.main.activity_detail_report.*
import kotlinx.android.synthetic.main.report_row.*
import java.text.SimpleDateFormat
import java.util.*

class DetailReportActivity : AppCompatActivity() {
    val TAG = "DetailPostActivity"
    var report: Report? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_report)
        report = intent.getParcelableExtra<Report>(LogReportActivity.REPORT_KEY)
        listenReport()
        btn_backtoreport.setOnClickListener {
            val intent = Intent(this, LogReportActivity::class.java)
            startActivity(intent)
        }
    }
    private fun listenReport() {
        val mdPatterns = SimpleDateFormat("MMMM dd")
        val timePatterns = SimpleDateFormat("h:mm a")
        val submitDate = Date(report!!.timestamp)
        val acceptDate = Date(report!!.statusTimestamp)
        tv_report_detail_title.text = report!!.title
        tv_date_submited.text = mdPatterns.format(submitDate) + " at " + timePatterns.format(submitDate)
        tv_date_sent.text = mdPatterns.format(submitDate) + " at " + timePatterns.format(submitDate)
        tv_date_review.text = mdPatterns.format(submitDate) + " at " + timePatterns.format(submitDate)
        tv_state_sent.text = "Diserahkan"
        tv_state_deliver.text = "Terkirim"
        tv_state_review.text = "Dalam Review"
        if (report!!.proses == 1 && report!!.verify != 3){
            tv_date_accept.text = "Dalam proses"
            rb_review.isChecked = true
            rb_accept.isEnabled = false
            rb_sent.isEnabled = false
            rb_deliver.isEnabled = false
            tv_state_accept.text = "Menunggu"
            tv_state_accept.setBackgroundColor(Color.GRAY)
            tv_notif_status.text = "Menunggu.."
        } else if (report!!.proses == 2 && report!!.verify == 2) {
            tv_date_accept.text =
                mdPatterns.format(acceptDate) + " at " + timePatterns.format(acceptDate)
            rb_accept.isEnabled = true
            rb_accept.isChecked = true
            rb_sent.isEnabled = false
            rb_deliver.isEnabled = false
            rb_review.isChecked = false
            rb_review.isEnabled = false
            tv_state_accept.text = "Disetujui"
            tv_notif_status.text = "Laporan kamu telah direview dan disetujui. sebentar lagi akan dihubungi untuk tindakan lebih lanjut."
        } else if (report!!.proses == 3 || report!!.verify == 3) {
            tv_date_accept.text =
                mdPatterns.format(acceptDate) + " at " + timePatterns.format(acceptDate)
            rb_accept.isEnabled = true
            rb_accept.isChecked = true
            rb_sent.isEnabled = false
            rb_deliver.isEnabled = false
            rb_review.isChecked = false
            rb_review.isEnabled = false
            tv_state_accept.text = "Ditolak"
            tv_state_accept.setBackgroundColor(Color.RED)
            tv_notif_status.text = "Maaf laporan kamu belum dapat diproses, silahkan isi report dengan detail dan bukti yang lebih akurat"
        }
    }
}