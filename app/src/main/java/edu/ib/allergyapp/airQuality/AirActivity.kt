package edu.ib.allergyapp.airQuality

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import edu.ib.allergyapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.provider.Settings
import android.widget.Toast

class AirActivity : AppCompatActivity() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null

    private var latString: String = "0"
    private var lonString: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_air)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun showSnackbar(
            mainTextStringId: String, actionStringId: String,
            listener: View.OnClickListener
    ) {
        Toast.makeText(this@AirActivity, mainTextStringId, Toast.LENGTH_LONG).show()
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
                this@AirActivity,
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

        val tvQuality = findViewById<View>(R.id.tvDominant) as TextView
        val tvC1 = findViewById<View>(R.id.tvC1) as TextView
        val tvC2 = findViewById<View>(R.id.tvGrass) as TextView
        val tvC3 = findViewById<View>(R.id.tvTree) as TextView
        val tvC4 = findViewById<View>(R.id.tvWeed) as TextView
        val tvC5 = findViewById<View>(R.id.tvMold) as TextView
        val tvC6 = findViewById<View>(R.id.tvC6) as TextView
        val tvC7 = findViewById<View>(R.id.tvC7) as TextView
        val tvC8 = findViewById<View>(R.id.tvC8) as TextView
        val tvLoc = findViewById<View>(R.id.tvLoc) as TextView

        val retrofit = Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val service = retrofit.create(AirService::class.java)
        lat = latString
        lon = lonString
        Log.d("test2", latString)
        Log.d("test2", lonString)
        val call = service.getAirData(lat, lon, AppId)
        call.enqueue(object : Callback<AirData> {
            override fun onResponse(call: Call<AirData>, response: Response<AirData>) {
                if (response.code() == 200) {
                    val airResponse = response.body()!!
                    Log.d("ResponseAir", "${airResponse.list[0]}")

                    if(airResponse.list[0].main.aqi==1)
                        tvQuality.setTextColor(Color.parseColor("#9DDB5C"))
                    else if(airResponse.list[0].main.aqi==2)
                        tvQuality.setTextColor(Color.parseColor("#FFA7D670"))
                    else if(airResponse.list[0].main.aqi==3)
                        tvQuality.setTextColor(Color.parseColor("#FFFFEB3B"))
                    else if(airResponse.list[0].main.aqi==4)
                        tvQuality.setTextColor(Color.parseColor("#FFFF9800"))
                    else
                        tvQuality.setTextColor(Color.parseColor("#FFDC1515"))


                    tvQuality.text = "${airResponse.list[0].main.aqi}"
                    tvC1.text = "CO: ${airResponse.list[0].components.co} [μg/m3]"
                    tvC2.text = "NH3: ${airResponse.list[0].components.nh3} [μg/m3]"
                    tvC3.text = "NO: ${airResponse.list[0].components.no} [μg/m3]"
                    tvC4.text = "NO2: ${airResponse.list[0].components.no2} [μg/m3]"
                    tvC5.text = "O3: ${airResponse.list[0].components.o3} [μg/m3]"
                    tvC6.text = "Pył PM10: ${airResponse.list[0].components.pm10} [μg/m3]"
                    tvC7.text = "Pył PM2.5: ${airResponse.list[0].components.pm2_5} [μg/m3]"
                    tvC8.text = "SO2: ${airResponse.list[0].components.so2} [μg/m3]"
                    tvLoc.text = "Twoja lokacja: ${lat}, ${lon}"

                }
            }

            override fun onFailure(call: Call<AirData>, t: Throwable) {
                Log.d("ResponseAir", "$t");
            }
        })
    }

    companion object {
        var BaseUrl = "http://api.openweathermap.org/"
        var AppId = "04538af0293472ba39dc60f47b9f13e1"
        var lat = "0"
        var lon = "0"
    }
}