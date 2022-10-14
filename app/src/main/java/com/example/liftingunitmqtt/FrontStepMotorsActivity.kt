package com.example.liftingunitmqtt

import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StepMotorActivity : AppCompatActivity() {
    var stepperTopic = "SFM/LiftingUnitController/Tests/MoveStepperMotors"

    //angle variables
    var selectedAngleDegrees = 0
    var currentAngleDegrees = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_step_motors)

        // overview fragment
        MqttHelperObject.dialog = EmergencyStopDialog(this)
        supportFragmentManager.beginTransaction().apply {
            MqttHelperObject.overviewFragment = OverviewFragment()
            replace(R.id.fcvOverviewFrontStepMotor, MqttHelperObject.overviewFragment!!)
            commit()
        }

        // GUI elements
        val npDegree: NumberPicker = findViewById(R.id.npDegree)
        val btnRelease: Button = findViewById(R.id.btnRelease)
        val btnFasten: Button = findViewById(R.id.btnFasten)

        // number picker initialization
        val validDegrees: MutableList<String> = mutableListOf()
        for (i in 0..9) {
            validDegrees.add((i * 10).toString())
        }
        npDegree.minValue = 0
        npDegree.maxValue = 9
        npDegree.displayedValues = validDegrees.toTypedArray()

        // functions
        btnRelease.setOnClickListener {
            selectedAngleDegrees = npDegree.value * 10
            if (currentAngleDegrees + selectedAngleDegrees > 180) {
                Toast.makeText(
                    this,
                    "Caution: You can not move the lever beyond an angle of 180°",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                MqttHelperObject.publish(
                    stepperTopic,
                    MessageBuilder.buildMessageFrontStepMotors(
                        selectedAngleDegrees,
                        true
                    )
                )
                currentAngleDegrees += selectedAngleDegrees
            }
        }

        btnFasten.setOnClickListener {
            selectedAngleDegrees = npDegree.value * 10
            if (currentAngleDegrees - selectedAngleDegrees < 0) {
                Toast.makeText(
                    this,
                    "Caution: You can not move the lever under an angle of 0°",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                MqttHelperObject.publish(
                    stepperTopic,
                    MessageBuilder.buildMessageFrontStepMotors(
                        selectedAngleDegrees,
                        false
                    )
                )
                currentAngleDegrees -= selectedAngleDegrees
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (currentAngleDegrees != 0) {
            MqttHelperObject.publish(
                stepperTopic,
                MessageBuilder.buildMessageFrontStepMotors(currentAngleDegrees, false)
            )
        }
    }
}