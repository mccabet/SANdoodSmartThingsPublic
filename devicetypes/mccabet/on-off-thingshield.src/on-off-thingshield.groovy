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
 *  Developed from smartthings On/Off Shield example Device Handler
 */
metadata {
	definition (name: "On/Off ThingShield", namespace: "mccabet", author: "mccabet") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"
	}

	// Simulator metadata
	simulator {
		status "on":  "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6E"
		status "off": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6666"

		// reply messages
		reply "raw 0x0 { 00 00 0a 0a 6f 6e }": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6E"
		reply "raw 0x0 { 00 00 0a 0a 6f 66 66 }": "catchall: 0104 0000 01 01 0040 00 0A21 00 00 0000 0A 00 0A6F6666"
	}

	// UI tile definitions
/*	tiles {
		standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "on", label: "Relay1", action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: "Relay1", action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}

		main "switch"
		details "switch"
	}
} */
	tiles(scale: 2) {
		// standard tile with actions
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: '${currentValue}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: '${currentValue}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}

		// standard flat tile with actions
		standardTile("relay1", "relay1.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state "off", label: "relay1", action: "switch.rly2_on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: "relay1", action: "switch.rly2_off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}

		// standard flat tile without actions
		standardTile("relay2", "relay2.switch", width: 2, height: 2, canChangeIcon: true, decoration: "flat") {
			state "off", label: "relay2", action: "switch.rly2_on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			state "on", label: "relay2", action: "switch.rly2_off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		}

		// standard with defaultState = true
		standardTile("flatDefaultState", "null", width: 2, height: 2, decoration: "flat") {
			state "off", label: 'Fail!', icon: "st.switches.switch.off"
			state "on", label: 'Pass!', icon: "st.switches.switch.on", defaultState: true
		}

		// utility tiles to fill the spaces
		standardTile("empty2x2", "null", width: 2, height: 2, decoration: "flat") {
			state "emptySmall", label:'', defaultState: true
		}
		standardTile("empty4x2", "null", width: 4, height: 2, decoration: "flat") {
			state "emptyBigger", label:'', defaultState: true
		}

		// multi-line text (explicit newlines)
		standardTile("multiLine", "device.multiLine", width: 2, height: 2) {
			state "multiLine", label: '${currentValue}', defaultState: true
		}

		standardTile("multiLineWithIcon", "device.multiLine", width: 2, height: 2) {
			state "multiLineIcon", label: '${currentValue}', icon: "st.switches.switch.off", defaultState: true
		}

		main("switch")
		details([
			"switch", "relay1", "relay2",

			"multiLine", "multiLineWithIcon"
		])
	}
}


def installed() {
    sendEvent(name: "integer", value: 47)
}

// Parse incoming device messages to generate events
def parse(String description) {
	def value = zigbee.parse(description)?.text
	def name = value in ["on","off"] ? "switch" : null
	def result = createEvent(name: name, value: value)
	log.debug "Parse returned ${result?.descriptionText}"
	return result
}

// Commands sent to the device
def on() {
	zigbee.smartShield(text: "on").format()
}

def off() {
	zigbee.smartShield(text: "off").format()
}

def rly1_on() {
	zigbee.smartShield(text: "rly1_on").format()
}

def rly1_off() {
	zigbee.smartShield(text: "rly1_off").format()
}

def rly2_on() {
	zigbee.smartShield(text: "rly2_on").format()
}

def rly2_off() {
	zigbee.smartShield(text: "rly2_off").format()
}