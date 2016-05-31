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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.widget.TextView;

public class MyBatteryManager extends BroadcastReceiver {
	
	private static TextView text;
	private Context context;
	
	public MyBatteryManager(Context context, TextView field) {
		super();
		text = field;
		this.context = context;
		updateLevel();
	}
	
    @Override
    public void onReceive(Context context, Intent batteryStatus) { 
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
        updateText(batteryPct);
    }
    
    public void updateLevel() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null, ifilter);
		onReceive(context, batteryStatus);
    	
    }
    
    private void updateText(float level) {
    	int percent = (int)(level * 100);
    	text.setText(percent+"%");
    }
}
