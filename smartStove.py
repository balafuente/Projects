#Project for ECE 554 Embedded Systems
#by Blake LaFuente
#Sections of code sourced from:
#temperature sensor: http://www.circuitbasics.com/raspberry-pi-ds18b20-temperature-sensor-tutorial/
#Hologram Nova: https://hologram.io/docs/reference/cloud/python-sdk/

import os
import glob
import time
import RPi.GPIO as GPIO
from Hologram.HologramCloud import HologramCloud

#activate temperature sensor
os.system('modprobe w1-gpio')
os.system('modprobe w1-therm')

base_dir = '/sys/bus/w1/devices/'
device_folder = glob.glob(base_dir + '28*')[0]
device_file = device_folder + '/w1_slave'

#set-up input from gas sensor
GPIO.setmode(GPIO.BCM)
GPIO.setup(12,GPIO.IN)

#set up credentials for text messaging on hologram
credentials = {'devicekey': 'iAW><Z6b'}
cloud = HologramCloud(credentials, network='cellular',authentication_type = 'csrpsk')

#initializing global variables
print "Beginning Stove Sensor"
startingTime = time.time()
clockOn = False
highTempStart = 0


#task to start and check temperature clock when temperature is over 90 degrees F
def highTemp():
    global highTempStart
    global clockOn
    highTempTaskStart = time.time()
    if (clockOn is False):        
        highTempStart = time.time()      
        clockOn= True
        print("clock started")    
    print(clockOn)
    #change number below to change clock length before temperature warning is sent
    if((time.time()- highTempStart )> 60):
        sendWarning("temperature")
    print("High temperature clock task time took " + str((time.time() - highTempTaskStart)) + " seconds")

#send text message warning
# replace pound signs with phone number   
def sendWarning(warningType):
    warningStartTime = time.time()
    if (warningType == "gas"):
        cloud.sendSMS('+1##########','Gas is on!')  
        print"gas is on"
    if (warningType == "temperature"):
        cloud.sendSMS('+1##########','The stove has been on for over 1 minute')
        global clockOn
        clockOn = False
    print("Warning task took "+str((time.time()-warningStartTime))+ " seconds")


while True:
    backroundStartTime = time.time()
    interruption1Time = 0
    interruption2Time = 0
	f = open(device_file, 'r')
    lines = f.readlines()
    f.close()
    while lines[0].strip()[-3:] != 'YES':
        f = open(device_file, 'r')
        lines = f.readlines()
        f.close()
    equals_pos = lines[1].find('t=')   
    if equals_pos != -1:
        #convert raw readings into Celsius and Fahrenheit
        temp_string = lines[1][equals_pos+2:]
        temp_c = float(temp_string) / 1000.0
        temp_f = temp_c * 9.0 / 5.0 + 32.0
	gasState = GPIO.input(12)
    #gas sensor returns 0 if activated, else 1
	if(gasState == 0):
        interruption1StartTime = time.time()
        sendWarning("gas")
        interruption1Time = time.time() - interruption1StartTime
	elif(temp_f > 90):
        interruption2StartTime = time.time()
        highTemp()
        interruption2Time = time.time() - interruption2StartTime
    else:
        clockOn = False
	print("Temperature Fahrenheit: "+str(temp_f))
	backgroundTime = time.time() - backroundStartTime - interruption1Time - interruption2Time
	print("background task took " + str(backgroundTime) + " seconds")
	