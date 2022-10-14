package com.example.liftingunitmqtt

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity

class SingleStepMotorActivity : AppCompatActivity() {
    //topic for publishing messages
    var stepperTopic = "SFM/LiftingUnitController/Tests/MoveStepperMotor"

    //global variables angle of step motor
    var selectedAngleDegrees = 0
    var currentAngleDegrees = 0

    companion object {
        const val STEP_MOTOR_NUMBER = "STEP_MOTOR_NUMBER"
    }

    //step motor number to distinguish the two step motors at the back
    private var stepMotorNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_step_motor)
        //set step motor number
        stepMotorNumber = intent.getIntExtra(STEP_MOTOR_NUMBER, 0)

        //initialise overview fragment
        MqttHelperObject.dialog = EmergencyStopDialog(this)
        supportFragmentManager.beginTransaction().apply {
            MqttHelperObject.overviewFragment = OverviewFragment()
            replace(R.id.fcvOverviewSingleStepMotor, MqttHelperObject.overviewFragment!!)
            commit()
        }

        // GUI elements
        val npDegree: NumberPicker = findViewById(R.id.npDegree)
        val rbtnClockwise: RadioButton = findViewById(R.id.rbtnClockwise)
        val btnStart: Button = findViewById(R.id.btnStart)

        // number picker initialization
        val validDegrees: MutableList<String> = mutableListOf()
        for (i in 0..36) {
            validDegrees.add((i * 10).toString())
        }
        npDegree.minValue = 0
        npDegree.maxValue = 36
        npDegree.displayedValues = validDegrees.toTypedArray()

        // radio button initialization
        rbtnClockwise.isChecked = true

        // button funtionality
        btnStart.setOnClickListener {
            val isClockwise = rbtnClockwise.isChecked
            //set angle for moving based on
            selectedAngleDegrees = npDegree.value * 10
            Log.d(
                MainActivity.TAG,
                "stepMotorNumber: $stepMotorNumber\tselectedAngleDegrees: $selectedAngleDegrees"
            )

            //publish message to move step motor
            MqttHelperObject.publish(
                stepperTopic, MessageBuilder.buildMessageSingleStepMotor(
                    stepMotorNumber, selectedAngleDegrees, isClockwise
                )
            )
            //set current angle with the following logic
            if (isClockwise) {
                currentAngleDegrees += selectedAngleDegrees
            } else {
                currentAngleDegrees -= selectedAngleDegrees
            }
            if (currentAngleDegrees < 0) {
                currentAngleDegrees += 360
            }
            //current angle always stays in an interval of 360°
            currentAngleDegrees = currentAngleDegrees % 360

        }
    }

    //move step motor lever back to 0° when leaving the activity
    override fun onDestroy() {
        super.onDestroy()
        if (currentAngleDegrees != 0) {
            if (currentAngleDegrees >= 180) {
                MqttHelperObject.publish(
                    stepperTopic,
                    MessageBuilder.buildMessageSingleStepMotor(
                        stepMotorNumber,
                        360 - currentAngleDegrees,
                        true
                    )
                )
            } else {
                MqttHelperObject.publish(
                    stepperTopic,
                    MessageBuilder.buildMessageSingleStepMotor(
                        stepMotorNumber,
                        currentAngleDegrees,
                        false
                    )
                )
            }
        }
    }
}