#include <Wire.h>
#include <math.h>
#include <UnoWiFiDevEd.h>

#define CONNECTOR     "rest" 
#define SERVER_ADDR   "192.168.0.7"

#define MPU6050_ACCEL_XOUT_H       0x3B   // R 
#define MPU6050_PWR_MGMT_1         0x6B   // R/W
#define MPU6050_PWR_MGMT_2         0x6C   // R/W
#define MPU6050_WHO_AM_I           0x75   // R
#define MPU6050_I2C_ADDRESS 0x68

//bpm
unsigned char bpm = 0;

typedef union accel_t_gyro_union
{
  struct
  {
    uint8_t x_accel_h;
    uint8_t x_accel_l;
    uint8_t y_accel_h;
    uint8_t y_accel_l;
    uint8_t z_accel_h;
    uint8_t z_accel_l;
    uint8_t t_h;
    uint8_t t_l;
    uint8_t x_gyro_h;
    uint8_t x_gyro_l;
    uint8_t y_gyro_h;
    uint8_t y_gyro_l;
    uint8_t z_gyro_h;
    uint8_t z_gyro_l;
  } reg;
  struct
  {
    int16_t x_accel;
    int16_t y_accel;
    int16_t z_accel;
    int16_t temperature;
    int16_t x_gyro;
    int16_t y_gyro;
    int16_t z_gyro;
  } value;
};

unsigned long last_read_time;
float         last_x_angle;  // These are the filtered angles
float         last_y_angle;
float         last_z_angle; 
float         last_gyro_x_angle;  // Store the gyro angles to compare drift
float         last_gyro_y_angle;
float         last_gyro_z_angle;

void set_last_read_angle_data(unsigned long time, float x, float y, float z, float x_gyro, float y_gyro, float z_gyro) {
  last_read_time = time;
  last_x_angle = x;
  last_y_angle = y;
  last_z_angle = z;
  last_gyro_x_angle = x_gyro;
  last_gyro_y_angle = y_gyro;
  last_gyro_z_angle = z_gyro;
}

inline unsigned long get_last_time() {return last_read_time;}
inline float get_last_x_angle() {return last_x_angle;}
inline float get_last_y_angle() {return last_y_angle;}
inline float get_last_z_angle() {return last_z_angle;}
inline float get_last_gyro_x_angle() {return last_gyro_x_angle;}
inline float get_last_gyro_y_angle() {return last_gyro_y_angle;}
inline float get_last_gyro_z_angle() {return last_gyro_z_angle;}

//  Use the following global variables and access functions
//  to calibrate the acceleration sensor
float    base_x_gyro;
float    base_y_gyro;
float    base_z_gyro;

int read_gyro_accel_vals(uint8_t* accel_t_gyro_ptr) {
  accel_t_gyro_union* accel_t_gyro = (accel_t_gyro_union *) accel_t_gyro_ptr;
  int error = MPU6050_read (MPU6050_ACCEL_XOUT_H, (uint8_t *) accel_t_gyro, sizeof(*accel_t_gyro));
  uint8_t swap;
  #define SWAP(x,y) swap = x; x = y; y = swap

  SWAP ((*accel_t_gyro).reg.x_accel_h, (*accel_t_gyro).reg.x_accel_l);
  SWAP ((*accel_t_gyro).reg.y_accel_h, (*accel_t_gyro).reg.y_accel_l);
  SWAP ((*accel_t_gyro).reg.z_accel_h, (*accel_t_gyro).reg.z_accel_l);
  SWAP ((*accel_t_gyro).reg.t_h, (*accel_t_gyro).reg.t_l);
  SWAP ((*accel_t_gyro).reg.x_gyro_h, (*accel_t_gyro).reg.x_gyro_l);
  SWAP ((*accel_t_gyro).reg.y_gyro_h, (*accel_t_gyro).reg.y_gyro_l);
  SWAP ((*accel_t_gyro).reg.z_gyro_h, (*accel_t_gyro).reg.z_gyro_l);
  return error;
}

