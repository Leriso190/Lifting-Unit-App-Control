package com.example.liftingunitmqtt

class MessageBuilder {
    companion object {
        //builds a JSON Message to move a single step motor
        //needs the number of the motor, the angle for the rotation in degrees and the information in which direction the motor should move (clockwise or counter clockwise)
        fun buildMessageSingleStepMotor(
            stepMotorNumber: Int,
            angleDegrees: Int,
            isClockwise: Boolean
        ): String {
            var message: String
            message = "{\n" +
                    "        \"steppermotor\" : $stepMotorNumber,\n" +
                    "        \"angleDegrees\" : $angleDegrees,\n" +
                    "        \"stepDivider\" : 4,\n" +
                    "        \"isClockwise\" : $isClockwise\n" +
                    "}\n"
            return message
        }

        //builds a JSON Message to move the front step motors
        //needs the angle for the rotation in degrees and the information in which direction the motors should move (release or or fasten)
        fun buildMessageFrontStepMotors(angleDegrees: Int, release: Boolean): String {
            var message: String
            message = "{\n" +
                    "        \"steppermotor1\" : 3,\n" +
                    "        \"steppermotor2\" : 4,\n" +
                    "        \"angleDegrees\" : $angleDegrees,\n" +
                    "        \"stepDivider\" : 4,\n" +
                    "        \"release\" : $release\n" +
                    "}\n"
            return message
        }

        //builds a JSON Message to move the lift table
        //needs information how many milliseconds the lift table needs to move and if it needs to upwards or downwards
        fun buildMessageLiftTable(timeMillis: Int, up: Boolean): String {
            var message: String
            message = "{\n" +
                    "        \"up\" : $up,\n" +
                    "        \"movingTimeMillis\" : $timeMillis\n" +
                    "}"
            return message
        }

        // builds a message to change the color of the RGB LED
        // needs the information of the color indicated by one letter
        fun buildMessageRGBLED(color: String): String {
            var message: String = ""
            if (color.equals("red")) {
                message = "r"
            } else if (color.equals("green")) {
                message = "g"
            } else if (color.equals("blue")) {
                message = "b"
            }
            return message
        }


    }


}