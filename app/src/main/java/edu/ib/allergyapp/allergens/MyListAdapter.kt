package edu.ib.allergyapp.allergens

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import edu.ib.allergyapp.R

class MyListAdapter(private val context: Activity, private val id: Array<String>, private val allergen: Array<String>, private val symptoms: Array<String>)
    : ArrayAdapter<String>(context, R.layout.custom_list, allergen) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val idText = rowView.findViewById(R.id.textViewId) as TextView
        val allergenText = rowView.findViewById(R.id.textViewAllergen) as TextView
        val symptomsText = rowView.findViewById(R.id.textViewSymptoms) as TextView

        idText.text = "Id: ${id[position]}"
        allergenText.text = "Alergen: ${allergen[position]}"
        symptomsText.text = "Objawy: ${symptoms[position]}"
        return rowView
    }
}