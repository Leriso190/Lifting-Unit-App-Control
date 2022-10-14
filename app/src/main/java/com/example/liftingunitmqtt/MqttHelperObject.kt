package com.example.liftingunitmqtt

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.TextView
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


import java.io.UnsupportedEncodingException

//object that implements a mqtt client to communicate with a broker via mqtt
object MqttHelperObject {

    //variables that are necessary to establish a mqtt connection with this client
    var mqttAndroidClient: MqttAndroidClient? = null
    var broker: String = ""
    var overviewFragment: OverviewFragment? = null
    var dialog: EmergencyStopDialog? = null

    //variables to store received sensor data
    var heightUltraSonicSensorStatus: Int? = null
    private var lightBarrierStatus: Boolean? = null
    private var rollSwitch1Status: Boolean? = null
    private var rollSwitch2Status: Boolean? = null
    private var emergencyStopStatus: Boolean? = null

    //connection variables
    var isConnected: Boolean = false
    var connectionFailed = false

    //function to initialise the mqtt client
    fun initialise(context: Context) {
        val clientId = MqttClient.generateClientId()
        try {
            mqttAndroidClient = MqttAndroidClient(context, broker, clientId, Ack.AUTO_ACK)
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }
    }

    //function that connects the mqtt client with the broker that is set by the user
    fun connect() {
        try {
            //standard options for connections
            val options = MqttConnectOptions()
            options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
            val token: IMqttToken = mqttAndroidClient!!.connect(options)
            token.actionCallback = object : IMqttActionListener {
                //when the connection is successful:
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.d(MainActivity.TAG, "connection success")
                    overviewFragment!!.tvConnectionStatus.text =
                        broker.substringAfterLast('/').substringBeforeLast(':')
                    isConnected = true
                    //notify broker that the machine needs to send sensor data via publishing the following message
                    subscribe("WriteSensorData", 1)
                    //subscribe to following topic to get sensor data changes
                    publish(
                        "SFM/LiftingUnitController/Tests/SendSensorData",
                        "Start Sending Sensor Data"
                    )
                }

                //when the connection isn't successful:
                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.d(MainActivity.TAG, "connection failure")
                    //set all sensor data variables to null
                    broker = ""
                    heightUltraSonicSensorStatus = null
                    lightBarrierStatus = null
                    rollSwitch1Status = null
                    rollSwitch2Status = null
                    emergencyStopStatus = null
                    exception.printStackTrace()
                    //set offline texts where sensor data would be shown
                    setOfflineTextViews()
                    isConnected = false
                    connectionFailed = true
                }
            }
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    //function to publish a message via mqtt
    fun publish(topic: String, payload: String) {
        Thread {
            try {
                val pubToken = mqttAndroidClient!!.publish(
                    topic,
                    MqttMessage(payload.toByteArray(charset("UTF-8")))
                )
                pubToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        Log.d(MainActivity.TAG, "publish success")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        Log.d(MainActivity.TAG, "publish failure")
                    }
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    //function to subscribe to a topic via mqtt
    fun subscribe(topic: String, qos: Int) {
        Thread {
            try {
                val subToken = mqttAndroidClient!!.subscribe(topic, qos)
                subToken.actionCallback = object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        Log.d(MainActivity.TAG, "subscription success")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        Log.d(MainActivity.TAG, "subscription failure")
                    }
                }
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }.start()
    }

    //create a callback to receive and use data from the subscribed topic
    fun setCallback() {
        try {
            mqttAndroidClient!!.setCallback(object : MqttCallback {

                override fun connectionLost(throwable: Throwable) {
                    isConnected = false
                }

                override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                    //height, emergency, light barrier, first switch, second switch
                    try {
                        //converting messages to sensor data variables and edit Text views based on this data
                        convertMessageToVariables(mqttMessage.toString())
                        editSensorDataTextViews()
                        //if emergency stop is pressed pause application and print a dialog
                        dialog!!.setup()
                        if (emergencyStopStatus!! && !dialog!!.isShowing) {
                            dialog!!.show()
                        }
                        if (!emergencyStopStatus!! && dialog!!.isShowing) {
                            dialog!!.dismiss()
                        }
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }

                }

                override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                }
            })
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }

    }

    //function to convert received messages into variables
    fun convertMessageToVariables(mqttString: String) {
        val list: List<String> = listOf(*mqttString.split(",").toTypedArray())
        heightUltraSonicSensorStatus = list[0].toInt()
        emergencyStopStatus = list[1].toBoolean()
        lightBarrierStatus = list[2].toBoolean()
        rollSwitch1Status = list[3].toBoolean()
        rollSwitch2Status = list[4].toBoolean()
    }

    //function to set the text that'll be shown in text views for sensor data
    fun editSensorDataTextViews() {
        if (lightBarrierStatus != null && emergencyStopStatus != null && rollSwitch1Status != null && rollSwitch2Status != null && heightUltraSonicSensorStatus != null) {
            setTextViewEmergencyStopAndLightBarrier(
                overviewFragment!!.tvLightBarrier,
                lightBarrierStatus!!,
                true
            )
            setTextViewEmergencyStopAndLightBarrier(
                overviewFragment!!.tvEmergencyStop,
                emergencyStopStatus!!,
                false
            )
            setTextViewRollSwitches(overviewFragment!!.tvSwitch1, rollSwitch1Status!!)
            setTextViewRollSwitches(overviewFragment!!.tvSwitch2, rollSwitch2Status!!)
            overviewFragment!!.tvUltrasoundBarrier.text = "$heightUltraSonicSensorStatus mm"
        } else {
            setOfflineTextViews()
        }
        if (isConnected) {
            overviewFragment!!.tvConnectionStatus.text =
                broker.substringAfterLast('/').substringBeforeLast(':')
        }
    }

    //function to set the text that'll be shown in text views for the roll switch sensor data
    private fun setTextViewRollSwitches(textView: TextView, status: Boolean) {
        if (status) {
            textView.text = "Box in Position"
            textView.setTextColor(Color.GREEN)
        } else {
            textView.text = "Box missing"
            textView.setTextColor(Color.RED)
        }
    }

    //function to set the text that'll be shown in text views for the emergency stop sensor data and the light barrier sensor data
    private fun setTextViewEmergencyStopAndLightBarrier(
        textView: TextView,
        status: Boolean,
        isLightbarrier: Boolean
    ) {
        val tempText: String
        if (status) {
            tempText = if (isLightbarrier) {
                "Obstacle detected"
            } else {
                "Pressed"
            }
            textView.text = tempText
            textView.setTextColor(Color.RED)
        } else {
            tempText = if (isLightbarrier) {
                "No Obstacle"
            } else {
                "Not pressed"
            }
            textView.text = tempText
            textView.setTextColor(Color.GREEN)
        }
    }

    //function to set the text that'll be shown in text views for the sensor data when no connection is established
    fun setOfflineTextViews() {
        val tvList = listOf(
            overviewFragment!!.tvLightBarrier,
            overviewFragment!!.tvEmergencyStop,
            overviewFragment!!.tvSwitch1,
            overviewFragment!!.tvSwitch2,
            overviewFragment!!.tvUltrasoundBarrier,
            overviewFragment!!.tvConnectionStatus
        )
        tvList.forEach {
            it.text = "Connection failed"
        }
    }
}