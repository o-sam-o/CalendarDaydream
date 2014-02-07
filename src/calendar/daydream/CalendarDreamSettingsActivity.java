package calendar.daydream;

import java.util.ArrayList;

import calendar.daydream.util.CalendarDreamContants;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.CalendarContract.Calendars;
import android.util.Log;

public class CalendarDreamSettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
    
    public static class SettingsFragment extends PreferenceFragment {
    	
    	public SettingsFragment() {
    	}

		@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            
            ListPreference cals = (ListPreference) findPreference("calendars_preference");
            configureCalendarsList(cals);
        }
    	
    	private void configureCalendarsList(ListPreference calsList) {
    		ContentResolver cr = getActivity().getContentResolver();
    		Uri uri = Calendars.CONTENT_URI;   
    		Cursor cur = cr.query(uri, new String[] {Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME}, null, null, null);
    		
    		ArrayList<String> ids = new ArrayList<String>();
    		ArrayList<String> displayNames = new ArrayList<String>();
    		
    		ids.add("___calDayDream_all");
    		displayNames.add("All");
    		
    		while(cur.moveToNext()) {
    			String id = cur.getString(0);
    			ids.add(id);
    			String displayName = cur.getString(1);
    			displayNames.add(displayName);
    			Log.i(CalendarDreamContants.DEBUG_TAG, "Found cal: " + id + " - " + displayName);
    		}
    		cur.close();
    		
    		calsList.setEntries(displayNames.toArray(new String[displayNames.size()]));
    		calsList.setEntryValues(ids.toArray(new String[ids.size()]));
    	}
    }
	
}
