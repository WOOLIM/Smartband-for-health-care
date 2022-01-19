#include <math.h> // (no semicolon)
#include <Wire.h>
#include <UnoWiFiDevEd.h>

#define CONNECTOR     "rest" 
#define SERVER_ADDR   "192.168.0.7"

const int MPU=0x68;
int AcX,AcY,AcZ,GyX,GyY,GyZ; 
float AM, GM;

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
  get_gyro();
  delay(500);
}

void get_gyro() {
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

  delay(333);
  AM = pow(pow(AcX, 2) + pow(AcY, 2) + pow(AcZ, 2), 0.5); //calculating Amplitute vector for 3 axis
  GM = pow(pow(GyX, 2) + pow(GyY, 2) + pow(GyZ, 2), 0.5); //never used, just for verification
  
  Serial.print(" AM = "); Serial.print(AM);
  Serial.print(" | GM = ");Serial.print(GM);
  Serial.println("");
  delay(333);
  insert_gyro(AM,GM);
    
}

void insert_gyro(float am, float gm){  
  
  String uri2 = "/gyro_insert.php?";
    uri2 += "AM=";
    uri2 += am;
    uri2 += "&GM=";
    uri2 += gm;

         
    CiaoData data1 = Ciao.write(CONNECTOR, SERVER_ADDR, uri2, "GET");
 
    if (!data1.isEmpty()){
      Ciao.println(F("Inserted Data2"));
    }
    else{ 
      Ciao.println(F("Write Error2"));
    }   
}

