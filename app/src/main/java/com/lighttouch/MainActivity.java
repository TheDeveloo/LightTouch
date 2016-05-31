/** 
 * This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.lighttouch;

import com.lighttouch.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {

	private boolean isSwitchOn = false;
	private boolean isFingerOn = false;
	private CameraManager camManager;
	private MyBatteryManager batteryManager;
	private Camera camera;
	private Intent batteryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Updating battery
        batteryManager = new MyBatteryManager(getApplicationContext(),(TextView) findViewById(R.id.batteryValue));
        batteryIntent = new Intent(this, MyBatteryManager.class);

        // Hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Hide title bar (notifications bar)
        ActionBar actionBar = getActionBar();
		if(actionBar != null) {
			actionBar.hide();
		}
        
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Frequency settings
        ((SeekBar)findViewById(R.id.frequency)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				camManager.setFrequency(seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(progress == 0)
					progress++;
				((TextView)findViewById(R.id.valueFrequency)).setText(String.format("%d Hz", progress));
			}
		});

        ((Switch)findViewById(R.id.switchInvert)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	isSwitchOn = !isSwitchOn;
            	changeLight();
            }
        });
        
        ((Switch)findViewById(R.id.switchLock)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!getLightLock())
                {
                	isSwitchOn = isFingerOn;
                	if(getLightInvertion())
                		isSwitchOn = !isSwitchOn;
                	changeLight();
                }
            }
        });

        ((Switch)findViewById(R.id.switchStrombo)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            	if(!getStromboscope())
            		camManager.stopStromboscope();
            	else
            		changeLight();
            }
        });
        
        // Existance test of flashlight
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
        {

            // Camera access 
            try {
            	camera = Camera.open();
                camManager = new CameraManager(camera);
            } catch (Exception e) {
            	
            }
            
        	// Add listener on light's button
        	findViewById(R.id.button1).setOnTouchListener(new OnTouchListener() {
	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                if(event.getAction() == MotionEvent.ACTION_DOWN) {
	                	isFingerOn = true;
	            		isSwitchOn = !isSwitchOn;
		                changeLight();
	                }else if(event.getAction() == MotionEvent.ACTION_UP) {
	                	isFingerOn = false;
	            		if(!getLightLock())
	                		isSwitchOn = !isSwitchOn;
		                changeLight();
	                }
	                return false;
	            }
	        });
        }else{
        	((TextView) findViewById(R.id.error)).setText(getResources().getString(R.string.missing_flashlight));
        	// Hiding light button
        	findViewById(R.id.button1).setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	try {
    		camManager.disconnect();
    	} catch (Exception e) {
    		
    	}
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// Camera access 
    	if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))
        try {
        	camera = Camera.open();
        	camManager.connect(camera);
        } catch (Exception e) {
        	
        }
    	changeLight();
    	batteryManager.updateLevel();
    }
    
    /*public void lightButtonTrigger() {

    	// Show parameters text
		findViewById(R.id.textStrombo).setVisibility(0);
		findViewById(R.id.textFrequency).setVisibility(0);
		
    	// Hide parameters text
		findViewById(R.id.textStrombo).setVisibility(4);
		findViewById(R.id.textFrequency).setVisibility(4);
    }*/
    
    public boolean getStromboscope() {
    	return ((Switch)findViewById(R.id.switchStrombo)).isChecked();
    }
    
    public boolean getLightLock() {
    	return ((Switch)findViewById(R.id.switchLock)).isChecked();
    }
    
    public boolean getLightInvertion() {
    	return ((Switch)findViewById(R.id.switchInvert)).isChecked();
    }
    
    private void changeLight() {
		if(isSwitchOn)
			camManager.setLightOn(getStromboscope());
		else
			camManager.setLightOff();
    }
}
