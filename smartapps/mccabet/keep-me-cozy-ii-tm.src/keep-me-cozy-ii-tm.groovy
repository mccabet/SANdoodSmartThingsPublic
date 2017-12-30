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
 *  Keep Me Cozy II TM
 *
 *  Author: SmartThings
 */

definition(
    name: "Keep Me Cozy II_TM",
    namespace: "mccabet", 
    author: "McCabet", 
    description: "Works the same as Keep Me Cozy, but enables you to pick an alternative temperature sensor in a separate space from the thermostat. Focuses on making you comfortable where you are spending your time rather than where the thermostat is located.",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png"
)

preferences() {
	section("Choose thermostat... ") {
		input "thermostat", "capability.thermostat"
	}
	section("Heat setting..." ) {
		input "heatingSetpoint", "decimal", title: "Degrees"
	}
	section("Air conditioning setting...") {
		input "coolingSetpoint", "decimal", title: "Degrees"
	}
	section("Optionally choose temperature sensor to use instead of the thermostat's... ") {
		input "sensor", "capability.temperatureMeasurement", title: "Temp Sensors", required: false
	}
}

def installed()
{
	log.debug "enter installed, state: $state"
	subscribeToEvents()
}

def updated()
{
	log.debug "enter updated, state: $state"
	unsubscribe()
	subscribeToEvents()
}

def subscribeToEvents()
{
	subscribe(location, changedLocationMode)
	if (sensor) {
		subscribe(sensor, "temperature", temperatureHandler)
		subscribe(thermostat, "temperature", temperatureHandler)
		subscribe(thermostat, "thermostatMode", temperatureHandler)
	}
	evaluate()
}

def changedLocationMode(evt)
{
	log.debug "changedLocationMode mode: $evt.value, heat: $heat, cool: $cool"
	evaluate()
}

def temperatureHandler(evt)
{
	evaluate()
}

private evaluate()
{
	if (sensor) {
		def threshold = 1.0
		def tm = thermostat.currentThermostatMode			// Heating, Cooling 
		def ct = thermostat.currentTemperature				// Temperature read by Thermostat
		def currentTemp = sensor.currentTemperature			// Temperature read by alternate temp sensor
        def curHeatSetPoint = thermostat.heatingSetpoint	// Current Thermostat heating set point
        def curCoolSetPoint = thermostat.coolingSetpoint	// Current Thermostat cooling set point
        def sensorOffset = 0								// sensor temperature Offset
        def newHeatSetpoint = curHeatSetPoint				// 
        def newCoolSetpoint = curCoolSetPoint				// 
        
		log.trace("evaluate:, thermostat mode: $tm -- temp: $ct, heat: $thermostat.currentHeatingSetpoint, cool: $thermostat.currentCoolingSetpoint ")
		log.trace("evaluate:, $sensor.name  - temp: $currentTemp, heatSetPoint: $heatingSetpoint, coolSetPoint: $coolingSetpoint")
        
		if (tm in ["cool","auto"]) {
			// air conditioner
			if (currentTemp - coolingSetpoint >= threshold) {
				thermostat.setCoolingSetpoint(ct - 2)
				log.debug "thermostat.setCoolingSetpoint(${ct - 2}), ON"
			}
			else if (coolingSetpoint - currentTemp >= threshold && ct - thermostat.currentCoolingSetpoint >= threshold) {
				thermostat.setCoolingSetpoint(ct + 2)
				log.debug "thermostat.setCoolingSetpoint(${ct + 2}), OFF"
			}
		}
		if (tm in ["heat","emergency heat","auto"]) {
			// heater heatingsetpoint=72
			// TM - Calculate offset between thermostat and sensor
            //      Assume thermostat is the higher level.
            sensorOffset = ct - currentTemp
			newHeatSetpoint = heatingSetpoint + sensorOffset
				
            log.debug( "sensorOffset:  $sensorOffset, SensorTemp: $currentTemp, ThermostatTemp: $ct" )
            log.debug( "HeatingSetpoint should be: ${newHeatSetpoint} ")

			if ( curHeatSetPoint != newHeatSetpoint ) {
              	thermostat.setHeatingSetpoint( newHeatSetpoint )
               	log.debug( "HeatingSetpoint changed to: ${newHeatSetpoint} ")
            }
/*
			if (heatingSetpoint - currentTemp >= threshold) {
				thermostat.setHeatingSetpoint(ct + 2)
				log.debug "thermostat.setHeatingSetpoint(${ct + 2}), ON"
			}
			else if (currentTemp - heatingSetpoint >= threshold && thermostat.currentHeatingSetpoint - ct >= threshold) {
				thermostat.setHeatingSetpoint(ct - 2)
				log.debug "thermostat.setHeatingSetpoint(${ct - 2}), OFF"
			}
*/
		}
	}
	else {
		thermostat.setHeatingSetpoint(heatingSetpoint)
		thermostat.setCoolingSetpoint(coolingSetpoint)
		thermostat.poll()
	}
}

// for backward compatibility with existing subscriptions
def coolingSetpointHandler(evt) {
	log.debug "coolingSetpointHandler()"
}
def heatingSetpointHandler (evt) {
	log.debug "heatingSetpointHandler ()"
}