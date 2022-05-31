package edu.ib.allergyapp.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import edu.ib.allergyapp.R

class OCRActivity : AppCompatActivity() {

    private lateinit var mCameraSource: CameraSource
    private lateinit var textRecognizer: TextRecognizer
    private var data: String = ""

    private val PERMISSION_REQUEST_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)
        startCameraSource()
        supportActionBar?.hide()
    }

    private fun startCameraSource() {

        val cameraPreview = findViewById<View>(R.id.cameraPreview) as SurfaceView

        // Create text Recognizer
        textRecognizer = TextRecognizer.Builder(this).build()

        if (!textRecognizer.isOperational) {
            Toast.makeText(this, "Dependencies are not loaded yet...please try after few moment!!",
                    Toast.LENGTH_SHORT).show()
            Log.d("mg", "Dependencies are downloading....try after few moment")
            return
        }

        // Init camera source to use high resolution and auto focus
        mCameraSource = CameraSource.Builder(applicationContext, textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .setRequestedFps(2.0f)
                .build()

        cameraPreview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (isCameraPermissionGranted()) {
                        mCameraSource.start(cameraPreview.holder)
                    } else {
                        requestForPermission()
                    }
                } catch (e: Exception) {
                    Log.d( "Error", "Error:  ${e.message}")
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<TextBlock>) {
                val items = detections.detectedItems
                val tvResult = findViewById<View>(R.id.tvResult) as TextView

                if (items.size() <= 0) {
                    return
                }

                tvResult.post {
                    val stringBuilder = StringBuilder()
                    for (i in 0 until items.size()) {
                        val item = items.valueAt(i)
                        stringBuilder.append(item.value)
                        stringBuilder.append("\n")
                    }
                    tvResult.text = stringBuilder.toString()
                    data = stringBuilder.toString()
                    tvResult.movementMethod = ScrollingMovementMethod()
                }
            }
        })
    }


    fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }


    private fun requestForPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val cameraPreview = findViewById<View>(R.id.cameraPreview) as SurfaceView

        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                mCameraSource.start(cameraPreview.holder)
            } else {
                Toast.makeText(this, "Permission need to grant", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    fun onAnalyzeClick(view: View) {
        val intent = Intent(this@OCRActivity,AnalyzeActivity::class.java)
        intent.putExtra("data",data)
        startActivity(intent)
    }
}