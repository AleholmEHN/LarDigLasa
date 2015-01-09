/**
 * Class for SetupView of the game
 * author elnhen-2
 */

package se.elnhen.lardiglasa;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import android.widget.RelativeLayout;
import android.widget.TextView;

public class SetupActivity extends Activity implements OnClickListener, OnCheckedChangeListener
{
	private ImageButton imgBtnRules;		// Button to view rules of the game
	private ImageButton imgBtnStart;		// Button to start the game
	private ImageButton imgBtnExit;			// Button to exit the game
	
	private RadioButton computerOpponent;	// Radiobutton for selecting ComputerOpponent
	private RadioButton player2;			// Radiobutton for selecting player2 as opponent
	
	private EditText player2Name;			// TextBox för player2 name
	private ImageView pl2Im;				// ImageView for player2
	
	public static final String MESSAGE_NAME1 = "NAME1";		// Messageconstant for Intent, Player1 name
	public static final String MESSAGE_NAME2 = "NAME2";		// Messageconstant for Intent, player2 name
	
	public static final int GAME_ENDED = 1;				// Intent message type.
	
	// Key for Shared Preferences, top level
	private final static String START_VALS = "startValues";
	// Key for Name Shared Preference
	public final static String PL_NAME1 = "player1";
	public final static String PL_NAME2 = "player2";
	
	
	RelativeLayout  layout = null;
	private int screenW= 0;
	private int screenH = 0;
	
	@Override
	 public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    //get/set the size here.
	    screenH = layout.getHeight();
	    screenW = layout.getWidth();
	    
