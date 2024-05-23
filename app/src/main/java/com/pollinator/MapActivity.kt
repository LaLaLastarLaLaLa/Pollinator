package com.pollinator

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity() {
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var familySpinner: Spinner
    private lateinit var genusSpinner: Spinner
    private lateinit var speciesSpinner: Spinner
    private lateinit var markButton : Button
    private lateinit var clearButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        initView()
        setListener()
    }
    private fun initView(){
        mapFragment = (supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment)!!
        familySpinner=findViewById(R.id.family_spinner)
        genusSpinner=findViewById(R.id.genus_spinner)
        speciesSpinner=findViewById(R.id.species_spinner)
        markButton=findViewById(R.id.mark_button)
        clearButton=findViewById(R.id.clear_button)

        familySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arrayOf("*")+familyListData)
        genusSpinner.isEnabled = false
        speciesSpinner.isEnabled = false
    }
    private fun setListener() {
        markButton.setOnClickListener {
            val family= if(familySpinner.selectedItem == null) "" else familySpinner.selectedItem.toString()
            val genus= if(genusSpinner.selectedItem == null) "" else genusSpinner.selectedItem.toString()
            val species = if(speciesSpinner.selectedItem == null) "" else speciesSpinner.selectedItem.toString()
            showMaker(family,genus,species)
        }
        clearButton.setOnClickListener {
            mapFragment.getMapAsync { googleMap ->
                googleMap.clear()
            }
        }
        familySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                genusSpinner.isEnabled = false
                genusSpinner.setSelection(0)
                speciesSpinner.isEnabled = false
                genusSpinner.setSelection(0)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position!=0) {
                    genusSpinner.isEnabled = true
                    genusSpinner.adapter = ArrayAdapter(
                        parent!!.context,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf("*") +familyToGenusMap[familySpinner.selectedItem.toString()]!!
                    )
                }else{
                    genusSpinner.isEnabled = false
                    speciesSpinner.isEnabled = false
                    genusSpinner.setSelection(0)
                    speciesSpinner.setSelection(0)
                }
            }
        }
        genusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                speciesSpinner.isEnabled = false
                speciesSpinner.setSelection(0)
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position!=0) {
                    speciesSpinner.isEnabled = true
                    speciesSpinner.adapter = ArrayAdapter(
                        parent!!.context,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrayOf("*") + genusToSpeciesMap[genusSpinner.selectedItem.toString()]!!
                    )
                }else{
                    speciesSpinner.isEnabled = false
                    speciesSpinner.setSelection(0)
                }
            }
        }
    }
    private fun showMaker(family: String, genus: String, species: String){
        val showList : ArrayList<PlantBean> = ArrayList()
        for(plant in plantListData){
            if(family=="*" || family==""){
                showList.add(plant)
            }else if(family==plant.family){
                if(genus=="*" || genus==""){
                    showList.add(plant)
                }else if(genus==plant.genus){
                    if(species=="*" || species=="" || species==plant.species){
                        showList.add(plant)
                    }
                }
            }
        }
        mapFragment.getMapAsync { googleMap ->
            showList.forEach{ plant ->
                //val marker = googleMap.addMarker(
                googleMap.addMarker(
                    MarkerOptions()
                        .title(plant.genus+"_"+plant.species)
                        .position(plant.openLocationCode.latLng)
                )
            }
        }
    }
}