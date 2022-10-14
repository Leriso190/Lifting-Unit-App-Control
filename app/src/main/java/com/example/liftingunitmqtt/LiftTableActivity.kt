package com.example.liftingunitmqtt

import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LiftTableActivity : AppCompatActivity() {
    //mqtt topic to move the lift table
    var liftTableTopic = "SFM/LiftingUnitController/Tests/LiftTableWithTime"

    //set time that lift table moves in milliseconds
    var movingTimeMillis = 3000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lift_table)

        //initialise emergency stop dialog
        MqttHelperObject.dialog = EmergencyStopDialog(this)
        supportFragmentManager.beginTransaction().apply {
            MqttHelperObject.overviewFragment = OverviewFragment()
            replace(R.id.fcvOverviewLiftTable, MqttHelperObject.overviewFragment!!)
            commit()
        }

        //GUI elements
        val btnUp: ImageButton = findViewById(R.id.btnUp)
        val btnDown: ImageButton = findViewById(R.id.btnDown)

        // functions
        btnUp.setOnClickListener {
            try {
                if (MqttHelperObject.heightUltraSonicSensorStatus!! > 630) {
                    Toast.makeText(this, "Lift Table is in highest Position", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    MqttHelperObject.publish(
                        liftTableTopic,
                        MessageBuilder.buildMessageLiftTable(movingTimeMillis, true)
                    )
                    //buttons get disabled for the time the lift table moves, so that there can be no messages send while moving
                    btnDown.isEnabled = false
                    btnUp.isEnabled = false
                    Handler().postDelayed(
                        Runnable {
                            btnDown.isEnabled = true
                            btnUp.isEnabled = true
                        }, movingTimeMillis.toLong()
                    )
                }
            } catch (exception: NullPointerException) {
                Toast.makeText(this, "You can't do that right now!", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
        }

        btnDown.setOnClickListener {
            try {
                if (MqttHelperObject.heightUltraSonicSensorStatus!! < 200) {
                    Toast.makeText(this, "Lift Table is in lowest Position", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    MqttHelperObject.publish(
                        liftTableTopic,
                        MessageBuilder.buildMessageLiftTable(movingTimeMillis, false)
                    )
                    //buttons get disabled for the time the lift table moves, so that there can be no messages send while moving
                    btnDown.isEnabled = false
                    btnUp.isEnabled = false
                    Handler().postDelayed(
                        Runnable {
                            btnDown.isEnabled = true
                            btnUp.isEnabled = true
                        }, movingTimeMillis.toLong()
                    )
                }
            } catch (exception: NullPointerException) {
                Toast.makeText(this, "You can't do that right now!", Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
        }
    }
}