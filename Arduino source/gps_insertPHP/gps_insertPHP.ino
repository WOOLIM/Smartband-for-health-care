#include <Wire.h>
#include <UnoWiFiDevEd.h>
#include <SoftwareSerial.h>
#include <TinyGPS.h>

#define CONNECTOR     "rest" 
#define SERVER_ADDR   "192.168.0.7"

TinyGPS gps;
SoftwareSerial uart_gps(5,6);

void setup()
{    
  Serial.begin(9600);
  uart_gps.begin(9600);
  Serial.println(F(""));
  Serial.println(F("GPS Shield QuickStart Example Sketch v12"));
  Serial.println(F("       ...waiting for lock...           "));
  Serial.println(F(""));
  Ciao.begin(); 
  Wire.begin();
}

void loop()
{
  while(uart_gps.available())     // While there is data on the RX pin...
  {
      int c = uart_gps.read();    // load the data into a variable...
      if(gps.encode(c))      // if there is a new valid sentence...
      {
        getgps(gps);         // then grab the data.
      }   
  }  
}

void getgps(TinyGPS &gps)
{
  float latitude, longitude;
  char lat[10];
  char lon[10];
  gps.f_get_position(&latitude, &longitude);
  
  dtostrf(latitude,7,7,lat);
  dtostrf(longitude,7,5,lon);
  Serial.print(F("Lat/Long: ")); 
  Serial.print(lat); //소수점 5자리?
  Serial.print(F(", ")); 
  Serial.println(lon);
  delay(1000);
ㅇ
  String uri3 = "/gps_insert.php?";
    uri3 += "lat=";
    uri3 += lat;
    uri3 += "&lon=";
    uri3 += lon;
 
    CiaoData data1 = Ciao.write(CONNECTOR, SERVER_ADDR, uri3, "GET");
}

