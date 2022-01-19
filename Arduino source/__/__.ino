#include <math.h> 
#include <Wire.h>
#include <UnoWiFiDevEd.h>

#define CONNECTOR     "rest" 
#define SERVER_ADDR   "192.168.0.7"

const int MPU=0x68;
int AcX,AcY,AcZ,GyX,GyY,GyZ; 

//bpm
unsigned char bpm = 0;

void setup()
{  
  int error;
  uint8_t c;
  
  Serial.begin(9600);
  Ciao.begin(); 
  Wire.begin();
  
  Wire.beginTransmission(MPU); 
  Wire.write(0x6B); // PWR_MGMT_1 register 
  Wire.write(0); // set to zero (wakes up the MPU-6050) 
  Wire.endTransmission(true); 
}

void loop()
{
  Wire.requestFrom(0xA0 >> 1, 1);    // request 1 bytes from slave device 
    while (Wire.available()) {          // slave may send less than requested
      bpm = Wire.read();   // receive heart rate value (a byte)
      Serial.println(bpm, DEC);         // print heart rate value
    }
  insert_bpm(bpm);
  
  Wire.beginTransmission(MPU); 
  Wire.write(0x3B); // starting with register 0x3B (ACCEL_XOUT_H) 
  Wire.endTransmission(false); 
  Wire.requestFrom(MPU,14,true); // request a total of 14 registers 
  
  AcX=Wire.read()<<8|Wire.read(); // 0x3B (ACCEL_XOUT_H) & 0x3C (ACCEL_XOUT_L) 
  AcY=Wire.read()<<8|Wire.read(); // 0x3D (ACCEL_YOUT_H) & 0x3E (ACCEL_YOUT_L) 
  AcZ=Wire.read()<<8|Wire.read(); // 0x3F (ACCEL_ZOUT_H) & 0x40 (ACCEL_ZOUT_L) 
  GyX=Wire.read()<<8|Wire.read(); // 0x43 (GYRO_XOUT_H) & 0x44 (GYRO_XOUT_L) 
  GyY=Wire.read()<<8|Wire.read(); // 0x45 (GYRO_YOUT_H) & 0x46 (GYRO_YOUT_L) 
  GyZ=Wire.read()<<8|Wire.read(); // 0x47 (GYRO_ZOUT_H) & 0x48 (GYRO_ZOUT_L)
  
  insert_gyro(GyX,GyY,GyZ,AcX,AcY,AcZ);
}

void insert_bpm(unsigned char bpm) {
  
  String uri1 = "/heartbeat_insert.php?";
    uri1 += "bpm=";
    uri1 += String(bpm);
      
    CiaoData data1 = Ciao.write(CONNECTOR, SERVER_ADDR, uri1, "GET");
 
    if (!data1.isEmpty()){
      Ciao.println(F("Inserted Data1"));
    }
    else{ 
      Ciao.println(F("Write Error1"));
    } 
}

void insert_gyro(int gx, int gy, int gz, int ax, int ay, int az){  
  
  String uri2 = "/gyro_insert.php?";
    uri2 += "gyx=";
    uri2 += String(gx);
    uri2 += "&gyy=";
    uri2 += String(gy);
    uri2 += "&gyz=";
    uri2 += String(gz);
    uri2 += "&acx=";
    uri2 += String(ax);
    uri2 += "&acy=";
    uri2 += String(ay);
    uri2 += "&acz=";
    uri2 += String(az);
         
    CiaoData data1 = Ciao.write(CONNECTOR, SERVER_ADDR, uri2, "GET");
 
    if (!data1.isEmpty()){
      Ciao.println(F("Inserted Data2"));
    }
    else{ 
      Ciao.println(F("Write Error2"));
    }   
}

