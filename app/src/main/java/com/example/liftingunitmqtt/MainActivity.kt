package com.example.liftingunitmqtt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    // TAG for log messages
    companion object {
        const val TAG = "AndroidMqttClient"
    }

    //function that reloads main activity when returning to it
    override fun onResume() {
        super.onResume()
        loadFragment()
    }

    //function that initialises all relevant program logic for the main activity when main activity is opened initially
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //loads sensor data fragment
        loadFragment()

        //loads dialog that asks the user for connection information
        val brokerDialog = BrokerDialog(this)
        brokerDialog.setup()

        // GUI elements
        val btnLiftTable: FloatingActionButton = findViewById(R.id.btnLiftTable)
        val btnStepMotor1: FloatingActionButton = findViewById(R.id.btnStepMotor1)
        val btnStepMotor2: FloatingActionButton = findViewById(R.id.btnStepMotor2)
        val btnLight: FloatingActionButton = findViewById(R.id.btnLight)
        val btnSingleStepMotor1: FloatingActionButton = findViewById(R.id.btnSingleStepMotor1)
        val btnSingleStepMotor2: FloatingActionButton = findViewById(R.id.btnSingleStepMotor2)

        // site navigation
        btnLiftTable.setOnClickListener {
            startActivity(Intent(this, LiftTableActivity::class.java))
        }
        btnStepMotor1.setOnClickListener {
            startActivity(Intent(this, StepMotorActivity::class.java))
        }
        btnStepMotor2.setOnClickListener {
            startActivity(Intent(this, StepMotorActivity::class.java))
        }
        btnLight.setOnClickListener {
            startActivity(Intent(this, LedLightActivity::class.java))
        }
        btnSingleStepMotor1.setOnClickListener {
            val intent = Intent(this, SingleStepMotorActivity::class.java)
            intent.putExtra("STEP_MOTOR_NUMBER", 1)
            startActivity(intent)
        }
        btnSingleStepMotor2.setOnClickListener {
            val intent = Intent(this, SingleStepMotorActivity::class.java)
            intent.putExtra("STEP_MOTOR_NUMBER", 2)
            startActivity(intent)
        }
    }

    //function that stops Lifting Unit Client from sending data when application is not in use by sending a mqtt message
    override fun onDestroy() {
        super.onDestroy()
        MqttHelperObject.publish(
            "SFM/LiftingUnitController/Tests/StopSendSensorData",
            "Stopped Reading Sensor Data"
        )
    }

    //function that loads the fragment that shows all the sensor data
    private fun loadFragment() {
        MqttHelperObject.dialog = EmergencyStopDialog(this)
        supportFragmentManager.beginTransaction().apply {
            MqttHelperObject.overviewFragment = OverviewFragment()
            replace(R.id.fcvOverviewMain, MqttHelperObject.overviewFragment!!)
            commit()
        }
    }
}