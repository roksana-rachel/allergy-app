package edu.ib.allergyapp.allergens

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.os.AsyncTask.execute
import edu.ib.allergyapp.medicine.MedicineModel

//logika database
class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "AllergensDatabase"
        private val TABLE_ALLERGEN = "AllergenTable"
        private val KEY_ID = "id"
        private val KEY_ALLERGEN = "allergen"
        private val KEY_SYMPTOPMS = "symptoms"
    }
    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_ALLERGEN_TABLE = ("CREATE TABLE " + TABLE_ALLERGEN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ALLERGEN + " TEXT,"
                + KEY_SYMPTOPMS + " TEXT" + ")")
        db?.execSQL(CREATE_ALLERGEN_TABLE)
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, 1)
        contentValues.put(KEY_ALLERGEN, "a")
        contentValues.put(KEY_SYMPTOPMS, "s" )
        db?.insert(TABLE_ALLERGEN, null, contentValues)
        db?.execSQL("INSERT INTO $TABLE_ALLERGEN ($KEY_ID, $KEY_ALLERGEN, $KEY_SYMPTOPMS) VALUES (2, 'a1', 's1')");
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_ALLERGEN)
        onCreate(db)
    }

    //dodawanie
    fun addAllergen(aller: AllergenModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, aller.id)
        contentValues.put(KEY_ALLERGEN, aller.allergen)
        contentValues.put(KEY_SYMPTOPMS,aller.symptoms )
        val success = db.insert(TABLE_ALLERGEN, null, contentValues)
        db.close()
        return success
    }

    //odczytanie danych
    fun viewAllergen():List<AllergenModel>{
        val allergenList:ArrayList<AllergenModel> = ArrayList<AllergenModel>()
        val selectQuery = "SELECT  * FROM $TABLE_ALLERGEN"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var allergen: String
        var symptoms: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                allergen = cursor.getString(cursor.getColumnIndex("allergen"))
                symptoms = cursor.getString(cursor.getColumnIndex("symptoms"))
                val aller= AllergenModel(id = id, allergen = allergen, symptoms = symptoms)
                allergenList.add(aller)
            } while (cursor.moveToNext())
        }
        return allergenList
    }

    //update
    fun updateAllergen(aller: AllergenModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, aller.id)
        contentValues.put(KEY_ALLERGEN, aller.allergen)
        contentValues.put(KEY_SYMPTOPMS,aller.symptoms )

        val success = db.update(TABLE_ALLERGEN, contentValues,"id="+aller.id,null)
        db.close()
        return success
    }

    //usuń
    fun deleteAllergen(aller: AllergenModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, aller.id) // EmpModelClass UserId
        val success = db.delete(TABLE_ALLERGEN,"id="+aller.id,null)
        db.close()
        return success
    }
}