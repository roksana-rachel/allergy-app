package edu.ib.allergyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import edu.ib.allergyapp.adrenaline.AdrenalineActivity
import edu.ib.allergyapp.airQuality.AirActivity
import edu.ib.allergyapp.allergens.AllergensActivity
import edu.ib.allergyapp.medicine.MedicineActivity
import edu.ib.allergyapp.ocr.OCRActivity
import edu.ib.allergyapp.pollens.PollenActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //9DDB5C
    }

    fun onAirQualityClick(view: View) {
        val intent = Intent(this, AirActivity::class.java)
        startActivity(intent)
    }

    fun onPollenClick(view: View) {
        val intent = Intent(this, PollenActivity::class.java)
        startActivity(intent)
    }

    fun onAdrenalineClick(view: View) {
        val intent = Intent(this, AdrenalineActivity::class.java)
        startActivity(intent)
    }

    fun onOCRClick(view: View) {
        val intent = Intent(this, OCRActivity::class.java)
        startActivity(intent)
    }

    fun onAllergensClick(view: View) {
        val intent = Intent(this, AllergensActivity::class.java)
        startActivity(intent)
    }

    fun onMedicineClick(view: View) {
        val intent = Intent(this, MedicineActivity::class.java)
        startActivity(intent)
    }
}