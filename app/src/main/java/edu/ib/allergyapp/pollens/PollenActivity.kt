package edu.ib.allergyapp.pollens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.ib.allergyapp.R
import edu.ib.allergyapp.airQuality.AirData
import edu.ib.allergyapp.airQuality.AirService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PollenActivity : AppCompatActivity() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    private var latString: String = "0"
    private var lonString: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pollen)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun showSnackbar(
            mainTextStringId: String, actionStringId: String,
            listener: View.OnClickListener
    ) {
        Toast.makeText(this@PollenActivity, mainTextStringId, Toast.LENGTH_LONG).show()
    }

    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        }
        else {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                latString = (lastLocation)!!.latitude.toString()
                lonString = (lastLocation)!!.longitude.toString()
                getCurrentData()
            }
            else {
                Log.w("test", "getLastLocation:exception", task.exception)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
                this@PollenActivity,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                34
        )
    }
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i("test", "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                    View.OnClickListener {
                        startLocationPermissionRequest()
                    })
        }
        else {
            Log.i("test", "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i("test", "onRequestPermissionResult")
        if (requestCode == 34) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i("test", "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                            View.OnClickListener {
                                // Build intent that displays the App settings screen.
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                        "package",
                                        Build.DISPLAY, null
                                )
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                    )
                }
            }
        }
    }



    private fun getCurrentData() {

        var dominant: String

        val tvDominant = findViewById<View>(R.id.tvDominant) as TextView
        val tvGrass = findViewById<View>(R.id.tvGrass) as TextView
        val tvTree = findViewById<View>(R.id.tvTree) as TextView
        val tvWeed = findViewById<View>(R.id.tvWeed) as TextView
        val tvMold = findViewById<View>(R.id.tvMold) as TextView

        val tvLoc = findViewById<View>(R.id.tvLoc) as TextView

        val retrofit = Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(PollenService::class.java)
        lat = latString
        lon = lonString
        Log.d("test2", latString)
        Log.d("test2", lonString)
        val call = service.getPollenData(lat, lon, key)
        call.enqueue(object : Callback<PollenData> {
            override fun onResponse(call: Call<PollenData>, response: Response<PollenData>) {
                if (response.code() == 200) {
                    val pollenResponse = response.body()!!
                    Log.d("ResponsePollen", "${pollenResponse.data[0]}")

                    if(pollenResponse.data[0].predominant_pollen_type.equals("Trees"))
                        dominant="Drzewa"
                    else if(pollenResponse.data[0].predominant_pollen_type.equals("Weeds"))
                        dominant="Chwasty"
                    else if(pollenResponse.data[0].predominant_pollen_type.equals("Grasses"))
                        dominant="Trawy"
                    else
                        dominant="Pleśnie"

                    tvDominant.text = "${dominant}"
                    tvGrass.text = "Trawy: ${pollenResponse.data[0].pollen_level_grass}"
                    tvTree.text = "Drzewa: ${pollenResponse.data[0].pollen_level_tree}"
                    tvWeed.text = "Chwasty: ${pollenResponse.data[0].pollen_level_weed}"
                    tvMold.text = "Pleśń: ${pollenResponse.data[0].mold_level}"
                    tvLoc.text = "Twoja lokacja: ${lat}, ${lon}"

                }
            }

            override fun onFailure(call: Call<PollenData>, t: Throwable) {
                Log.d("ResponsePollen", "$t");
            }
        })
    }

    companion object {
        var BaseUrl = "http://api.weatherbit.io/"
        var key = "524796596d924434ba70c07acc61c23c"
        var lat = "0"
        var lon = "0"
    }
}
