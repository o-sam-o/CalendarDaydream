package calendar.daydream.data;

import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract.Instances;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class CalendarCursorAdapter extends CursorAdapter {

	public CalendarCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int nameCol = cursor.getColumnIndex(Instances.TITLE);

		String name = cursor.getString(nameCol);

		TextView name_text = (TextView) view.findViewById(android.R.id.text1);
		if (name_text != null) {
			name_text.setText("Update: " + name);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(android.R.layout.simple_list_item_1, parent,
				false);

		int nameCol = cursor.getColumnIndex(Instances.TITLE);
		String name = cursor.getString(nameCol);


		TextView name_text = (TextView) v.findViewById(android.R.id.text1);
		if (name_text != null) {
			name_text.setText("New: " + name);
		}

		return v;
	}

}
