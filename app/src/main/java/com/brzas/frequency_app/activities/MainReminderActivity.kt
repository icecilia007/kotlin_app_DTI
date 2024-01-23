package com.brzas.frequency_app.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.brzas.frequency_app.R
import com.brzas.frequency_app.adapter.ReminderRecyclerViewAdapter
import com.brzas.frequency_app.fragment.ReminderCreateDialogFragment
import com.brzas.frequency_app.helper.ReminderCreateListener
import com.brzas.frequency_app.helper.SQLiteAttributes
import com.brzas.frequency_app.models.Reminder
import com.brzas.frequency_app.sqlite.DatabaseQueryClass

class MainReminderActivity : AppCompatActivity(), ReminderCreateListener {
    private val databaseQueryClass = DatabaseQueryClass(this)
    private val reminderList: MutableList<Reminder> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private lateinit var reminderRecyclerViewAdapter: ReminderRecyclerViewAdapter

    private lateinit var actionInsert: ImageView
    private lateinit var actionDelete: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_reminder)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = ""

        recyclerView = findViewById(R.id.recyclerReminder)
        reminderList.addAll(databaseQueryClass.getAllReminders())
        Log.d(TAG, "ReminderList $reminderList")

        reminderRecyclerViewAdapter = ReminderRecyclerViewAdapter(this, reminderList)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = reminderRecyclerViewAdapter

        actionInsert = findViewById(R.id.action_insert)
        actionDelete = findViewById(R.id.action_delete)
        actionInsert.setOnClickListener { openReminderCreateDialog() }
        actionDelete.setOnClickListener {
            if (reminderList.isEmpty()) return@setOnClickListener
            val alertDialogBuilder = AlertDialog.Builder(this@MainReminderActivity)
            alertDialogBuilder.setMessage("Are you sure you want to delete?")
            alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                val isAllDeleted = databaseQueryClass.deleteAllReminders()
                if (isAllDeleted) {
                    reminderList.clear()
                    reminderRecyclerViewAdapter.notifyDataSetChanged()
                }
            }
            alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_reminder, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                openReminderCreateDialog()
                true
            }
            R.id.action_delete -> {
                if (reminderList.isEmpty()) false else {
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setMessage("Are you sure you want to delete?")
                    alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                        val isAllDeleted = databaseQueryClass.deleteAllReminders()
                        if (isAllDeleted) {
                            reminderList.clear()
                            reminderRecyclerViewAdapter.notifyDataSetChanged()
                        }
                    }
                    alertDialogBuilder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                    true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openReminderCreateDialog() {
        val reminderCreateDialogFragment = ReminderCreateDialogFragment.newInstance("Create Reminder", this)
        reminderCreateDialogFragment.show(supportFragmentManager, SQLiteAttributes.CREATE_REMINDER)
    }

    override fun onReminderCreated(reminder: Reminder) {
//        reminderList.add(reminder)
         reminderRecyclerViewAdapter.addReminder(reminder)
        Log.d(TAG, reminder.name)
    }

    companion object {
        private const val TAG = "MainReminderActivity"
    }
}