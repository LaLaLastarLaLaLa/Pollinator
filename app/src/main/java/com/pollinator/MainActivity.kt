package com.pollinator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

var plantListData : ArrayList<PlantBean> = ArrayList()
var familyListData : ArrayList<String> = ArrayList()
var familyToGenusMap : MutableMap<String,ArrayList<String>> = mutableMapOf()
var genusToSpeciesMap : MutableMap<String,ArrayList<String>> = mutableMapOf()

class MainActivity : AppCompatActivity() {
    private lateinit var mapButton : Button
    private lateinit var cameraButton : Button
    private lateinit var albumButton : Button
    private lateinit var plantRv : RecyclerView
    private lateinit var adapter: PlantListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        initPlantList()
        initView()
        setListener()
    }
    private fun initPlantList(){
        val fileTree = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Pollinator/").walk()
        fileTree.maxDepth(4)
            .filter { it.isFile }
            .filter{it.extension in listOf("png","jpeg","jpg")}
            .filter { it.name.split('_').size==9}
            .forEach {
                var path = it.path
                path = path.substring(path.indexOf("/Pictures/Pollinator/")+21)
                val info = path.split('/')
                val info2 = it.nameWithoutExtension.split('_')
                if(info[0] !in familyListData) {
                    familyListData.add(info[0])
                    familyToGenusMap[info[0]] = ArrayList()
                }
                if(info[1] !in familyToGenusMap[info[0]]!!){
                    familyToGenusMap[info[0]]!!.add(info[1])
                    genusToSpeciesMap[info[1]]=ArrayList()
                }
                if(info[2] !in genusToSpeciesMap[info[1]]!!){
                    genusToSpeciesMap[info[1]]!!.add(info[2])
                }
                plantListData.add(PlantBean(
                    info2[0]+'/'+info2[1]+'/'+info2[2],
                    info2[3]+':'+info2[4]+":"+info2[5],
                    info[0],
                    info[1],
                    info[2],
                    OpenLocationCode(info2[6]),
                    info2[7].toInt(),
                    info2[8].toInt()
                ))
            }
    }
    private fun initView() {
        plantRv = findViewById(R.id.top_plant_list)
        mapButton = findViewById(R.id.bottom_button_map)
        cameraButton = findViewById(R.id.bottom_button_camera)
        albumButton=findViewById(R.id.bottom_button_album)
        adapter = PlantListAdapter(plantListData)
        plantRv.adapter=adapter
        plantRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
    }

    private fun setListener(){
        mapButton.setOnClickListener{
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        cameraButton.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
        albumButton.setOnClickListener{
            val intent = Intent(this, GuessActivity::class.java)
            startActivity(intent)
        }
        adapter.setOnItemClickListener(object : PlantListAdapter.OnItemClickListener {
            override fun onItemClick(view : View, position : Int ){

            }
        })
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults:
        IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).apply {
            }.toTypedArray()
    }
}