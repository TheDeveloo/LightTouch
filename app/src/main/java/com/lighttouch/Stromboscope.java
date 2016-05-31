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

import java.util.Timer;
import java.util.TimerTask;

public class Stromboscope {
	
	private CameraManager camManager;
	private Timer timer = null;
	private int delay;
	// Used to return to previous flash state
	private static boolean loop = false;
	
	public Stromboscope(CameraManager cam, int frequency) {
		changeFrequency(frequency);
		camManager = cam;
	}
	
	public void start() {
		if(timer != null)
			stop();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
	    			camManager.flashSwitch();
	    			Stromboscope.loop = !loop;
			}
		},0,delay);
	}
	
	public void stop() {
		if(loop)
		{
			camManager.flashSwitch();
			loop = false;
		}
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}
	
	public void restart() {
		stop();
		start();
	}
	
	public void changeFrequency(int frequency) {
		if(frequency == 0)
			frequency++;
		delay = 500/frequency;
		if(timer != null)
			restart();

	}
}
