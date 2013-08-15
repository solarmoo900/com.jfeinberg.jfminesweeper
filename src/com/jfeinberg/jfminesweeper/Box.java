package com.jfeinberg.jfminesweeper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TableRow;

@SuppressLint("ViewConstructor")
public class Box extends Button {

	private Minesweeper ms;
	private boolean isMine;
	private boolean isHit;
	private boolean isFlagged;
	private char charRepresentation;
	private int surrounding;
	private Drawable myflag;
	private Drawable defaultBG;

	/**
	 * Creates a button with additional attributes
	 *
	 * @param  ms        instance of the minesweeper game
	 * @param  context   context of the game for reference to screen and timer
	 * @return     		 an instance of a Box
	 */
	public Box(Minesweeper ms, Context context) {
		super(context);
		this.isMine = false;
		this.isHit = false;
		this.isFlagged = false;
		this.charRepresentation = ' ';
		this.surrounding = 0;
		this.ms = ms;
		int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) 48.0, getResources().getDisplayMetrics());
		TableRow.LayoutParams params = new TableRow.LayoutParams(width, LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.setMargins(-3, -3, -3, -3);
		this.setLayoutParams(params);
		float font = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 
                (float) 15.0, getResources().getDisplayMetrics());
		this.setTextSize(font);
		super.setText(String.valueOf(this.charRepresentation));
		this.defaultBG = this.getBackground();
	}
	
	/**
	 * Sets this Box as a mine
	 */
	public void makeMine() {
		this.isMine = true;
	}
	/**
	 * Returns an integer specifying if this Box is a mine
	 *
	 * @return      an integer
	 */
	public int isMine() {
		return (this.isMine) ? 1 : 0;
	}
	
	/**
	 * Adds 1 to the total surrounding mines
	 *
	 */
	public void addSurrounding() {
		this.surrounding++;
	}

	/**
	 * If a non-hit Box with 0 surrounding it will be hit automatically if a
	 * surrounding non-hit Box with 0 surrounding is hit
	 *
	 * @return      the surrounding mines
	 */
	public int clearMe() {
		if (this.isHit) {
			return 1;
		}
		if (!this.isMine && !this.isFlagged && !this.isHit) {
			this.hitMe();
		}
		return this.surrounding;
	}

	/**
	 * When a Box is hit it is filled in with the number of surrounding mines.
	 * If the it is 0 then it replaces it with a blue button
	 *
	 * @return      a string of the action that occurred or the amount of surrounding mines
	 */
	public String hitMe() {
		if (this.isHit) {
			return "HIT";
		} else {
			if (this.isFlagged) {
				return "FLAG";
			} else {
				this.isHit = true;
				if (this.isMine) {
					this.ms.addBoxFilled();
					return "DEAD";
				} else {
					this.charRepresentation = (char) (this.surrounding + 48);
					super.setText(String.valueOf(this.charRepresentation));
					this.ms.addBoxFilled();
					switch (this.surrounding) {
					case 0:
						super.setBackgroundColor(Color.BLUE);
						super.setText(String.valueOf(" "));
						break;
					case 1:
						super.setTextColor(Color.BLUE);
						break;
					case 2:
						super.setTextColor(Color.GRAY);
						break;
					case 3:
						super.setTextColor(Color.DKGRAY);
						break;
					case 4:
						super.setTextColor(Color.BLACK);
						break;
					case 5:
						super.setTextColor(Color.GREEN);
						break;
					case 6:
						super.setTextColor(Color.MAGENTA);
						break;
					case 7:
						super.setTextColor(getResources().getColor(android.R.color.holo_red_light));
						break;
					case 8:
						super.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
						break;
					}
					return String.valueOf(this.charRepresentation);
				}
			}
		}
	}

	
	/**
	 * If the minesweeper game is in flag mode this is called when a Box is hit.
	 * If it is set to be flagged it creates the bitmap or reuses an existing one
	 * and sets that as the background
	 *
	 * @return      an empty string on success
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public String flagMe() {
		if (this.isHit) {
			return "";
		} else if (this.isFlagged) {
			this.isFlagged = false;
			this.charRepresentation = ' ';
			this.ms.boxesFlagged--;
			super.setText(String.valueOf(this.charRepresentation));
			super.setTextColor(Color.RED);
			if (android.os.Build.VERSION.SDK_INT >= 16) {
		    	super.setBackground(this.defaultBG);
		    } else {
		    	super.setBackgroundDrawable(this.defaultBG);
		    }
		} else {
			this.isFlagged = true;
			if (this.myflag == null) {
				Bitmap flag = BitmapFactory.decodeResource(getResources(), R.drawable.flag);
			    Bitmap flagScaled = Bitmap.createScaledBitmap(flag, this.getWidth(), this.getHeight(), true);
			    this.myflag = new BitmapDrawable(getResources(),flagScaled);
			}
		    if (android.os.Build.VERSION.SDK_INT >= 16) {
		    	super.setBackground(this.myflag);
		    } else {
		    	super.setBackgroundDrawable(this.myflag);
		    }
			this.ms.boxesFlagged++;
		}
		return "";
	}
	
	/**
	 * Ran when a game is ended to show the location of all mines. If a Box
	 * is marked as with a flag but is not a mine it is replaced with a graphic.
	 *
	 * @param  wrongmine  sends a pre-scalled Bitmap for an incorrect marked mine
	 * @param  mymine     sends a pre-scalled Bitmap for a mine
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void showResult(Drawable wrongmine, Drawable mymine) {
	    if (this.isFlagged && !this.isMine) {
		    if (android.os.Build.VERSION.SDK_INT >= 16) {
		    	super.setBackground(wrongmine);
		    } else {
		    	super.setBackgroundDrawable(wrongmine);
		    }
	    } else if (this.isMine) {
	      super.setBackgroundColor(Color.RED);
	      
	      if (android.os.Build.VERSION.SDK_INT >= 16) {
	    	  super.setBackground(mymine);
	      } else {
	    	  super.setBackgroundDrawable(mymine);
	      }
	    } /*else {
	    	super.setText(String.valueOf(this.surrounding));
	    	if (this.surrounding == 0) {
	    		super.setBackgroundColor(Color.BLUE);
	    	}
	    }*/
	  }

}
