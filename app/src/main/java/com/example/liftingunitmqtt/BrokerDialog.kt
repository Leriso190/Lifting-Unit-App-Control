package com.example.liftingunitmqtt

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources

/*
 * Dialog window that is to be shown on application startup.
 * Lets the user choose an MQTT broker to try to establish connection with it.
 */
class BrokerDialog(context: Context) : Dialog(context) {

    // GUI Elements
    private var tvInfo: TextView? =
        null // Info text for User. Does not need to be interacted with programmatically.
    private var btnOktopi: Button? = null // function: connect to SFM broker
    private var btnHive: Button? = null // function: connect to HiveMQ broker
    private var etUserDefined: EditText? = null // get input for user-defined broker
    private var btnUserDefined: Button? = null // function: connect to user-defined broker
    private var btnCancel: Button? = null // function: dismiss dialog without connection

    /*
     * Includes every necessary LOC to set dialog window behavior.
     * Includes Dialog.show() function.
     * Important aspects:
     * - Dialog options
     * - Initialisation of GUI Elements
     * - OnClickListeners for all four buttons
     */
    fun setup() {
        //dialog options (content, background, layout, cancelable)
        setContentView(R.layout.dialog_broker)
        window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                context,
                R.drawable.popup_menu_background
            )
        )
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setCancelable(false)

        // initialisation of GUI Elements
        tvInfo = findViewById(R.id.tvInfo)
        btnOktopi = findViewById(R.id.btnOktopi)
        btnHive = findViewById(R.id.btnHive)
        etUserDefined = findViewById(R.id.etUserDefined)
        btnUserDefined = findViewById(R.id.btnUserDefined)
        btnCancel = findViewById(R.id.btnCancel)

        // onClickListeners for all four buttons
        btnOktopi!!.setOnClickListener {
            // establish connection to SFM MQTT Broker and dismiss dialog.
            MqttHelperObject.broker = "tcp://exampleBroker:1883"
            MqttHelperObject.initialise(context)
            MqttHelperObject.connect()
            MqttHelperObject.setCallback()
            dismiss()
        }

        btnHive!!.setOnClickListener {
            // establish connection to (public) HiveMQ Broker and dismiss dialog.
            MqttHelperObject.broker = "tcp://broker.hivemq.com:1883"
            MqttHelperObject.initialise(context)
            MqttHelperObject.connect()
            MqttHelperObject.setCallback()
            dismiss()
        }

        btnUserDefined!!.setOnClickListener {
            // try to establish connection to user defined MQTT Broker and dismiss dialog.
            if (etUserDefined!!.text.isNotEmpty()) {
                MqttHelperObject.broker = "tcp://${etUserDefined!!.text}:1883"
                MqttHelperObject.initialise(context)
                MqttHelperObject.connect()
                MqttHelperObject.setCallback()
                dismiss()
            } else {
                Toast.makeText(context, "Please enter a broker address.", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel!!.setOnClickListener {
            // dismiss dialog without trying to connect to any MQTT brokers.
            dismiss()
        }

        // show dialog after setup is complete
        show()
    }
}