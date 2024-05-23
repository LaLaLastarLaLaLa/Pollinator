package com.pollinator

import com.google.android.gms.maps.model.LatLng

class OpenLocationCode{
    var latLng : LatLng
    var code : String
    private val codeList= arrayOf('2', '3', '4', '5','6', '7', '8', '9','C', 'F', 'G', 'H','J', 'M', 'P', 'Q','R', 'V', 'W', 'X')
    constructor(latitude:Double,longitude:Double) {
        this.latLng=LatLng(latitude,longitude)
        code=""
        val latitudeCode= doubleToBase20(latitude+90.0)
        val longitudeCode = doubleToBase20(longitude+180.0)

        var i = 0
        while(i<4){
            code+=latitudeCode[i]
            code+=longitudeCode[i]
            i++
        }
        code+="+"
        code+=latitudeCode[i]
        code+=longitudeCode[i]
        val acLa = codeList.indexOf(latitudeCode[5])/4
        val acLo = codeList.indexOf(longitudeCode[5])/5
        code+=codeList[acLa*4+acLo]
    }
    constructor(code : String) {
        this.code=code
        val acIndex= codeList.indexOf(code[11])
        val acLa = acIndex/4
        val acLo = acIndex%4
        //longitude: code[10],code[7],code[5],code[3],code[1]
        //latitude: code[9],code[6],code[4],code[2],code[0]
        val latitudeCode : Array<Char> = Array(6) { '0' }
        val longitudeCode : Array<Char> = Array(6) { '0' }
        latitudeCode[0]=code[0]
        latitudeCode[1]=code[2]
        latitudeCode[2]=code[4]
        latitudeCode[3]=code[6]
        latitudeCode[4]=code[9]
        latitudeCode[5]=codeList[acLa]
        longitudeCode[0]=code[1]
        longitudeCode[1]=code[3]
        longitudeCode[2]=code[5]
        longitudeCode[3]=code[7]
        longitudeCode[4]=code[10]
        longitudeCode[5]=codeList[acLo]
        this.latLng=LatLng(base20ToDouble(latitudeCode)-90,base20ToDouble(longitudeCode)-180)
    }
    private fun doubleToBase20(num : Double) : Array<Char>{
        var integer= num.toInt()
        var decimal = num - integer
        val result : Array<Char> = Array(6) { '0' }
        result[1]=codeList[integer%20]
        integer/=20
        result[0]=codeList[integer%20]
        decimal*=20
        result[2]=codeList[decimal.toInt()]
        decimal-=decimal.toInt()
        decimal*=20
        result[3]=codeList[decimal.toInt()]
        decimal-=decimal.toInt()
        decimal*=20
        result[4]=codeList[decimal.toInt()]
        decimal-=decimal.toInt()
        decimal*=20
        result[5]=codeList[decimal.toInt()]
        return result
    }
    private fun base20ToDouble(code : Array<Char>) : Double{
        var result = 0.0
        var i = 5
        while(i>=0){
            result+=codeList.indexOf(code[i])
            result/=20
            i--
        }
        return result*400
    }
}