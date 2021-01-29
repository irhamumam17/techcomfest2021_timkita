package com.ibnadm.kartinilite.utils

import android.graphics.Color
import android.util.Log
import com.ibnadm.kartinilite.R
import com.ibnadm.kartinilite.models.Report
import com.ibnadm.kartinilite.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_detail_report.*
import kotlinx.android.synthetic.main.report_row.view.*
import java.text.SimpleDateFormat

class ReportItem(val report: Report ): Item<ViewHolder>(){
    var author: User? = null
    override fun getLayout(): Int {
        return R.layout.report_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        Log.d("ReportItem", "report from id = ${report.uid}")

        viewHolder.itemView.tv_title_report1.text = report.title
        var status = ""
        if (report.proses == 1 && report.verify != 3){
            status = "Menunggu"
            viewHolder.itemView.tv_state_report.setBackgroundColor(Color.GRAY)
        } else if (report.proses == 2 && report.verify == 2) {
            status = "Disetujui"
            viewHolder.itemView.tv_state_report.setBackgroundColor(Color.parseColor("#03EE61"))
        } else if (report.proses == 3 || report.verify == 3) {
            status = "Ditolak"
            viewHolder.itemView.tv_state_report.setBackgroundColor(Color.RED)
        }
        viewHolder.itemView.tv_state_report.text = status
        viewHolder.itemView.tv_date.text = getDate()
    }
    private fun getDate():String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy");
        val dateString = simpleDateFormat.format (report.timestamp);
        return String.format(dateString)
    }
}