void calibrate_sensors() {
  int                   num_readings = 10;
  float                 x_accel = 0;
  float                 y_accel = 0;
  float                 z_accel = 0;
  float                 x_gyro = 0;
  float                 y_gyro = 0;
  float                 z_gyro = 0;
  accel_t_gyro_union    accel_t_gyro;

  read_gyro_accel_vals((uint8_t *) &accel_t_gyro);
 
  for (int i = 0; i < num_readings; i++) {
    read_gyro_accel_vals((uint8_t *) &accel_t_gyro);
    x_accel += accel_t_gyro.value.x_accel;
    y_accel += accel_t_gyro.value.y_accel;
    z_accel += accel_t_gyro.value.z_accel;
    x_gyro += accel_t_gyro.value.x_gyro;
    y_gyro += accel_t_gyro.value.y_gyro;
    z_gyro += accel_t_gyro.value.z_gyro;
    delay(100);
  }
  x_accel /= num_readings;
  y_accel /= num_readings;
  z_accel /= num_readings;
  x_gyro /= num_readings;
  y_gyro /= num_readings;
  z_gyro /= num_readings;
 
  // Store the raw calibration values globally
  base_x_gyro = x_gyro;
  base_y_gyro = y_gyro;
  base_z_gyro = z_gyro;
}

void setup()
{     
  int error;
  uint8_t c;

  Serial.begin(9600);
  Wire.begin();
  Ciao.begin();

  error = MPU6050_read (MPU6050_WHO_AM_I, &c, 1);
  error = MPU6050_read (MPU6050_PWR_MGMT_2, &c, 1);
  
  // Clear the 'sleep' bit to start the sensor.
  MPU6050_write_reg (MPU6050_PWR_MGMT_1, 0);
 
  //Initialize the angles
  calibrate_sensors(); 
  set_last_read_angle_data(millis(), 0, 0, 0, 0, 0, 0);
}

float x,y,z;
int count=0,prev=0,state=0;
int threshold=13;
unsigned long t_now, timer1, timer2;
float addtime=0;

