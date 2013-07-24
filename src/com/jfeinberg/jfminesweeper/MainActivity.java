package com.jfeinberg.jfminesweeper;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.Chronometer;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public Minesweeper ms;
	TableLayout gl;
	public int flagMode;
	ImageButton flagButton;
	public Chronometer timer;
	public TextView minesRemaining;
	public int seconds;
	boolean hasScrolled = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlayout);
                
        this.gl = (TableLayout) findViewById(R.id.minesweeper2);
        //this.gl = (LinearLayout) findViewById(R.id.minesweeper);
        
       
        this.flagMode = 0;
        timer = (Chronometer) findViewById(R.id.chronometer1);
        final TextView time = (TextView) findViewById(R.id.time);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.setOnChronometerTickListener(
                new Chronometer.OnChronometerTickListener(){

                	@Override
                	public void onChronometerTick(Chronometer chronometer) {
                		if (ms.isGoing && !ms.isPaused) {
                			seconds++;
                		}
                		time.setText(Integer.toString(seconds));
                	}}
        		);
        
        final LinearLayout zoom = (LinearLayout) findViewById(R.id.zoomme);
        final android.os.CountDownTimer cdt = new android.os.CountDownTimer(2000, 1000) {

			 public void onTick(long millisUntilFinished) {
				 zoom.setVisibility(View.VISIBLE);
			 }

			 public void onFinish() {
				zoom.setVisibility(View.GONE);
				 
			 }
		}.start();
        ScrollView sview = (ScrollView) findViewById(R.id.layout);
        HorizontalScrollView hview = (HorizontalScrollView) findViewById(R.id.horizontalLayout);
        ScrollView.OnTouchListener mylistener = new ScrollView.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				cdt.cancel();
				cdt.start();
				return false;
			}
		};
        sview.setOnTouchListener(mylistener);
        hview.setOnTouchListener(mylistener);
        

        minesRemaining = (TextView) findViewById(R.id.minesRemaining);
        
        flagButton = (ImageButton) findViewById(R.id.flagButton);
        final ImageButton flagB = flagButton;
        flagButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (flagMode == 1) {
					flagMode = 0;
					flagB.setImageDrawable(getResources().getDrawable(R.drawable.mine));
				} else if (flagMode == 0) {
					flagMode = 1;
					flagB.setImageDrawable(getResources().getDrawable(R.drawable.flag));
				} else {
					newGameMenu(true);
					flagMode = 0;
					flagB.setImageDrawable(getResources().getDrawable(R.drawable.mine));
				}
			}
		});
        
        this.ms = new Minesweeper(gl, this, 9, 15, 40);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	this.newGameMenu(true);
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if (item.getItemId() == R.id.menu_newgame) {
        	this.timer.stop();
        	this.newGameMenu(false);
    	}
    	
    	if (item.getItemId() == R.id.menu_stats) {
    		this.timer.stop();
    		this.statsMenu();
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onPause() {
    	if (ms.isGoing) {
    		this.timer.stop();
    	}
    	
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	if (ms.isGoing) {
    		this.timer.start();
    	}
    	
    	super.onResume();
    }
    
    
    public void newGame() {
    	this.gl.removeAllViews();
    	this.ms = new Minesweeper(this.gl, this);
    	this.timer.stop();
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	int action = event.getAction();
    	int keyCode = event.getKeyCode();
    	switch (keyCode) {
    	case KeyEvent.KEYCODE_VOLUME_UP:
    		if (action == KeyEvent.ACTION_UP) {
    		}
    		return true;
    	case KeyEvent.KEYCODE_VOLUME_DOWN:
    		if (action == KeyEvent.ACTION_DOWN) {
    			if (flagMode == 1) {
					this.flagMode = 0;
					this.flagButton.setImageDrawable(getResources().getDrawable(R.drawable.mine));
				} else if (flagMode == 0) {
					this.flagMode = 1;
					this.flagButton.setImageDrawable(getResources().getDrawable(R.drawable.flag));
				}
    		}
    		return true;
    	default:
    		return super.dispatchKeyEvent(event);
    	}
    }
    
    public void newGameMenu(boolean onStart) {
    	this.seconds = 0;
    	this.ms.isPaused = true;
    	LayoutInflater factory = LayoutInflater.from(this);            
        final View newGameView = factory.inflate(R.layout.newgame, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this); 
        alert.setTitle("New Game"); 
        alert.setMessage("Select your board options: "); 
        alert.setView(newGameView); 
        
        Button begButton = (Button) newGameView.findViewById(R.id.begButton);
        Button medButton = (Button) newGameView.findViewById(R.id.medButton);
        Button hardButton = (Button) newGameView.findViewById(R.id.hardButton);
        
        Button rowAdd = (Button) newGameView.findViewById(R.id.rowsAdd);
        final TextView numRows = (TextView) newGameView.findViewById(R.id.numRows);
        Button rowSub = (Button) newGameView.findViewById(R.id.rowsSub);
        
        Button colAdd = (Button) newGameView.findViewById(R.id.colsAdd);
        final TextView numCols = (TextView) newGameView.findViewById(R.id.numCols);
        Button colSub = (Button) newGameView.findViewById(R.id.colsSub);
        
        Button mineAdd = (Button) newGameView.findViewById(R.id.minesAdd);
        final TextView numMines = (TextView) newGameView.findViewById(R.id.numMines);
        Button mineSub = (Button) newGameView.findViewById(R.id.minesSub);
        
        final SharedPreferences prefs = this.getSharedPreferences("jfminesweeper", MainActivity.MODE_PRIVATE);
        int lastRows = prefs.getInt("lastRows", Constants.begRowDefault);
        int lastCols = prefs.getInt("lastCols", Constants.begColDefault);
        int lastMines = prefs.getInt("lastMines", Constants.begMineDefault);
        numRows.setText(Integer.toString(lastRows));
	  	numCols.setText(Integer.toString(lastCols));
	   	numMines.setText(Integer.toString(lastMines));
        
        
        begButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				numRows.setText(Integer.toString(Constants.begRowDefault));
            	numCols.setText(Integer.toString(Constants.begColDefault));
            	numMines.setText(Integer.toString(Constants.begMineDefault));
			}
		});
        medButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				numRows.setText(Integer.toString(Constants.medRowDefault));
            	numCols.setText(Integer.toString(Constants.medColDefault));
            	numMines.setText(Integer.toString(Constants.medMineDefault));
			}
		});
        hardButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				numRows.setText(Integer.toString(Constants.hardRowDefault));
            	numCols.setText(Integer.toString(Constants.hardColDefault));
            	numMines.setText(Integer.toString(Constants.hardMineDefault));
			}
		});
        
        rowAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int rows = Integer.parseInt(numRows.getText().toString()) + 1;
				if (rows > 9) {
					//rows = 9;
				}
				numRows.setText(Integer.toString(rows));
			}
		});
        rowSub.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int rows = Integer.parseInt(numRows.getText().toString()) - 1;
				if (rows < 1) {
					rows = 1;
				}
				numRows.setText(Integer.toString(rows));
			}
		});
        
        colAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int cols = Integer.parseInt(numCols.getText().toString()) + 1;
				numCols.setText(Integer.toString(cols));
			}
		});
        colSub.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int cols = Integer.parseInt(numCols.getText().toString()) - 1;
				if (cols < 1) {
					cols = 1;
				}
				numCols.setText(Integer.toString(cols));
			}
		});
        
        mineAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int mines = Integer.parseInt(numMines.getText().toString()) + 1;
				int rows = Integer.parseInt(numRows.getText().toString());
				int cols = Integer.parseInt(numCols.getText().toString());
				if (mines > ((rows*cols) - 1)) {
					mines = (rows * cols) - 1;
				}
				numMines.setText(Integer.toString(mines));
			}
		});
        mineSub.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int mines = Integer.parseInt(numMines.getText().toString()) - 1;
				if (mines < 1) {
					mines = 1;
				}
				numMines.setText(Integer.toString(mines));
			}
		});
        
        
        final MainActivity ma = this;
        final ImageButton flagB = (ImageButton) findViewById(R.id.flagButton);
        alert.setPositiveButton(R.string.start, new DialogInterface.OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int whichButton) { 
        		if (ms.isGoing) {
        			if (ms.getX() == Constants.begRowDefault && ms.getY() == Constants.begColDefault && ms.getMines() == Constants.begMineDefault) {
        				int score = prefs.getInt("begLosses", 0);
        				Editor editor = prefs.edit();
        				editor.putInt("begLosses", ++score);
        				editor.commit();
        			} else if (ms.getX() == Constants.medRowDefault && ms.getY() == Constants.medColDefault && ms.getMines() == Constants.medMineDefault) {
        				int score = prefs.getInt("medLosses", 0);
        				Editor editor = prefs.edit();
        				editor.putInt("medLosses", ++score);
        				editor.commit();
        			} else if (ms.getX() == Constants.hardRowDefault && ms.getY() == Constants.hardColDefault && ms.getMines() == Constants.hardMineDefault) {
        				int score = prefs.getInt("hardLosses", 0);
        				Editor editor = prefs.edit();
        				editor.putInt("hardLosses", ++score);
        				editor.commit();
        			}
        		}
        		gl.removeAllViews();
        		int mines = Integer.parseInt(numMines.getText().toString());
				int rows = Integer.parseInt(numRows.getText().toString());
				int cols = Integer.parseInt(numCols.getText().toString());
				Editor editor = prefs.edit();
				editor.putInt("lastRows", rows);
				editor.putInt("lastCols", cols);
				editor.putInt("lastMines", mines);
				editor.commit();
            	ms = new Minesweeper(gl, ma, rows, cols, mines);
        	} 
        }); 
        
        if (!onStart) {
        	alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
        		@Override
        		public void onClick(DialogInterface dialog, int which) {
        			if (ms.isGoing) {
        				timer.start();	
        			} else {
        				flagB.setImageDrawable(getResources().getDrawable(R.drawable.smile));
    					flagMode = 2;
        			}
        		}
        	});
        }
        
        alert.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				flagB.setImageDrawable(getResources().getDrawable(R.drawable.smile));
				flagMode = 2;
			}
		});
        
        alert.show();	
    }
    
    public void statsMenu() {
    	this.ms.isPaused = true;
    	LayoutInflater factory = LayoutInflater.from(this);            
        final View newStatsView = factory.inflate(R.layout.stats, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(this); 
        alert.setTitle("Statistics"); 
        alert.setMessage("Your game statistics: "); 
        alert.setView(newStatsView); 
        
        final SharedPreferences prefs = this.getSharedPreferences("jfminesweeper", MainActivity.MODE_PRIVATE);
        
        TextView begWins = (TextView) newStatsView.findViewById(R.id.begWins);
        TextView begLosses = (TextView) newStatsView.findViewById(R.id.begLosses);
        TextView begTime = (TextView) newStatsView.findViewById(R.id.begTime);
        
        TextView medWins = (TextView) newStatsView.findViewById(R.id.medWins);
        TextView medLosses = (TextView) newStatsView.findViewById(R.id.medLosses);
        TextView medTime = (TextView) newStatsView.findViewById(R.id.medTime);
        
        TextView hardWins = (TextView) newStatsView.findViewById(R.id.hardWins);
        TextView hardLosses = (TextView) newStatsView.findViewById(R.id.hardLosses);
        TextView hardTime = (TextView) newStatsView.findViewById(R.id.hardTime);
        
        begWins.setText(Integer.toString(prefs.getInt("begWins", 0)));
        begLosses.setText(Integer.toString(prefs.getInt("begLosses", 0)));
        begTime.setText(Integer.toString(prefs.getInt("begTime", 0)));
        
        medWins.setText(Integer.toString(prefs.getInt("medWins", 0)));
        medLosses.setText(Integer.toString(prefs.getInt("medLosses", 0)));
        medTime.setText(Integer.toString(prefs.getInt("medTime", 0)));
        
        hardWins.setText(Integer.toString(prefs.getInt("hardWins", 0)));
        hardLosses.setText(Integer.toString(prefs.getInt("hardLosses", 0)));
        hardTime.setText(Integer.toString(prefs.getInt("hardTime", 0)));
        
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int whichButton) { 

        	} 
        }); 
        
        alert.setNegativeButton(R.string.reset, new DialogInterface.OnClickListener() {
        	@Override
        	public void onClick(DialogInterface dialog, int whichButton) { 
        		Editor editor = prefs.edit();
				editor.putInt("begWins", 0);
				editor.putInt("begLosses", 0);
				editor.putInt("begTime", 0);
				editor.putInt("medWins", 0);
				editor.putInt("medLosses", 0);
				editor.putInt("medTime", 0);
				editor.putInt("hardWins", 0);
				editor.putInt("hardLosses", 0);
				editor.putInt("hardTime", 0);
				editor.commit();
        	} 
        }); 
        
        alert.show();
    }
    
}
