/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Freezer Temperature Sensor", namespace: "mccabet", author: "Thomas McCabe") {
		capability "Temperature Measurement"
        capability "Battery"
		capability "Switch"

		attribute "Current1", "number"
		attribute "Current2", "number"
		attribute "Current3", "number"
        
		attribute "Low1", "number"
		attribute "Low2", "number"
		attribute "Low3", "number"

		attribute "High1", "number"
		attribute "High2", "number"
		attribute "High3", "number"

		attribute "Battery1", "number"
		attribute "Battery2", "number"
		attribute "Battery3", "number"

//		fingerprint profileId: "0104", deviceId: "0302", inClusters: "0000,0001,0003,0009,0402,0405"
		fingerprint profileId: "0104", deviceId: "0138", inClusters: "0000"
	}
	// Simulator metadata
	simulator {
		// status messages
		status "ping": "catchall: 0104 0000 01 01 0040 00 6A67 00 00 0000 0A 00 0A70696E67"
		status "hello": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A48656c6c6f20576f726c6421"
        status "Freezer1": "catchall: 0104 0000 01 01 '{\"Channel\": 1, \"Current\": -3.60, \"Low\": -5.90, \"High\": 15.30, \"Battery\": 100 }'"

		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "Current: $i F"
		}

		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "Low: $i F"
		}

		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "High: $i F"
		}

		for (int i = 0; i <= 100; i += 10) {
			status "${i}F": "Battery: $i F"
		}

//		for (int i = 0; i <= 100; i += 10) {
//			status "${i}%": "humidity: ${i}%"
//		}
	}

	// UI tile definitions
	tiles {
			valueTile("Freezer1", "Freezer1.name", width: 1, height: 1) {
			state "Freezer1", label:'Freezer1', backgroundColor:"#44b621"
		}
			valueTile("Freezer2", "Freezer2.name", width: 1, height: 1) {
			state "Freezer2", label:'Freezer2', backgroundColor:"#44b621"
		}
			valueTile("Freezer3", "Freezer3.name", width: 1, height: 1) {
			state "Freezer3", label:'Freezer3', backgroundColor:"#44b621"
		}
			valueTile("Current1", "Freezer1.Current", width: 1, height: 1) {
			state "Current", label:'${currentValue}%', backgroundColor:"#153591"
		}
			valueTile("Freezer2", "Freezer2.Current", width: 1, height: 1) {
			state "Current", label:'${currentValue}%', backgroundColor:"#153591"
		}
			valueTile("Freezer3", "Freezer3.Current", width: 1, height: 1) {
			state "Current", label:'${currentValue}%', backgroundColor:"#153591"
		}
			valueTile("Freezer1", "Freezer1.Low", width: 1, height: 1) {
			state "Low", label:'${currentValue}%', backgroundColor:"#f1d801"
		}
			valueTile("Freezer2", "Freezer2.Low", width: 1, height: 1) {
			state "Low", label:'${currentValue}%', backgroundColor:"#f1d801"
		}
			valueTile("Freezer3", "Freezer3.Low", width: 1, height: 1) {
			state "Low", label:'${currentValue}%', backgroundColor:"#f1d801"
		}
			valueTile("Freezer1", "Freezer1.High", width: 1, height: 1) {
			state "High", label:'${currentValue}%', backgroundColor:"#bc2323"
		}
			valueTile("Freezer2", "Freezer2.High", width: 1, height: 1) {
			state "High", label:'${currentValue}%', backgroundColor:"#bc2323"
		}
			valueTile("Freezer3", "Freezer3.High", width: 1, height: 1) {
			state "High", label:'${currentValue}%', backgroundColor:"#bc2323"
		}
			valueTile("Freezer1", "Freezer1.Battery", width: 1, height: 1) {
			state "Battery", label:'${currentValue}%',defaultState: true, backgroundColors: [
            		[value: 0, color: "#bc2323"],
            		[value: 100, color: "#44b621"]
        	]	
		}
			valueTile("Freezer2", "Freezer2.Battery", width: 1, height: 1) {
			state "Battery", label:'${currentValue}%',defaultState: true, backgroundColors: [
            		[value: 0, color: "#bc2323"],
            		[value: 100, color: "#44b621"]
        	]	
		}
			valueTile("Freezer3", "Freezer3.Battery", width: 1, height: 1) {
			state "Battery", label:'${currentValue}%',defaultState: true, backgroundColors: [
            		[value: 0, color: "#bc2323"],
            		[value: 100, color: "#44b621"]
        	]	
		}
		standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}