void loop()
{
  int error;
  double dT;
  accel_t_gyro_union accel_t_gyro;

  error = read_gyro_accel_vals((uint8_t*) &accel_t_gyro);
  // Get the time of reading for rotation computations
  t_now = millis();
    
  float FS_SEL = 131;
 
  float gyro_x = (accel_t_gyro.value.x_gyro - base_x_gyro)/FS_SEL;
  float gyro_y = (accel_t_gyro.value.y_gyro - base_y_gyro)/FS_SEL;
  float gyro_z = (accel_t_gyro.value.z_gyro - base_z_gyro)/FS_SEL;
 
  // Get raw acceleration values
  //float G_CONVERT = 16384;
  float accel_x = accel_t_gyro.value.x_accel;
  float accel_y = accel_t_gyro.value.y_accel;
  float accel_z = accel_t_gyro.value.z_accel;
 
  // Get angle values from accelerometer
  float RADIANS_TO_DEGREES = 180/3.14159;
  float accel_vector_length = sqrt(pow(accel_x,2) + pow(accel_y,2) + pow(accel_z,2));
  float accel_angle_y = atan(-1*accel_x/sqrt(pow(accel_y,2) + pow(accel_z,2)))*RADIANS_TO_DEGREES;
  float accel_angle_x = atan(accel_y/sqrt(pow(accel_x,2) + pow(accel_z,2)))*RADIANS_TO_DEGREES;
 
  float accel_angle_z = atan(sqrt(pow(accel_x,2) + pow(accel_y,2))/accel_z)*RADIANS_TO_DEGREES;;
 
  // Compute the (filtered) gyro angles
  float dt =(t_now - get_last_time())/1000.0;
  addtime += dt;
  if (addtime > 1){
    Wire.requestFrom(0xA0 >> 1, 1);    // request 1 bytes from slave device 
    if (Wire.available()) {          // slave may send less than requested
      bpm = Wire.read();   // receive heart rate value (a byte)
      Serial.println(bpm, DEC);         // print heart rate value
    }
    addtime = 0;
    insert_step(count, bpm, state);
  }
  //Serial.println(dt);
  float gyro_angle_x = gyro_x*dt + get_last_x_angle();
  float gyro_angle_y = gyro_y*dt + get_last_y_angle();
  float gyro_angle_z = gyro_z*dt + get_last_z_angle();
 
  // Compute the drifting gyro angles
  float unfiltered_gyro_angle_x = gyro_x*dt + get_last_gyro_x_angle();
  float unfiltered_gyro_angle_y = gyro_y*dt + get_last_gyro_y_angle();
  float unfiltered_gyro_angle_z = gyro_z*dt + get_last_gyro_z_angle();
 
  // Apply the complementary filter to figure out the change in angle - choice of alpha is
  // estimated now.  Alpha depends on the sampling rate...
  float alpha = 0.96;
  float angle_x = alpha*gyro_angle_x + (1.0 - alpha)*accel_angle_x;
  float angle_y = alpha*gyro_angle_y + (1.0 - alpha)*accel_angle_y;
  float angle_z = gyro_angle_z;  //Accelerometer doesn't give z-angle
 
  // Update the saved data with the latest values
  set_last_read_angle_data(t_now, angle_x, angle_y, angle_z, unfiltered_gyro_angle_x, unfiltered_gyro_angle_y, unfiltered_gyro_angle_z);

 //Finding the magnitude of acceleration from the combined data from gyroscope and accelerometer.
  int mag=sqrt(pow(x-angle_x,2)+pow(y-angle_y,2)+pow(z-angle_z,2));
  
 //If the magnitude is greater than threshold and the previous magnitude is lesser than threshold value increase count.
  if(mag>=threshold && prev<threshold)
  {
    count+=1;
  }
  
  if(state==0){
    if(accel_vector_length>30000 || accel_vector_length<6000){
      state = 1;
      Serial.println("STATE1!!!!");
      timer1 = t_now;
    } else{
      state = 0;
    }
  }else if(state == 1) {
    if((t_now - timer1) / 1000 == 3){
      if(angle_y <40 && angle_y >-20){
        state = 2;
        Serial.println("STATE2222!!!!");
        timer2 = t_now;
      }else{
        state=0;
      }
    }
  }else if(state == 2) {
    if((t_now - timer2) / 1000 == 8){
      if(angle_y <40 && angle_y >-20){
        state = 3;
        Serial.println("STATE333!!!!");
      }else{
        state=0;
      }
    }
  }else if(state == 3) {
    if(angle_y >60){
        state = 0;
      }
  }
  
  prev = mag;
  x=angle_x;
  y=angle_y;
  z=angle_z;
  // Delay so we don't swamp the serial  port
  //Serial.write(10);
}

void insert_step(int step, unsigned char bpm, int state) {
  
  String uri1 = "/step_insert.php?";
    uri1 += "step=";
    uri1 += step;
    uri1 += "&bpm=";
    uri1 += String(bpm);
    uri1 += "&state=";
    uri1 += state;
      
    CiaoData data1 = Ciao.write(CONNECTOR, SERVER_ADDR, uri1, "GET");
}

int MPU6050_read(int start, uint8_t *buffer, int size)
{
  int i, n, error;

  Wire.beginTransmission(MPU6050_I2C_ADDRESS);
  n = Wire.write(start);
  if (n != 1)
    return (-10);

  n = Wire.endTransmission(false);    // hold the I2C-bus
  if (n != 0)
    return (n);

  // Third parameter is true: relase I2C-bus after data is read.
  Wire.requestFrom(MPU6050_I2C_ADDRESS, size, true);
  i = 0;
  while(Wire.available() && i<size)
  {
    buffer[i++]=Wire.read();
  }
  if ( i != size)
    return (-11);

  return (0);  // return : no error
}


int MPU6050_write(int start, const uint8_t *pData, int size)
{
  int n, error;

  Wire.beginTransmission(MPU6050_I2C_ADDRESS);
  n = Wire.write(start);        // write the start address
  if (n != 1)
    return (-20);

  n = Wire.write(pData, size);  // write data bytes
  if (n != size)
    return (-21);

  error = Wire.endTransmission(true); // release the I2C-bus
  if (error != 0)
    return (error);

  return (0);         // return : no error
}

int MPU6050_write_reg(int reg, uint8_t data)
{
  int error;

  error = MPU6050_write(reg, &data, 1);

  return (error);
}
