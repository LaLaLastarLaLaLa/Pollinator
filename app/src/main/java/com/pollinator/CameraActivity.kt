package com.pollinator

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.RectF
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.pollinator.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class CameraActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var camera: Camera
    private lateinit var cameraExecutor: ExecutorService
    var altitude = 0.0
    var latitude = 0.0
    var longitude = 0.0
    var accuracy : Float = -1.0F


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            for (location in p0.locations){
                accuracy = location.accuracy
                latitude = location.latitude
                altitude = location.altitude
                longitude = location.longitude
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
        viewBinding.imageTorchButton.setOnClickListener {
            if (camera.cameraInfo.torchState.value == TorchState.ON) {
                camera.cameraControl.enableTorch(false)
            } else {
                camera.cameraControl.enableTorch(true)
            }
        }
        cameraExecutor = Executors.newSingleThreadExecutor()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.Builder(5000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }


    @SuppressLint("MissingPermission")
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return
        val fileName = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,fileName)
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/Pollinator")
        }

        val outputOption = ImageCapture.OutputFileOptions.Builder(
                    contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues).build()
        imageCapture.takePicture(
            outputOption,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                }

                override fun onError(exception: ImageCaptureException) {
                }

            }
        )

        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setTitle("Save Image")
        alert.setMessage("Please Input Family,Genus,Species")
        val inputFamily = EditText(this)
        inputFamily.hint = "Family"
        val inputGenus = EditText(this)
        inputGenus.hint = "Genus"
        val inputSpecies = EditText(this)
        inputSpecies.hint = "Species"
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(inputFamily)
        linearLayout.addView(inputGenus)
        linearLayout.addView(inputSpecies)
        alert.setView(linearLayout)
        alert.setPositiveButton("Ok"
        ) { _, _ ->
            var family = inputFamily.text.toString()
            if(family=="") {
                family="Unknown"
            }
            var genus = inputGenus.text.toString()
            if(genus=="") {
                genus="Unknown"
            }
            var species = inputSpecies.text.toString()
            if(species=="") {
                species="Unknown"
            }

            val storePath = Environment.getExternalStorageDirectory().toString() + "/Pictures/Pollinator/"
            val familyPath= File("$storePath$family")
            if(!familyPath.exists()){
                familyPath.mkdir()
            }
            val genusPath = File("$storePath$family/$genus")
            if(!genusPath.exists()){
                genusPath.mkdir()
            }
            val speciesPath = File("$storePath$family/$genus/$species")
            if(!speciesPath.exists()){
                speciesPath.mkdir()
            }
            val openLocationCode = OpenLocationCode(latitude,longitude)
            val newFileName= family+"/"+genus+"/"+species+"/"+fileName+"_"+openLocationCode.code+"_"+altitude.toInt().toString()+"_"+accuracy.toInt().toString()


            val src = File("$storePath$fileName.png")
            val dst = File("$storePath$newFileName.png")

            src.renameTo(dst)
            if(family !in familyListData) {
                familyListData.add(family)
                familyToGenusMap[family] = ArrayList()
            }
            if(genus !in familyToGenusMap[family]!!){
                familyToGenusMap[family]!!.add(genus)
                genusToSpeciesMap[genus]=ArrayList()
            }
            if(species !in genusToSpeciesMap[genus]!!){
                genusToSpeciesMap[genus]!!.add(species)
            }
            val time = fileName.split('_')
            plantListData.add(PlantBean(
                time[0]+'/'+time[1]+'/'+time[2],
                time[3]+':'+time[4]+":"+time[5],
                family,
                genus,
                species,
                openLocationCode,
                altitude.toInt(),
                accuracy.toInt()
            ))
        }
        alert.setNegativeButton("Cancel"
        ) { _, _ ->
        }
        alert.show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                camera=cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                setUpZoomTapToFocus()

            } catch(_: Exception) {
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun setUpZoomTapToFocus(){
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = camera.cameraInfo.zoomState.value?.zoomRatio  ?: 1f
                val delta = detector.scaleFactor
                camera.cameraControl.setZoomRatio(currentZoomRatio * delta)
                return true
            }
        }

        val scaleGestureDetector = ScaleGestureDetector(this,listener)

        viewBinding.viewFinder.setOnTouchListener { view, event ->
            scaleGestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN){
                val factory = viewBinding.viewFinder.meteringPointFactory
                val point = factory.createPoint(event.x,event.y)
                val action = FocusMeteringAction.Builder(point,FocusMeteringAction.FLAG_AF)
                    .setAutoCancelDuration(2, TimeUnit.SECONDS)
                    .build()

                val focusCircle = RectF(event.x-50,event.y-50, event.x+50,event.y+50)
                viewBinding.focusView.focusCircle = focusCircle
                viewBinding.focusView.invalidate()
                camera.cameraControl.startFocusAndMetering(action)
                view.performClick()
            }
            true
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyy_MM_dd_HH_mm_ss"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).apply {
            }.toTypedArray()
    }
}
