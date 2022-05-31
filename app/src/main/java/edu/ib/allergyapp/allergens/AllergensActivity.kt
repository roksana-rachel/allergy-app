package edu.ib.allergyapp.allergens

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import edu.ib.allergyapp.R

class AllergensActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_allergens)
        viewRecords()
    }

    //dodaj rekord
    fun saveRecord(view: View){

        val d_id = findViewById<View>(R.id.d_id) as EditText
        val d_allergen = findViewById<View>(R.id.d_allergen) as EditText
        val d_symptoms = findViewById<View>(R.id.d_symptoms) as EditText

        val id = d_id.text.toString()
        val allergen = d_allergen.text.toString()
        val symptoms = d_symptoms.text.toString()
        val databaseHandler: DatabaseHandler= DatabaseHandler(this)

        if(id.trim()!="" && allergen.trim()!="" && symptoms.trim()!=""){
            val status = databaseHandler.addAllergen(AllergenModel(Integer.parseInt(id),allergen, symptoms))
            if(status > -1){
                Toast.makeText(applicationContext,"Zapisano alergen",Toast.LENGTH_LONG).show()
                d_id.text.clear()
                d_allergen.text.clear()
                d_symptoms.text.clear()
                viewRecords()
            }
        }else{
            Toast.makeText(applicationContext,"Pola nie mogą być puste",Toast.LENGTH_LONG).show()
        }

    }

    /*
    fun viewRecord(view: View){
        val databaseHandler: DatabaseHandler= DatabaseHandler(this)

        val aller: List<AllergenModel> = databaseHandler.viewAllergen()
        val allerArrayId = Array<String>(aller.size){"0"}
        val allerArrayAllergen = Array<String>(aller.size){"null"}
        val allerArraySymptoms = Array<String>(aller.size){"null"}

        var index = 0
        for(a in aller){
            allerArrayId[index] = a.id.toString()
            allerArrayAllergen[index] = a.allergen
            allerArraySymptoms[index] = a.symptoms
            index++
        }

        val myListAdapter = MyListAdapter(this,allerArrayId,allerArrayAllergen,allerArraySymptoms)
        val listView = findViewById<View>(R.id.listView) as ListView
        listView.adapter = myListAdapter
    }
     */

    //update
    fun updateRecord(view: View){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtId = dialogView.findViewById(R.id.updateId) as EditText
        val edtAllergen = dialogView.findViewById(R.id.updateAllergen) as EditText
        val edtSymptoms = dialogView.findViewById(R.id.updateSymptoms) as EditText

        dialogBuilder.setTitle("Zaktualizuj alergen")
        dialogBuilder.setMessage("Wprowdź dane")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val updateId = edtId.text.toString()
            val updateAllergen = edtAllergen.text.toString()
            val updateSymptoms = edtSymptoms.text.toString()
            val databaseHandler: DatabaseHandler= DatabaseHandler(this)
            if(updateId.trim()!="" && updateAllergen.trim()!="" && updateSymptoms.trim()!=""){
                val status = databaseHandler.updateAllergen(AllergenModel(Integer.parseInt(updateId),updateAllergen, updateSymptoms))
                if(status > -1){
                    Toast.makeText(applicationContext,"Zaktualizowano alergen",Toast.LENGTH_LONG).show()
                    viewRecords()
                }
            }else{
                Toast.makeText(applicationContext,"Pola nie mogą być puste",Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cofnij", DialogInterface.OnClickListener { dialog, which ->
        })
        val b = dialogBuilder.create()
        b.show()
    }

    //usuń rekord
    fun deleteRecord(view: View){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.delete_dialog, null)
        dialogBuilder.setView(dialogView)

        val dltId = dialogView.findViewById(R.id.deleteId) as EditText
        dialogBuilder.setTitle("Usuń alergen")
        dialogBuilder.setMessage("Wprowadź id")
        dialogBuilder.setPositiveButton("Usuń", DialogInterface.OnClickListener { _, _ ->

            val deleteId = dltId.text.toString()
            val databaseHandler: DatabaseHandler= DatabaseHandler(this)
            if(deleteId.trim()!=""){
                val status = databaseHandler.deleteAllergen(AllergenModel(Integer.parseInt(deleteId),"",""))
                if(status > -1){
                    Toast.makeText(applicationContext,"Usuniętego alergen",Toast.LENGTH_LONG).show()
                    viewRecords()
                }
            }else{
                Toast.makeText(applicationContext,"Id nie może być puste",Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cofnij", DialogInterface.OnClickListener { _, _ ->
        })
        val b = dialogBuilder.create()
        b.show()
    }

    //załaduj rekody
    private fun viewRecords(){
        val databaseHandler: DatabaseHandler= DatabaseHandler(this)

        val aller: List<AllergenModel> = databaseHandler.viewAllergen()
        val allerArrayId = Array<String>(aller.size){"0"}
        val allerArrayAllergen = Array<String>(aller.size){"null"}
        val allerArraySymptoms = Array<String>(aller.size){"null"}

        var index = 0
        for(a in aller){
            allerArrayId[index] = a.id.toString()
            allerArrayAllergen[index] = a.allergen
            allerArraySymptoms[index] = a.symptoms
            index++
        }

        val myListAdapter = MyListAdapter(this,allerArrayId,allerArrayAllergen,allerArraySymptoms)
        val listView = findViewById<View>(R.id.listView) as ListView
        listView.adapter = myListAdapter
    }
}
