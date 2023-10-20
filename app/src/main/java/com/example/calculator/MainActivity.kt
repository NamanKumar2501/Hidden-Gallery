package com.example.calculator


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.calculator.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityMainBinding
    var lastNumeric = false
    var stateError = false
    var lastDot = false

    private lateinit var expression: Expression

  /*  @RequiresApi(Build.VERSION_CODES.O)
    val currentTime = LocalTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val minutes = currentTime.minute*/
   // Toast.makeText(this, "Current time is $minutes", Toast.LENGTH_SHORT).show()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()
        // Get the current updated minutes
        val minutes = calendar.get(Calendar.MINUTE)

      //  Toast.makeText(this, "Current time is $minutes", Toast.LENGTH_SHORT).show()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

    }

    fun onAllClearClick(view: View) {

        binding.dataTv.text = ""
        binding.resultTv.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false

        binding.resultTv.visibility = View.GONE

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onEqualClick(view: View) {

        onEqual()
        binding.dataTv.text = binding.resultTv.text.toString().drop(1)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onDigitClick(view: View) {
        if (stateError) {
            binding.dataTv.text = (view as Button).text
            stateError = false
        } else {
            binding.dataTv.append((view as Button).text)
        }

        lastNumeric = true
        onEqual()

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onOperatorClick(view: View) {

        if (!stateError && lastNumeric) {
            binding.dataTv.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            onEqual()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onBackClick(view: View) {
        binding.dataTv.text = binding.dataTv.text.toString().dropLast(1)

        try {
            val lastChar = binding.dataTv.text.toString().last()

            if (lastChar.isDigit()) {
                onEqual()
            }
        } catch (e: Exception) {
            binding.resultTv.text = ""
            binding.resultTv.visibility = View.GONE
            Log.e("last char error", e.toString())
        }
    }


    fun onClearClick(view: View) {
        binding.dataTv.text = ""
        lastNumeric = false
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onEqual() {
        if (lastNumeric && !stateError) {
            val txt = binding.dataTv.text.toString()

            try {
                expression = ExpressionBuilder(txt).build()
            } catch (e: Exception) {
                Log.e("error", e.toString())
            }


            try {
                sharedPreferences = getSharedPreferences("SwitchState", Context.MODE_PRIVATE)
                val switchState = sharedPreferences.getBoolean("SWITCH_STATE", false)


                val result = expression.evaluate()

                val calendar = Calendar.getInstance()
                // Get the current updated minutes
                val minutes = calendar.get(Calendar.MINUTE)

                if((minutes.toDouble() ==result)&&(switchState==true)){
                    val intent = Intent(this,LockerActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else{
                    binding.resultTv.visibility = View.VISIBLE
                    binding.resultTv.text = "= " + result.toString()
                }


            } catch (ex: ArithmeticException) {

                Log.e("evaluate error", ex.toString())
                binding.resultTv.text = "Error"
                stateError = true
                lastNumeric = false

            }

        }

    }

    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.calculator_home_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){

            R.id.settings ->{
                val intent = Intent(this,SettingActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }



}