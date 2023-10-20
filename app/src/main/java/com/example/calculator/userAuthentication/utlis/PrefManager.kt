package com.example.calculator.userAuthentication.utlis

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

    private val sharedPref: SharedPreferences
    private val editor:SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences("Aditya",Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

        fun name(key:String,value:String){
            editor.putString(key,value)
                .apply()
        }

    fun email(key:String,value:String){
        editor.putString(key,value)
            .apply()
    }
        fun password(key:String,value:String){
            editor.putString(key,value)
                .apply()
        }

        fun checkLogin(key: String,value: Boolean){
            editor.putBoolean(key, value)
                .apply()
        }

        fun getValue(key: String): String? {
            return sharedPref.getString(key,null)
        }

        fun getBoolean(key: String):Boolean{
            return sharedPref.getBoolean(key, false)
        }

        fun clear(){
                editor.clear()
                    .apply()
        }


}