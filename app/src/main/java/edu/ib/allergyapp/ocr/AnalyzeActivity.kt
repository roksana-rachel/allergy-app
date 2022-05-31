package edu.ib.allergyapp.ocr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import edu.ib.allergyapp.R
import edu.ib.allergyapp.allergens.AllergenModel
import edu.ib.allergyapp.allergens.DatabaseHandler

class AnalyzeActivity : AppCompatActivity() {

    //val list: List<String> = listOf("mleko", "skrobia", "soja")
    private var allergens: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)

        val tvResult2 = findViewById<View>(R.id.tvResult2) as TextView
        val tvResult3 = findViewById<View>(R.id.tvResult3) as TextView

        val data=intent.getStringExtra("data")
        tvResult2.text = data
        tvResult2.movementMethod = ScrollingMovementMethod()

        val databaseHandler: DatabaseHandler = DatabaseHandler(this)

        val aller: List<AllergenModel> = databaseHandler.viewAllergen()
        val allerArrayAllergen = Array<String>(aller.size){"null"}

        var index = 0
        for(a in aller){
            allerArrayAllergen[index] = a.allergen
            index++
        }

        for (i in aller.indices){
            if (data != null) {
                if (data.contains(aller[i].allergen)) {
                    allergens = allergens + aller[i].allergen + " "
                }
            }
        }
        tvResult3.text = allergens
        tvResult3.movementMethod = ScrollingMovementMethod()
    }
}