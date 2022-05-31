package edu.ib.allergyapp.adrenaline

import android.annotation.TargetApi
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import edu.ib.allergyapp.R
import java.util.Calendar

class AdrenalineActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {

    private var minute = 0;
    private var hour = 0;
    private var dayOfMonth = 0;
    private var month = 0;
    private var year = 0;

    lateinit var alarmManager: AlarmManager //alarm
    lateinit var alarmIntent: PendingIntent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adrenaline)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = PendingIntent.getBroadcast(applicationContext, 0,
            Intent(applicationContext, AlarmReceiver::class.java), 0)

        val timePick = findViewById<View>(R.id.timePick) as Button
        val datePick = findViewById<View>(R.id.datePick) as Button
        val showDate = findViewById<View>(R.id.showDate) as Button

        timePick.setOnClickListener{
            val dialog = MyTimePickerDialog()
            dialog.show(supportFragmentManager, "time_picker")
        }

        datePick.setOnClickListener{
            val dialog = MyDatePickerDialog()
            dialog.show(supportFragmentManager, "date_picker")
        }

        showDate.setOnClickListener{

            //val date = Calendar.Builder().setDate(year, month, dayOfMonth).setTimeOfDay(hour, minute, 0)
            //    .build()

            Toast.makeText(applicationContext, 
            "Alarm ustawiono na: $dayOfMonth-${month+1}-$year $hour:$minute", Toast.LENGTH_SHORT).show()

            //alarmManager.set(AlarmManager.RTC_WAKEUP, date.timeInMillis, alarmIntent)
        }

    }

    //odbiera godzinę
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        this.minute = minute
        this.hour = hourOfDay
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
    }
}