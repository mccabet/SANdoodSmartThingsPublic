/**
 *  Zwave Button
 *  based on
 *  Z-Wave Door/Window Sensor by SmartThings
 *
 *  Author: Brian Dahlem
 *  Date: 2014-8-3
 */

// for the UI
metadata {
	// Automatically generated. Make future change here.
	definition (name: "Z-Wave Button", namespace: "bdahlem", author: "Brian Dahlem") {
		capability "Button"
		capability "Sensor"
		capability "Battery"
        capability "Momentary"

		fingerprint deviceId: "0x2001", inClusters: "0x30,0x80,0x84,0x70,0x85,0x86,0x72"
	}

	// simulator metadata
	simulator {
		// status messages
		status "released":  "command: 2001, payload: FF"
		status "pushed": "command: 2001, payload: 00"
	}

	// UI tile definitions
	tiles {
		standardTile("button", "device.button", inactiveLabel: false, width: 2, height: 2) {
			state "released", label: '', icon: "st.secondary.off", action: "push"
			state "pushed", label: '', icon: "st.custom.buttons.easy", backgroundColor: "#b82121", action: "push"
		}
        valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat") {
			state "battery", label:'${currentValue}% battery', unit:""
		}


		main "button"
		details(["button", "battery"])
	}
}

def parse(String description) {
	def result = null
	if (description.startsWith("Err")) {
	    result = createEvent(descriptionText:description)
	} else if (description == "updated") {
		if (!state.MSR) {
			result = [
				response(zwave.wakeUpV1.wakeUpIntervalSet(seconds:4*3600, nodeid:zwaveHubNodeId)),
				response(zwave.manufacturerSpecificV2.manufacturerSpecificGet()),
			]
		}
	} else {
		def cmd = zwave.parse(description, [0x20: 1, 0x25: 1, 0x30: 1, 0x31: 5, 0x80: 1, 0x84: 1, 0x71: 3, 0x9C: 1])
		if (cmd) {
			result = zwaveEvent(cmd)
		}
	}
	return result
}

def sensorValueEvent(value) {
	if (value) {
		createEvent(name: "button", value: "released", descriptionText: "$device.displayName is released")
	} else {
		createEvent(name: "button", value: "pushed", descriptionText: "$device.displayName is pushed")
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd)
{
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
	sensorValueEvent(cmd.value)
}

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd)
{
	sensorValueEvent(cmd.sensorValue)
}

def zwaveEvent(physicalgraph.zwave.commands.sensoralarmv1.SensorAlarmReport cmd)
{
	sensorValueEvent(cmd.sensorState)
}

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd)
{
	def result = []
	if (cmd.notificationType == 0x06 && cmd.event == 0x16) {
		result << sensorValueEvent(1)
	} else if (cmd.notificationType == 0x06 && cmd.event == 0x17) {
		result << sensorValueEvent(0)
	} else if (cmd.notificationType == 0x07) {
		if (cmd.v1AlarmType == 0x07) {  // special case for nonstandard messages from Monoprice door/window sensors
			result << sensorValueEvent(cmd.v1AlarmLevel)
		} else if (cmd.event == 0x01 || cmd.event == 0x02) {
			result << sensorValueEvent(1)
		} else if (cmd.event == 0x03) {
			result << createEvent(descriptionText: "$device.displayName covering was removed", isStateChange: true)
			result << response(zwave.wakeUpV1.wakeUpIntervalSet(seconds:4*3600, nodeid:zwaveHubNodeId))
			if(!state.MSR) result << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet())
		} else if (cmd.event == 0x05 || cmd.event == 0x06) {
			result << createEvent(descriptionText: "$device.displayName detected glass breakage", isStateChange: true)
		} else if (cmd.event == 0x07) {
			if(!state.MSR) result << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet())
			result << createEvent(name: "motion", value: "active", descriptionText:"$device.displayName detected motion")
		}
	} else if (cmd.notificationType) {
		def text = "Notification $cmd.notificationType: event ${([cmd.event] + cmd.eventParameter).join(", ")}"
		result << createEvent(name: "notification$cmd.notificationType", value: "$cmd.event", descriptionText: text, displayed: false)
	} else {
		def value = cmd.v1AlarmLevel == 255 ? "active" : cmd.v1AlarmLevel ?: "inactive"
		result << createEvent(name: "alarm $cmd.v1AlarmType", value: value, displayed: false)
	}
	result
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd)
{
	def result = [createEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)]
	if (!state.lastbat || (new Date().time) - state.lastbat > 53*60*60*1000) {
		result << response(zwave.batteryV1.batteryGet())
		result << response("delay 1200")
	}
	if (!state.MSR) {
		result << response(zwave.wakeUpV1.wakeUpIntervalSet(seconds:4*3600, nodeid:zwaveHubNodeId))
		result << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet())
		result << response("delay 1200")
	}
	result << response(zwave.wakeUpV1.wakeUpNoMoreInformation())
	result
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
	def map = [ name: "battery", unit: "%" ]
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "${device.displayName} has a low battery"
		map.isStateChange = true
	} else {
		map.value = cmd.batteryLevel
	}
	state.lastbat = new Date().time
	[createEvent(map), response(zwave.wakeUpV1.wakeUpNoMoreInformation())]
}

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	def result = []

	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	log.debug "msr: $msr"
	updateDataValue("MSR", msr)

	retypeBasedOnMSR()

	result << createEvent(descriptionText: "$device.displayName MSR: $msr", isStateChange: false)
	result
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	createEvent(descriptionText: "$device.displayName: $cmd", displayed: false)
}

def retypeBasedOnMSR() {
	switch (state.MSR) {
		case "0086-0002-002D":
			log.debug("Changing device type to Z-Wave Water Sensor")
			setDeviceType("Z-Wave Water Sensor")
			break
		case "011F-0001-0001":  // Schlage motion
		case "014A-0001-0001":  // Ecolink motion
		case "0060-0001-0002":  // Everspring SP814
		case "0060-0001-0003":  // Everspring HSP02
			log.debug("Changing device type to Z-Wave Motion Sensor")
			setDeviceType("Z-Wave Motion Sensor")
			break
		
	}
}

def push() {
    log.debug "pressed"

    sendEvent(name: "button", value: "pushed", displayed: true, isStateChange: true)
    sendEvent(name: "button", value: "released", displayed: true, isStateChange: true)
}