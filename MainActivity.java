package com.cs680.labyrinthcontroller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity implements SensorEventListener{
	protected PowerManager.WakeLock mWakeLock;

	private static final String TAG = "com.cs680.labyrinthcontroller";
	ServerSocket server = null;
	private Thread serverThread;

	private SensorManager mSensorManager;
	protected float[] orientation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		orientation = new float[3];
		
		// Get access to the accelerometer
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Start server thread
		serverThread = new Thread(initializeConnection);
		serverThread.start(); 
		
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// Button Handlers
		findViewById(R.id.quit).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				android.os.Process.killProcess(android.os.Process.myPid());  
			}
		});
		
		Button quit = (Button) findViewById(R.id.quit);
		quit.setVisibility(View.GONE);
	}
	
	// Server Thread
	private Runnable initializeConnection = new Thread() {
		public void run() {
			ByteBuffer buffer = ByteBuffer.wrap("GREETING".getBytes());
			ServerSocketChannel ssc = null;
			try {
				ssc = ServerSocketChannel.open();
				ssc.socket().bind(new InetSocketAddress(38301));
				ssc.configureBlocking(false);
				
				// Listen until thread interrupt is received
				while (true) {
					try{
						Log.e(TAG, "Waiting for connections");						
						
						SocketChannel sc = ssc.accept();
						
						if (sc == null) {
							Thread.sleep(2000);
						}
						else {
							Log.e(TAG, "Incoming connection from: " + sc.socket().getRemoteSocketAddress());
							while(sc.isOpen())
							{
								float[] values = new float[3];
								
								// Get synchronized access to the orientation data
								synchronized(this)
								{
									values = orientation;
									if(values.length > 0)
									{
										// Add orientation data to string
										String strBuffer = Float.toString(values[0]) + " " + Float.toString(values[1]) + " " + Float.toString(values[2]);

										// Convert string to bytebuffer
										ByteBuffer byteBuff = ByteBuffer.wrap(strBuffer.getBytes());
										
										// Send non-blocking tcp packet
										sc.write(byteBuff);																		
									}
								}
								
								// Sleep server thread for 50ms
								Thread.sleep(50);							
							}
						
							// Close connection
							sc.close();
						}
					}
					catch (IOException e) {
						Log.e(TAG, "IO ERROR");
					}

				}
			} catch (IOException e) {
				Log.e(TAG, "COULDNT CONNECT");
			}
			catch (InterruptedException e) {
				Log.e(TAG, "THREAD INTERRUPT/ KILLED");
			}
		}
	};


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {    
		synchronized(this)
		{
			orientation = event.values;
		}
	}
	

}
 