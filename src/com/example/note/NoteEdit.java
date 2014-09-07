package com.example.note;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class NoteEdit extends Activity{
	
	public static int numTitle = 1;	
	public static String curDate = "";
	public static String curText = "";	
    private EditText mTitleText;
    private EditText mBodyText;
    private TextView mDateText;
    private Long mRowId;

    private Cursor note;

    private NotesDbAdapter mDbHelper;
      
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();        
        
        setContentView(R.layout.note_edit);
        setTitle(R.string.app_name);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mDateText = (TextView) findViewById(R.id.notelist_date);

        long msTime = System.currentTimeMillis();  
        Date curDateTime = new Date(msTime);
 	
        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y");  
        curDate = formatter.format(curDateTime);        
        
        mDateText.setText(""+curDate);
        

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                                    : null;
        }

        populateFields();
    
    }
	
	  public static class LineEditText extends EditText{
			// we need this constructor for LayoutInflater
			public LineEditText(Context context, AttributeSet attrs) {
				super(context, attrs);
					mRect = new Rect();
			        mPaint = new Paint();
			        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
			        mPaint.setColor(Color.BLUE);
			}

			private Rect mRect;
		    private Paint mPaint;	    
		    
		    @Override
		    protected void onDraw(Canvas canvas) {
		  
		        int height = getHeight();
		        int line_height = getLineHeight();

		        int count = height / line_height;

		        if (getLineCount() > count)
		            count = getLineCount();

		        Rect r = mRect;
		        Paint paint = mPaint;
		        int baseline = getLineBounds(0, r);

		        for (int i = 0; i < count; i++) {

		            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
		            baseline += getLineHeight();

		        super.onDraw(canvas);
		    }

		}
	  }
	  
	  @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        saveState();
	        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        saveState();
	    }
	    
	    @Override
	    protected void onResume() {
	        super.onResume();
	        populateFields();
	    }
	    
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.noteeditreg_menu, menu);
			return true;		
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case R.id.menu_delete:
				if(note != null){
	    			note.close();
	    			note = null;
	    		}
	    		if(mRowId != null){
	    			mDbHelper.deleteNote(mRowId);
	    		}
	    		finish();
		    	
		        return true;
		    case R.id.menu_addPassword:
	    		addPassword();
	    		break;
	    		
		    case R.id.menu_save:
	    		saveState();
	    		break;
		    }
		    return super.onOptionsItemSelected(item);
		}
		
		private void addPassword()
		{/*
     		AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Add Password");
			alert.setMessage("Enter a secure password");

				
			// Set an EditText view to get user input 
			
			final EditText input = new EditText(this);
			alert.setView(input);
			final EditText input2 = new EditText(this);
			alert.setView(input2);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  Editable value = input.getText();
			  Editable value2 = input2.getText();
			  // Do something with value!
			  if (value.equals(value2))
			  Toast.makeText(getApplicationContext(), value, 
					   Toast.LENGTH_LONG).show();
			  else
				  Toast.makeText(getApplicationContext(), "Wrong password", 
						   Toast.LENGTH_LONG).show();
			  }
			});

			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
			});

			alert.show();
			*/
			 LayoutInflater factory = LayoutInflater.from(this);
			 final View textEntryView = factory.inflate(R.layout.new_password, null);
			 //text_entry is an Layout XML file containing two text field to display in alert dialog
			 final EditText input1 = (EditText) textEntryView.findViewById(R.id.type_pass);
			 final EditText input2 = (EditText) textEntryView.findViewById(R.id.retype_pass);             
			 input1.setText("", TextView.BufferType.EDITABLE);
			 input2.setText("", TextView.BufferType.EDITABLE);
			 final AlertDialog.Builder alert = new AlertDialog.Builder(this);

			 alert.setTitle("Set Password")
			      .setView(textEntryView)
			      .setPositiveButton("Save", 
			          new DialogInterface.OnClickListener() {
			              public void onClick(DialogInterface dialog, int whichButton) {
			                     Log.i("AlertDialog","TextEntry 1 Entered "+input1.getText().toString());
			                     Log.i("AlertDialog","TextEntry 2 Entered "+input2.getText().toString());
			                  
			                  Editable value = input1.getText();
			       			  Editable value2 = input2.getText();
			       			  if (value.toString().equals(value2.toString()))
			       			  Toast.makeText(getApplicationContext(), value, 
			       					   Toast.LENGTH_LONG).show();
			       			  else
			       				  Toast.makeText(getApplicationContext(), "Wrong password", 
			       						   Toast.LENGTH_LONG).show();
			              }
			          })
			      .setNegativeButton("Cancel",
			          new DialogInterface.OnClickListener() {
			              public void onClick(DialogInterface dialog,
			                     int whichButton) {
			              }
			          });
			 alert.show();

		}
	    
	    private void saveState() {
	        String title = mTitleText.getText().toString();
	        String body = mBodyText.getText().toString();

	        if(mRowId == null){
	        	long id = mDbHelper.createNote(title, body, curDate, (byte)111);
	        	if(id > 0){
	        		mRowId = id;
	        	}else{
	        		Log.e("saveState","failed to create note");
	        	}
	        }else{
	        	if(!mDbHelper.updateNote(mRowId, title, body, curDate, (byte)1111)){
	        		Log.e("saveState","failed to update note");
	        	}
	        }
	    }
	    
	  
	    private void populateFields() {
	        if (mRowId != null) {
	            note = mDbHelper.fetchNote(mRowId);
	            startManagingCursor(note);
	            mTitleText.setText(note.getString(
	    	            note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
	            mBodyText.setText(note.getString(
	                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
	            curText = note.getString(
	                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
	        }
	    }


}
