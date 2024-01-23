package com.brzas.frequency_app.models

import java.util.Date

class Reminder(var id: Long, var date: Date, var name: String) {

    constructor(id: Long, name: String) : this(id, Date(), name)

    constructor(id: Long, name: String, date: Date) : this(id, date, name)

    override fun toString(): String {
        return "Reminder(id=$id, date=$date, name='$name')"
    }
}
