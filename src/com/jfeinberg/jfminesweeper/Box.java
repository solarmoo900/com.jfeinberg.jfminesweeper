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
		/*int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
                (float) -5, getResources().getDisplayMetrics());
		this.setPadding(padding,padding,padding,padding);*/
		//this.setPadding(-10, -10, -10, -10);
		super.setText(String.valueOf(this.charRepresentation));
		this.defaultBG = this.getBackground();
	}
	

	public void makeMine() {
		this.isMine = true;
	}
	public int isMine() {
		return (this.isMine) ? 1 : 0;
	}

	public void addSurrounding() {
		this.surrounding++;
	}

	public int clearMe() {
		if (this.isHit) {
			return 1;
		}
		if (!this.isMine && !this.isFlagged && !this.isHit) {
			this.hitMe();
		}
		return this.surrounding;
	}

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
			//this.charRepresentation = 'F';
			this.ms.boxesFlagged++;
		}
		//super.setText(String.valueOf(this.charRepresentation));
		//this.setForeground(Color.red);
		//super.setTextColor(Color.RED);
		return "";
	}
	
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
