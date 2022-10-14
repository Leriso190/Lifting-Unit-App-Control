package com.example.liftingunitmqtt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

//fragment that contains all sensor data
class OverviewFragment : Fragment() {

    //text view variables
    lateinit var tvSwitch1: TextView
    private lateinit var tvSwitch1Header: TextView
    lateinit var tvSwitch2: TextView
    private lateinit var tvSwitch2Header: TextView
    lateinit var tvUltrasoundBarrier: TextView
    private lateinit var tvUltrasoundBarrierHeader: TextView
    lateinit var tvEmergencyStop: TextView
    private lateinit var tvEmergencyStopHeader: TextView
    lateinit var tvLightBarrier: TextView
    private lateinit var tvLightBarrierHeader: TextView
    lateinit var tvConnectionStatus: TextView
    private lateinit var tvConnectionStatusHeader: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // GUI elements
        tvSwitch1 = view.findViewById(R.id.tvSwitch1)
        tvSwitch1Header = view.findViewById(R.id.tvSwitch1Header)
        tvSwitch2 = view.findViewById(R.id.tvSwitch2)
        tvSwitch2Header = view.findViewById(R.id.tvSwitch2Header)
        tvUltrasoundBarrier = view.findViewById(R.id.tvUltrasoundBarrier)
        tvUltrasoundBarrierHeader = view.findViewById(R.id.tvUltrasoundBarrierHeader)
        tvEmergencyStop = view.findViewById(R.id.tvEmergencyStop)
        tvEmergencyStopHeader = view.findViewById(R.id.tvEmergencyStopHeader)
        tvLightBarrier = view.findViewById(R.id.tvLightBarrier)
        tvLightBarrierHeader = view.findViewById(R.id.tvLightBarrierHeader)
        tvConnectionStatus = view.findViewById(R.id.tvConnectionStatus)
        tvConnectionStatusHeader = view.findViewById(R.id.tvConnectionStatusHeader)

        try {
            MqttHelperObject.editSensorDataTextViews()
        } catch (exception: UninitializedPropertyAccessException) {
            exception.printStackTrace()
        }

    }
}