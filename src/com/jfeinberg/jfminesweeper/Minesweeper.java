package com.jfeinberg.jfminesweeper;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

@SuppressLint("ViewConstructor")
public class Minesweeper {

	Box[][] board;
	private int x;
	private int y;
	private int mines;
	private int boxesFilled;
	private MainActivity context;
	public boolean isGoing;
	public boolean isPaused;
	SharedPreferences prefs;
	public int boxesFlagged;


	public Minesweeper(ViewGroup pane, MainActivity context) {
		this.createBoard(pane,9,5,8,context);
	}
	
	public Minesweeper(ViewGroup pane, MainActivity context, int rows, int cols, int mines) {
		this.createBoard(pane,rows,cols,mines,context);
		prefs = context.getSharedPreferences("jfminesweeper", MainActivity.MODE_PRIVATE);
	}
	
	public int getX() {
		return this.x;
	}
	public int getY() {
		return this.y;
	}
	public int getMines() {
		return this.mines;
	}
	public int getBoxesFilled() {
		return this.boxesFilled;
	}

	public void addBoxFilled() {
		this.boxesFilled++;
	}

	/**
	 * Creates the actual minesweeper board as well as all Boxes. Sets up certain
	 * boxes as mines.
	 *
	 * @param  frame2   tablerow to hold board
	 * @param  x        number of rows
	 * @param  y        number of columns
	 * @param  mines	number of mines
	 * @param  context  context of activity
	 */
	public void createBoard(ViewGroup frame2, int x, int y, int mines, MainActivity context) {
		TableLayout frame = (TableLayout) frame2;
		this.context = context;
		this.context.minesRemaining.setText(Integer.toString(mines));
		this.x = x;
		this.y = y;
		this.mines = mines;
		this.boxesFilled = 0;
		this.isGoing = false;
		this.isPaused = false;
		int minesCreated = 0;
		this.board = new Box[x][y];
		
		
		for (int i = 0; i < this.x; ++i) {
			TableRow row = new TableRow(context);
			int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
	                (float) 48.0, context.getResources().getDisplayMetrics());
			TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, height);
			params.gravity = Gravity.CENTER;
			params.setMargins(0, 0, 0, 0);
			row.setLayoutParams(params);
			row.setOrientation(0);
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
	                (float) -2, context.getResources().getDisplayMetrics());
			row.setPadding(padding,padding,padding,padding);
			
			for (int j = 0; j < this.y; ++j) {
				this.board[i][j] = new Box(this, context);
				final int localI = i;
				final int localJ = j;
				final MainActivity newcontext = context;
				this.board[i][j].setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						final int result;
						if (newcontext.flagMode == 1) {
							result = flagIt(localI, localJ);
						} else {
							result = hitIt(localI, localJ);
						}
						getResult(result);
					}
				});
				row.addView(this.board[i][j]);
			}
			frame.addView(row);
		}
		
		// use a while loop for more randomness
		while (minesCreated < this.mines) {
			Random r = new Random();
			int i = r.nextInt(this.x);
			int j = r.nextInt(this.y);
			double rando =  (((this.mines-minesCreated) / ((this.x*this.y)-((i * this.y) + j)*1.0)) * 10);
			int surroundingMines = this.getSurrounding(i, j);
			int temp = r.nextInt(10-(surroundingMines/2));
			if (rando >= temp && this.board[i][j].isMine() == 0) {
				this.board[i][j].makeMine();
				this.addSurrounding(i, j);
				minesCreated++;
			}
		}
		/*for (int i = 0; i < this.x; ++i) {
			for (int j = 0; j < this.y; ++j) {
				Random r = new Random();
				double rando =  (((this.mines-minesCreated) / ((this.x*this.y)-((i * this.y) + j)*1.0)) * 10);
				int surroundingMines = this.getSurrounding(i, j);
				int temp = r.nextInt(10-surroundingMines);
				if (rando >= temp && minesCreated < this.mines) {
					this.board[i][j].makeMine();
					this.addSurrounding(i, j);
					minesCreated++;
				}
			}
		}*/
		
		
		// zoom in and out code
		final Minesweeper ms2 = this;
		ImageView zoomOutButton = (ImageView) context.findViewById(R.id.zoomout);
        zoomOutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ms2.resizeMe(1);
			}
		});
        
        ImageView zoomInButton = (ImageView) context.findViewById(R.id.zoomin);
        zoomInButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ms2.resizeMe(0);
			}
		});
        
        context.flagButton.setImageDrawable(context.getResources().getDrawable(R.drawable.mine));
		context.flagMode = 0;
	}
	
	/**
	 * When a Box is marked as a mine, the surrounding mines surrounding
	 * value is increased
	 *
	 * @param  x   x coordinate of Box hit
	 * @param  y   y coordinate of Box hit
	 * @see    Box
	 */
	public void addSurrounding(int x, int y) {
		if (x != 0) {
			this.board[x-1][y].addSurrounding();
			if (y != 0) {
				this.board[x-1][y-1].addSurrounding();
			}
			if (y != this.y-1) {
				this.board[x-1][y+1].addSurrounding();
			}
		}

		if (x != this.x-1) {
			this.board[x+1][y].addSurrounding();
			if (y != 0) {
				this.board[x+1][y-1].addSurrounding();
			}
			if (y != this.y-1) {
				this.board[x+1][y+1].addSurrounding();
			}
		}

		if (y != 0) {
			this.board[x][y-1].addSurrounding();
		}
		if (y != this.y-1) {
			this.board[x][y+1].addSurrounding();
		}
	}
	
	/**
	 * If Box is hit an has no surrounding mines, the surrounding
	 * boxes will be marked as hit
	 *
	 * @param  x   x coordinate of Box hit
	 * @param  y   y coordinate of Box hit
	 * @see    Box
	 */
	public void fixSurrounding(int x, int y) {
		if (x != 0) {
			if (this.board[x-1][y].clearMe() == 0) {
				this.fixSurrounding(x-1, y);
			}
			if (y != 0) {
				if (this.board[x-1][y-1].clearMe() == 0) {
					this.fixSurrounding(x-1, y-1);
				}
			}
			if (y != this.y-1) {
				if (this.board[x-1][y+1].clearMe() == 0) {
					this.fixSurrounding(x-1, y+1); 
				}
			}
		}

		if (x != this.x-1) {
			if (this.board[x+1][y].clearMe() == 0) {
				this.fixSurrounding(x+1, y); 
			}
			if (y != 0) {
				if (this.board[x+1][y-1].clearMe() == 0) {
					this.fixSurrounding(x+1, y-1);
				}
			}
			if (y != this.y-1) {
				if (this.board[x+1][y+1].clearMe() == 0) {
					this.fixSurrounding(x+1, y+1);
				}
			}
		}

		if (y != 0) {
			if (this.board[x][y-1].clearMe() == 0) {
				this.fixSurrounding(x, y-1);
			}
		}
		if (y != this.y-1) {
			if (this.board[x][y+1].clearMe() == 0) {
				this.fixSurrounding(x, y+1);
			}
		}
	}
	
	/**
	 * Calculates the amount of surrounding mines to a Box
	 *
	 * @param  x   x coordinate of Box to lookup
	 * @param  y   y coordinate of Box to lookup
	 * @return     an integer with the amount of surrounding mines
	 * @see    Box
	 */
	public int getSurrounding(int x, int y) {
		int count = 0;
		if (x != 0) {
			count += this.board[x-1][y].isMine();
			if (y != 0) {
				count += this.board[x-1][y-1].isMine();
			}
			if (y != this.y-1) {
				count += this.board[x-1][y+1].isMine();
			}
		}

		if (x != this.x-1) {
			count += this.board[x+1][y].isMine();
			if (y != 0) {
				count += this.board[x+1][y-1].isMine();
			}
			if (y != this.y-1) {
				count += this.board[x+1][y+1].isMine();
			}
		}

		if (y != 0) {
			count += this.board[x][y-1].isMine();
		}
		if (y != this.y-1) {
			count += this.board[x][y+1].isMine();
		}
		return count;
	}
	
	/**
	 * Attempts to hit a Box
	 *
	 * @param  x   x coordinate of Box hit
	 * @param  y   y coordinate of Box hit
	 * @return     an integer specifying result of hitting attempt
	 * @see    Box
	 */
	public int hitIt(int x, int y) {
		if (!this.isGoing) {
			this.isGoing = true;
			this.context.timer.setBase(SystemClock.elapsedRealtime());
			this.context.timer.start();
		}
		if (this.isPaused) {
			this.isPaused = false;
			this.context.timer.start();
		}
		if (x >= 0 && x < this.x && y >= 0 && y < this.y) {
			String result = this.board[x][y].hitMe();
			if (result.equals("0")) {
				this.fixSurrounding(x,y);
				this.board[x][y].setClickable(false);
				return 1;
			} else if (result.equals("DEAD")) {
				return 0;
			} else if (result.equals("FLAG")) {
				return 2;
			} else if (result.equals("HIT")) {
				return 3;
			} else {
				this.board[x][y].setClickable(false);
				return 1;
			}
		}
		return 4;
	}
	
	/**
	 * Marks a Box as flagged or unflags it
	 *
	 * @param  x   x coordinate of Box hit
	 * @param  y   y coordinate of Box hit
	 * @return     an integer specifying result of flagging attempt
	 * @see    Box
	 */
	public int flagIt(int x, int y) {
		if (!this.isGoing) {
			this.isGoing = true;
			this.context.timer.setBase(SystemClock.elapsedRealtime());
			this.context.timer.start();
		}
		if (this.isPaused) {
			this.isPaused = false;
			this.context.timer.start();
		}
	    if (x >= 0 && x < this.x && y >= 0 && y < this.y) {
	    	this.board[x][y].flagMe();
	    	int minesRemaining = this.mines - this.boxesFlagged;
	    	this.context.minesRemaining.setText(Integer.toString(minesRemaining));
	    	return 1;
	    }
	    return 4;
	}
	
	/**
	 * When a game is finished, scalled Bitmaps are generated and the results shown
	 *
	 */
	public void showResults() {
		Bitmap wmine = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.wrongmine);
		Bitmap wmineScaled = Bitmap.createScaledBitmap(wmine, this.board[0][0].getWidth(), this.board[0][0].getHeight(), true);
		Drawable wrongmine = new BitmapDrawable(this.context.getResources(),wmineScaled);
		Bitmap mine = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.mine);
		Bitmap mineScaled = Bitmap.createScaledBitmap(mine, this.board[0][0].getWidth(), this.board[0][0].getHeight(), true);
		Drawable mymine = new BitmapDrawable(this.context.getResources(),mineScaled);
	    for (int i = 0; i < this.x; ++i) {
	      for (int j = 0; j < this.y; ++j) {
	        this.board[i][j].showResult(wrongmine,mymine);
	      }
	    }
	  }
	
	/**
	 * Gets the result of all actions on a button. If a victory or defeat the 
	 * players statistics are updated
	 *
	 * @param  result    an integer sent from the Box specifying hit result
	 */
	public void getResult(int result) {
		boolean good = true;
		switch (result) {
		case 0:
			for (int i = 0; i < this.x; ++i) {
				for (int j = 0; j < this.y; ++j) {
					this.board[i][j].setClickable(false);
				}
			}
			good = false;
			
			if (this.x == Constants.begRowDefault && this.y == Constants.begColDefault && this.mines == Constants.begMineDefault) {
				int score = this.prefs.getInt("begLosses", 0);
				Editor editor = this.prefs.edit();
				editor.putInt("begLosses", ++score);
				editor.commit();
			} else if (this.x == Constants.medRowDefault && this.y == Constants.medColDefault && this.mines == Constants.medMineDefault) {
				int score = this.prefs.getInt("medLosses", 0);
				Editor editor = this.prefs.edit();
				editor.putInt("medLosses", ++score);
				editor.commit();
			} else if (this.x == Constants.hardRowDefault && this.y == Constants.hardColDefault && this.mines == Constants.hardMineDefault) {
				int score = this.prefs.getInt("hardLosses", 0);
				Editor editor = this.prefs.edit();
				editor.putInt("hardLosses", ++score);
				editor.commit();
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
			builder.setMessage("You have Died!");
			builder.setPositiveButton(R.string.newgame, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					context.newGameMenu(false);
				}
			});
			final ImageButton flagB = (ImageButton) this.context.findViewById(R.id.flagButton);
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					flagB.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
					context.flagMode = 2;
				}
			});
			AlertDialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					flagB.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
					context.flagMode = 2;
				}
			});
			dialog.show();
			
			this.showResults();
			
			this.isGoing = false;
			this.context.timer.stop();
			
			break;
		case 2:
			System.out.println("You have already flagged that!");
			break;
		case 3:
			System.out.println("You already hit that!");
			break;
		case 4:
			System.out.println("Invalid Input!");
			break;
		default:
			break;
		}
		
		if ((this.getX() * this.getY()) - this.getBoxesFilled() - this.getMines() <= 0 && good != false) {
			for (int i = 0; i < this.x; ++i) {
				for (int j = 0; j < this.y; ++j) {
					this.board[i][j].setClickable(false);
				}
			}
			
			//long time = SystemClock.elapsedRealtime() - this.context.timer.getBase();
			//time /= 1000;
			int time = this.context.seconds;
			
			if (this.x == Constants.begRowDefault && this.y == Constants.begColDefault && this.mines == Constants.begMineDefault) {
				int score = this.prefs.getInt("begWins", 0);
				int prevTime = this.prefs.getInt("begTime", 0);
				time = time < prevTime || prevTime == 0 ? time : prevTime;
				Editor editor = this.prefs.edit();
				editor.putInt("begWins", ++score);
				editor.putInt("begTime", time);
				editor.commit();
			} else if (this.x == Constants.medRowDefault && this.y == Constants.medColDefault && this.mines == Constants.medMineDefault) {
				int score = this.prefs.getInt("medWins", 0);
				int prevTime = this.prefs.getInt("medTime", 0);
				time = time < prevTime || prevTime == 0 ? time : prevTime;
				Editor editor = this.prefs.edit();
				editor.putInt("medWins", ++score);
				editor.putInt("medTime", time);
				editor.commit();
			} else if (this.x == Constants.hardRowDefault && this.y == Constants.hardColDefault && this.mines == Constants.hardMineDefault) {
				int score = this.prefs.getInt("hardWins", 0);
				int prevTime = this.prefs.getInt("hardTime", 0);
				time = time < prevTime || prevTime == 0 ? time : prevTime;
				Editor editor = this.prefs.edit();
				editor.putInt("hardWins", ++score);
				editor.putInt("hardTime", time);
				editor.commit();
			}
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
			builder.setMessage("You have WON!");
			builder.setPositiveButton(R.string.newgame, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					context.newGameMenu(false);
				}
			});
			final ImageButton flagB = (ImageButton) this.context.findViewById(R.id.flagButton);
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					flagB.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
					context.flagMode = 2;
				}
			});
			AlertDialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					flagB.setImageDrawable(context.getResources().getDrawable(R.drawable.smile));
					context.flagMode = 2;
				}
			});
			dialog.show();
			
			
			
			this.isGoing = false;
			this.context.timer.stop();
			
		}
	}
	
	/**
	 * Zooms in or out of the board by scalling the size of each Box
	 *
	 * @param  big       increase or decrease the size
	 */
	public void resizeMe(int big) {
		double resizeButton = 0.0;
		double resizeText = 0.0;
		boolean myCondition = false;
		if (big == 0) {
			resizeButton = 1.25;
			resizeText = 0.3125;
			myCondition = true;
		} else {
			resizeButton = 0.8;
			resizeText = 0.3125;
			myCondition = false;
		}
		

		for (int x = 0; x < this.getX(); ++x) {
			for (int y = 0; y < this.getY(); ++y) {
				Button change = this.board[x][y];
				LayoutParams params = change.getLayoutParams();
				
				if (((change.getHeight() * resizeButton > 70.0) && !myCondition) || ((change.getHeight() * resizeButton < 130.0) && myCondition)) {
					params.height = (int) (change.getHeight() * resizeButton);
					params.width = (int) (change.getWidth() * resizeButton);
					change.setLayoutParams(params);
					change.setTextSize((float) (params.height * resizeText));
				}
			}
		}
	}

}
