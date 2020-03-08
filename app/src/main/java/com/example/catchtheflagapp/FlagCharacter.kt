package com.example.catchtheflagapp

import android.location.Location

class  FlagCharacter{

    var titleOfFlag:String?=null
    var messege: String? = null
    var iconOfFlag: Int? = null
    var location: Location? = null
    var isKilled: Boolean? = null

    constructor(titleOfFlag: String, message: String, iconOfFlag: Int, latitude: Double, longtitude: Double ) {

        location= Location("MyProvider")

        this.iconOfFlag = iconOfFlag
        this.messege = message
        this.titleOfFlag = titleOfFlag

        this.location?.latitude = latitude
        this.location?.longitude = longtitude
    }
}