package com.pollinator

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import java.io.File

class GuessActivity : AppCompatActivity() {
    private lateinit var imageView : ImageView
    private lateinit var familyInput: EditText
    private lateinit var genusInput: EditText
    private lateinit var speciesInput: EditText
    private lateinit var confirmButton : Button
    private lateinit var nextButton : Button
    private lateinit var resultList : List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guess)

        initView()
        setListener()
    }
    private fun draw() : List<String>{
        val family = familyListData[(0 until familyListData.size).random()]
        val genus = familyToGenusMap[family]!![(0 until familyToGenusMap[family]!!.size).random()]
        val species = genusToSpeciesMap[genus]!![(0 until genusToSpeciesMap[genus]!!.size).random()]

        val fileTree = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Pollinator/$family/$genus/$species").walk()
        val randomUri : ArrayList<Uri> = arrayListOf()
        fileTree.maxDepth(1)
            .filter { it.isFile }
            .filter{it.extension in listOf("png","jpeg","jpg")}
            .filter { it.name.split('_').size==9}
            .forEach {
                randomUri.add(it.toUri())
            }
        imageView.setImageURI(randomUri[(0 until randomUri.size).random()])
        return listOf(family,genus,species)
    }
    private fun initView(){
        imageView = findViewById(R.id.image_show)
        familyInput=findViewById(R.id.family_input)
        genusInput=findViewById(R.id.genus_input)
        speciesInput=findViewById(R.id.species_input)
        confirmButton=findViewById(R.id.confirm_button)
        nextButton=findViewById(R.id.next_button)
        resultList = draw()
    }
    private fun setListener() {
        confirmButton.setOnClickListener {
            if(familyInput.text.toString() == resultList[0]){
                familyInput.setTextColor(getColor(R.color.green))
            }else{
                familyInput.setText(resultList[0])
                familyInput.setTextColor(getColor(R.color.red))
            }

            if(genusInput.text.toString() == resultList[1]){
                genusInput.setTextColor(getColor(R.color.green))
            }else{
                genusInput.setText(resultList[1])
                genusInput.setTextColor(getColor(R.color.red))
            }

            if(speciesInput.text.toString() == resultList[2]){
                speciesInput.setTextColor(getColor(R.color.green))
            }else{
                speciesInput.setText(resultList[2])
                speciesInput.setTextColor(getColor(R.color.red))
            }
        }
        nextButton.setOnClickListener {
            resultList=draw()
            familyInput.setText("")
            familyInput.setTextColor(getColor(R.color.black))
            genusInput.setText("")
            genusInput.setTextColor(getColor(R.color.black))
            speciesInput.setText("")
            speciesInput.setTextColor(getColor(R.color.black))
        }

    }
}