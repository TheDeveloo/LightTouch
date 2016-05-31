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

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

public class CameraManager {
	
	private static Camera cam = null;
	private static boolean isFlashEnabled;
	private static boolean isLightEnabled;
	private static Parameters param_on;
	private static Parameters param_off;
	private static Stromboscope strombo;
	private static int frequency = 1;
	
	public CameraManager(Camera camera) {
		connect(camera);
	    param_off = cam.getParameters();
	    param_on = cam.getParameters();
	    param_on.setFlashMode(Parameters.FLASH_MODE_TORCH);
		param_off.setFlashMode(Parameters.FLASH_MODE_OFF);
		strombo = new Stromboscope(this,frequency);
	}
	
	/**
	 * Release the manager's camera instance
	 */
	public void disconnect() {
		cam.release();
		cam = null;
	}
	
	/**
	 * Sets the manager's camera
	 * @param camera Camera instance
	 */
	public void connect(Camera camera) {
		cam = camera;
		isFlashEnabled = false;
	}
	
	/**
	 * Sets the light on (with or without stromboscope)
	 * @param stromboscope boolean true to enable stromboscopic light
	 */
	public void setLightOn(boolean stromboscope) {
		isLightEnabled = true;
		if(stromboscope)
			strombo.start();
		else {
			setFlash(true);
			strombo.stop();
		}
	}
	
	/**
	 * Shut down light
	 */
	public void setLightOff() {
		strombo.stop();
		isLightEnabled = false;
		setFlash(false);
	}
	
	/**
	 * Returns if the light is currently enabled or not
	 * @return boolean true if enabled else false
	 */
	public boolean getLight() {
		return isLightEnabled;
	}
    
	public void stopStromboscope() {
		strombo.stop();
	}
	/**
	 * Sets the flash on or off
	 * @param io boolean true for on, false for off
	 */
    public void setFlash(boolean io) {
    	isFlashEnabled = io;
    	if(io)
    	{
        	// Camera flash on
        	cam.setParameters(param_on);
			cam.startPreview();
		}else{
        	// Camera flash off
        	cam.setParameters(param_off);
			cam.stopPreview();
    	}
    }
    
    /**
     * Change flash from on to off or from off to on
     */
    public void flashSwitch() {
    	if(isFlashEnabled())
    		setFlash(false);
    	else
    		setFlash(true);
    }
    
    /**
     * Returns if the flash is enabled or not
     * @return boolean true if on else false
     */
    public boolean isFlashEnabled() {
    	return isFlashEnabled;
    }
    
    /**
     * Return the stromboscope's frenquency
     * @return int stromboscope frequency
     */
    public int getFrequency() {
    	return frequency;
    }
    
    /**
     * Change the stromboscope's frequency
     * @param freq int stromboscope frequency
     */
    public void setFrequency(int freq) {
    	frequency = freq;
    	strombo.changeFrequency(freq);
    }
}
