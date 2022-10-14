package com.example.liftingunitmqtt

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

//class that implements the program logic to the led Activity
class LedLightActivity : AppCompatActivity() {
    //global variable topic for the mqtt message
    var ledTopic = "SFM/LiftingUnitController/Tests/ControlLED"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led_light)
        //initialise a dialog for the case that the emergency stop is pressed
        MqttHelperObject.dialog = EmergencyStopDialog(this)
        supportFragmentManager.beginTransaction().apply {
            MqttHelperObject.overviewFragment = OverviewFragment()
            replace(R.id.fcvOverviewLedLight, MqttHelperObject.overviewFragment!!)
            commit()
        }

        // GUI elements
        val btnRed: Button = findViewById(R.id.btnRed)
        val btnGreen: Button = findViewById(R.id.btnGreen)
        val btnBlue: Button = findViewById(R.id.btnBlue)

        // functions
        btnRed.setOnClickListener {
            MqttHelperObject.publish(ledTopic, MessageBuilder.buildMessageRGBLED("red"))
        }

        btnGreen.setOnClickListener {
            MqttHelperObject.publish(ledTopic, MessageBuilder.buildMessageRGBLED("green"))
        }

        btnBlue.setOnClickListener {
            MqttHelperObject.publish(ledTopic, MessageBuilder.buildMessageRGBLED("blue"))
        }
    }
}