package com.brzas.frequency_app.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.brzas.frequency_app.R
import com.brzas.frequency_app.helper.ReminderCreateListener
import com.brzas.frequency_app.helper.SQLiteAttributes
import com.brzas.frequency_app.models.Reminder
import com.brzas.frequency_app.sqlite.DatabaseQueryClass
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ReminderCreateDialogFragment : DialogFragment() {

    private lateinit var reminderCreateListener: ReminderCreateListener

    private lateinit var reminderNameEditText: EditText
    private lateinit var dateEditText: EditText

    private lateinit var createButton: Button
    private lateinit var cancelButton: Button

    private var reminderName = ""
    private var timeDuration: Long = -1

    private var reminderDate: Date? = null
    private val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val inputFormatDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder_create_dialog, container, false)

        reminderNameEditText = view.findViewById(R.id.name)
        dateEditText = view.findViewById(R.id.date)
        createButton = view.findViewById(R.id.createButton)
        cancelButton = view.findViewById(R.id.cancelButton)

        val title = requireArguments().getString(SQLiteAttributes.TITLE)
        dialog?.setTitle(title)

        createButton.setOnClickListener {
            reminderName = reminderNameEditText.text.toString()

            // Verifica se o nome e a data estão preenchidos
            if (reminderName.isBlank() || dateEditText.text.isNullOrBlank()) {
                showAlert("Nome e data são campos obrigatórios.")
                return@setOnClickListener
            }

            try {
                val date = inputFormatDate.parse(dateEditText.text.toString())
                Log.d(TAG, "String ${dateEditText.text.toString()} date $date")
                reminderDate = date
            } catch (e: ParseException) {
                throw RuntimeException(e)
            }

            // Verifica se a data é posterior à data atual
            if (reminderDate?.before(Date()) == true) {
                showAlert("A data deve ser igual ou posterior à data atual.")
                return@setOnClickListener
            }

            val reminder = Reminder(-1, reminderName, reminderDate!!)

            val databaseQueryClass = DatabaseQueryClass(requireContext())

            val id = databaseQueryClass.insertReminder(reminder)

            if (id > 0) {
                reminder.id = id
                reminderCreateListener.onReminderCreated(reminder)
                dialog?.dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dialog?.dismiss()
        }

        return view
    }
    fun showAlert(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Alerta")
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    companion object {
        private const val TAG = "CreateDialog"

        fun newInstance(title: String, listener: ReminderCreateListener): ReminderCreateDialogFragment {
            val fragment = ReminderCreateDialogFragment()
            fragment.reminderCreateListener = listener
            val args = Bundle()
            args.putString("title", title)
            fragment.arguments = args

            fragment.setStyle(STYLE_NORMAL, R.style.CustomDialog)

            return fragment
        }
    }
}
