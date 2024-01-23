package com.brzas.frequency_app.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.brzas.frequency_app.R
import com.brzas.frequency_app.models.Reminder
import com.brzas.frequency_app.sqlite.DatabaseQueryClass
import java.text.SimpleDateFormat
import java.util.*

class ReminderRecyclerViewAdapter(
    private val context: Context,
    private val reminderList: MutableList<Reminder>
) : RecyclerView.Adapter<ReminderRecyclerViewAdapter.ReminderViewHolder>() {

    private val databaseQueryClass = DatabaseQueryClass(context)
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reminder_content, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminderList[position]

        holder.nameTextView.text = reminder.name
        holder.dateTextView.text = dateFormat.format(reminder.date)

        holder.deleteImageView.setOnClickListener {
            showDeleteConfirmationDialog(position)
        }

    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage("Are you sure you want to delete?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ -> deleteReminder(position) }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    /*private fun showEditDialog(reminderId: Long, position: Int) {
        val reminderUpdateDialogFragment =
            ReminderUpdateDialogFragment.newInstance("Update Reminder", reminderId, position, object : ReminderUpdateListener {
                override fun onReminderUpdate(reminder: Reminder, pos: Int) {
                    reminderList[pos] = reminder
                    notifyItemChanged(pos)
                }
            })
        reminderUpdateDialogFragment.show((context as MainReminderActivity).supportFragmentManager, SQLiteAttributes.UPDATE_REMINDER)
    }*/

    private fun deleteReminder(position: Int) {
        val reminder = reminderList[position]
        val count = databaseQueryClass.deleteReminderById(reminder.id)
        if (count > 0) {
            reminderList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun addReminder(reminder: Reminder) {
        reminderList.add(reminder)
        notifyItemInserted(itemCount - 1)
    }

    override fun getItemCount(): Int = reminderList.size

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_txt)
        val dateTextView: TextView = itemView.findViewById(R.id.date_txt)
        val deleteImageView: ImageView = itemView.findViewById(R.id.deleteImageView)
    }
}