	    setup();
	 }
	
	
	/**
	 * Called when first created
	 * @savedInstanceState     Saved state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setup);
		layout = (RelativeLayout) findViewById(R.id.relLayout);
		
		// Connect to XML-file and add listeners
		imgBtnRules = (ImageButton)findViewById(R.id.imageBtnRules);
		imgBtnRules.setOnClickListener(this);
		
		imgBtnStart = (ImageButton)findViewById(R.id.imageBtnStart);
		imgBtnStart.setOnClickListener(this);
		
		computerOpponent = (RadioButton)findViewById(R.id.radioBtnComputer);
		computerOpponent.setOnCheckedChangeListener(this);
		
		player2 = (RadioButton)findViewById(R.id.radioBtnOpponent);
		player2.setOnCheckedChangeListener(this);
		
		imgBtnExit = (ImageButton)findViewById(R.id.imageBtnExit);
		imgBtnExit.setOnClickListener(this);
		
		player2Name = (EditText)findViewById(R.id.editPlayer2);
		pl2Im = (ImageView)findViewById(R.id.imagePlayer2);
		
		EditText pl1Name = (EditText)findViewById(R.id.editPlayer1);
		
		// Get saved names (if any)
		SharedPreferences prefs = getSharedPreferences(START_VALS, MODE_PRIVATE);
		String name1 = prefs.getString(PL_NAME1, "");		// Get saved name
		String name2 = prefs.getString(PL_NAME2, "");
		
		if (name1.length() != 0)
			pl1Name.setText(name1);
		if (name2.length() != 0)
			player2Name.setText(name2);
			
		
	}
	
	public void setup()
	{
		
		int maxH=0, maxW=0;
		maxH = screenH/7;
		maxW = screenW/4;
		

		imgBtnRules.setMaxWidth(maxW);
		imgBtnRules.setMaxHeight(maxH);
		imgBtnRules.setAdjustViewBounds(true);
		
		ImageView pl1 = (ImageView)findViewById(R.id.imagePlayer1);
		pl1.setMaxHeight(maxH);
		pl1.setMaxWidth(maxW);
		
		ImageView pl2 = (ImageView)findViewById(R.id.imagePlayer2);
		pl2.setMaxHeight(maxH);
		pl2.setMaxWidth(maxW);
		
		
		ImageView imgV = (ImageView)findViewById(R.id.imageTitle);
		imgV.setMaxHeight(maxH*2);
		imgV.setAdjustViewBounds(true);
		
		
		imgBtnStart.setMaxHeight(maxH);
		imgBtnStart.setMaxWidth(maxW);
		imgBtnStart.setAdjustViewBounds(true);
		
		imgBtnExit.setMaxHeight(maxH);
		imgBtnExit.setMaxWidth(maxW);
		imgBtnExit.setAdjustViewBounds(true);
				
		

	}


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.imageBtnRules)		// Clicked rules button
		{
			// Show the rules for this game
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.helpdialog);
			dialog.setTitle("Spelregler");	

			// Connect to XML-file
			TextView theText =(TextView) dialog.findViewById(R.id.textView1);
			Resources resources = this.getResources();
			  
			// Get scale for screen
			float scale = resources.getDisplayMetrics().density;

			theText.setTextSize(35/scale);
			Typeface comic = Typeface.createFromAsset(this.getAssets(), "fonts/comicsans.ttf");
			theText.setTypeface(comic);			// Use typeface loaded

			int width = resources.getDisplayMetrics().widthPixels;
			int height = resources.getDisplayMetrics().heightPixels;
			// Set width and height of dialog
			dialog.getWindow().setLayout((int)(0.90*width), (int)(0.90*height));

			// set the custom dialog components - text, image and button
			ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.imageBtnExit);
			
			// Scale exit button			
			dialogButton.setMaxHeight(screenW/15);
			dialogButton.setMaxWidth(screenW/15);
			dialogButton.setAdjustViewBounds(true);
			
			// Scale title image
			ImageView title = (ImageView) dialog.findViewById(R.id.imageView1);
			title.setMaxWidth(screenW);
			title.setAdjustViewBounds(true);

			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					dialog.dismiss();			// Go back to activity
				}
			});
			dialog.show();				// Show the rules dialog
		
		}
		else if (v.getId() == R.id.imageBtnStart)			// Start the game
		{
			// Start the game
			Intent theGame = new Intent(this, MainActivity.class);
			// Get names from EditText-boxes
			EditText p1Name = (EditText) findViewById(R.id.editPlayer1);
			String message1 = p1Name.getText().toString();
			String message2 = "";
			if (message1.length() == 0)
				message1 = "Spelare 1";
			theGame.putExtra(MESSAGE_NAME1, message1);		// Send name to Intent
			
			
			RadioButton btnOpponent = (RadioButton)findViewById(R.id.radioBtnOpponent);
			if (btnOpponent.isChecked())	// Opponent selected (instead of play against computer)
			{
				EditText p2Name = (EditText)findViewById(R.id.editPlayer2);
				message2 = p2Name.getText().toString();
				if (message2.length() == 0)
					message2 = "Spelare 2";
				theGame.putExtra(MESSAGE_NAME2, message2);		// Send name to Intent
			}
			// Save the names in preferences
			SharedPreferences prefs = getSharedPreferences(START_VALS, 0);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString(PL_NAME1, message1);
			edit.putString(PL_NAME2, message2);
			edit.commit();

			startActivityForResult(theGame, GAME_ENDED);		// Start activity
		}
		else if (v.getId() == R.id.imageBtnExit)
		{
			finish();			// Exit game
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView.getId() == R.id.radioBtnComputer)
		{
			if (isChecked)	// Play against computer. Remove other options		
			{
				player2Name.setEnabled(false);
				player2Name.setVisibility(View.INVISIBLE);
				pl2Im.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			if (isChecked)			// Play against another opponent
			{
				player2Name.setEnabled(true);
				player2Name.setVisibility(View.VISIBLE);
				pl2Im.setVisibility(View.VISIBLE);
			}
		}
	}
	
	/**
	 * Get the result from activity
	 * @param requestCode    the request
	 * @param resultCode     the resul
	 * @param data    the Intent
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == GAME_ENDED)
			finish();
	}
}