/*		valueTile("Temperature", "device.temperature") {
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],	// Dark Blue
					[value: 44, color: "#1e9cbb"],	// Medium Blue
					[value: 59, color: "#90d2a7"],	// Pale Green
					[value: 74, color: "#44b621"],	// Green
					[value: 84, color: "#f1d801"],	// Yellow
					[value: 95, color: "#d04e00"],	// Orange
					[value: 96, color: "#bc2323"]	// Red
				]
			)
		}
			valueTile("Channel", "device.Channel") {
			state "Channel", label:'Yellow', backgroundColor:"#f1d801"
		}
		valueTile("Type", "device.sensorType") {
			state "type", label:'Orange', backgroundColor:"#d04e00"
		}
        valueTile("humidity", "device.humidity") {
			state "humidity", label:'${currentValue}%', unit:""
		}
*/
		main(["Freezer1", "Freezer2", "Freezer3", "Current1", "Current2", "Current3", "switch"])
		details(["Freezer1", "Freezer2", "Freezer3", "Current1", "Current2", "Current3", "switch"])
	}
}

// Parse incoming device messages to generate events
/* def parse(String description) {
	def value = zigbee.parse(description)?.text
	def name = value in ["on","off"] ? "switch" : null
	def result = createEvent(name: name, value: value)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
} */

/* Format of data in description.text
'{	"Channel": 1,
    "Current": -3.60,
    "Low": -3.60,
    "High": -3.60,
    "Battery": 100
}'
*/
def parse(String description) {	
	def jsonString = zigbee.parse(description)?.text
	def jsonDevice = jsonSlurper.parseText(jsonString)
	log.trace "Parse returned ${jsonString}"
	if( jsonDevice.Channel == 1) {
    	Freezer.Current1 = jsonDevice.Current
        Freezer.Low1 = jsonDevice.Low
        Freezer.High1 = jsonDevice.High
        Freezer.Battery1 = jsonDevice.Battery
    }
	if( jsonDevice.Channel == 2) {
    	Freezer2.Current = jsonDevice.Current
        Freezer2.Low = jsonDevice.Low
        Freezer2.High = jsonDevice.High
        Freezer2.Battery = jsonDevice.Battery
    }
	if( jsonDevice.Channel == 3) {
    	Freezer3.Current = jsonDevice.Current
        Freezer3.Low = jsonDevice.Low
        Freezer3.High = jsonDevice.High
        Freezer3.Battery = jsonDevice.Battery
    }
    // handle device messages, determine what value of the event is
//    return createEvent(name: "", value: someValue)
}

def installed() {
    sendEvent(name: "Current", value: 47)
    sendEvent(name: "Low", value: -7.0)
    sendEvent(name: "High", value: "32.0F")
    sendEvent(name: "Battery", value: "100")
}

/*
def parse(String description) {
	def name = parseName(description)
	def value = parseValue(description)
	def unit = name == "temperature" ? getTemperatureScale() : null
	def result = createEvent(name: name, value: value, unit: unit)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}
*/
private String parseName(String description) {
    if (description?.startsWith("Freezer: ")) {
		return "Freezer"
    } else if (description?.startsWith("Current: ")) {
		return "temperature"
	} else if (description?.startsWith("High: ")) {
		return "Channel"
	} else if (description?.startsWith("Low: ")) {
		return "Type"
	} else if (description?.startsWith("Battery: ")) {
		return "humidity"
	}
	null
}
// Commands to device
// Commands sent to the device
def on() {
	zigbee.smartShield(text: "on on on ").format()
}

def off() {
	zigbee.smartShield(text: "off off off").format()
}
