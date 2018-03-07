/*
Source File:   MainActivity
Authors & IDs: Taha Saleem     100803048\
               Ivan Echavarria 101092562
Creation Date: 02/03/2018


File Description: it's our Main activity

*/
package com.example.taha.slotmachine;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;





public class MainActivity extends AppCompatActivity  {

    private int playerMoney = 100;   // Player current money
    private int playerBet = 0;       // What the player is betting in the current turn
    private int wonInRound = 0;      // What the player earn in the current turn
    private int jackpot = 10000;     // Current Accumulated jackpot
    private String spinResult[]=new String[3];       // Result of the current spin
    private String fruits;           // Fruit result name

    // Counters for each possible result in an individual rail
    private int grapes = 0;
    private int bananas = 0;
    private int oranges = 0;
    private int cherries = 0;
    private int bars = 0;
    private int bells = 0;
    private int sevens = 0;
    private int blanks = 0;
    //TEXTO
    private TextView Bet;
    private TextView ReelOne;
    private TextView ReelTwo;
    private TextView ReelThree;
    private TextView playerMoneyText;
    private TextView jackPotText;
    private TextView wonInRoundText;

    //Lever sprites
    private ImageView leverOne;
    private ImageView leverTwo;
    private ImageView leverThree;
    //Gesture
   // private  GestureDetectorCompat detector;

    //Reel Images
    private int ReelImages[] = {R.drawable.blank, R.drawable.grapes, R.drawable.bananas, R.drawable.oranges, R.drawable.cherries, R.drawable.bars, R.drawable.bells, R.drawable.seven};

    //Image view reference to reel images
    private ImageView reelOneImg;
    private ImageView reelTwoImg;
    private ImageView reelThreeImg;
    //Holder for the reel Images
    private ImageView reelImgArray[] = new ImageView[3];

    //X and Y position for lever
    private float xInitial;
    private float yInitial;
    private float xFinal;
    private float yFinal;
    private boolean rightPosition = false;

    //Screen Height and Width
    private int height;
    private int width;

    // Sounds
    private MediaPlayer buttonClick;
    private MediaPlayer jackPotSound;
    private MediaPlayer leverPull;
    private MediaPlayer wheelspin;
    private MediaPlayer backgroundmusic;


    //alerts
    AlertDialog.Builder noMoney;
    AlertDialog.Builder zeroBet;
    AlertDialog.Builder wonTheJackpot;
    AlertDialog.Builder zeroMoney;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Creating Buttons
        Button plusOne = (Button) findViewById(R.id.PlusOne);
        plusOneBtnListener plusUno = new plusOneBtnListener();
        plusOne.setOnClickListener(plusUno);

        Button plusFive = (Button) findViewById(R.id.PlusFive);
        plusFiveBtnListener plusCinco = new plusFiveBtnListener();
        plusFive.setOnClickListener(plusCinco);

        Button plusTen = (Button) findViewById(R.id.PlusTen);
        plusTenBtnListener plusDiez = new plusTenBtnListener();
        plusTen.setOnClickListener(plusDiez);

        Button Restart = (Button) findViewById(R.id.Restart);
        restartBtnListener newGame = new restartBtnListener();
        Restart.setOnClickListener(newGame);

        Button quit = (Button) findViewById(R.id.Quit);
        quitButtonListener quitListener = new quitButtonListener();
        quit.setOnClickListener(quitListener);


        //Messing with text
        Bet = (TextView) findViewById(R.id.Bet);
        Bet.setText(String.valueOf(playerBet));
       // ReelOne = (TextView)findViewById(R.id.RailOne);
       // ReelTwo = (TextView) findViewById(R.id.RailTwo);
       // ReelThree = (TextView)findViewById(R.id.RailThree);
        playerMoneyText = (TextView)findViewById(R.id.PlayerMoney);
        playerMoneyText.setText(String.valueOf(playerMoney));
        jackPotText = (TextView)findViewById(R.id.jackPot);
        jackPotText.setText(String.valueOf(jackpot));
        wonInRoundText = (TextView)findViewById(R.id.WonInRound);
        wonInRoundText.setText(String.valueOf(wonInRound));

        //Lever Images
        leverOne = (ImageView)findViewById(R.id.leverOne);
        leverTwo = (ImageView)findViewById(R.id.leverTwo);

        //Reel images
        reelOneImg = (ImageView)findViewById(R.id.FirstReelImg);
        reelTwoImg = (ImageView)findViewById(R.id.secondReelImg);
        reelThreeImg = (ImageView)findViewById(R.id.thirdReelImg);

        reelImgArray[0] = reelOneImg;
        reelImgArray[1] = reelTwoImg;
        reelImgArray[2] = reelThreeImg;

        height = getResources().getDisplayMetrics().heightPixels;
        width  = getResources().getDisplayMetrics().widthPixels;

        //Create Sounds
        buttonClick = MediaPlayer.create(this, R.raw.buttonclick);
        jackPotSound= MediaPlayer.create(this, R.raw.jackpot);
        leverPull= MediaPlayer.create(this, R.raw.leverpull);
        wheelspin= MediaPlayer.create(this, R.raw.wheelspin);
        backgroundmusic = MediaPlayer.create(this,R.raw.backgroundmusic);
        backgroundmusic.start();
        backgroundmusic.isLooping();
    }

    // Reset the values after the current spin is done for a new spin
    private void resetFruitTally()
    {
        grapes = 0;
        bananas = 0;
        oranges = 0;
        cherries = 0;
        bars = 0;
        bells = 0;
        sevens = 0;
        blanks = 0;
    }

    // Reset the betting values ( Restart the game)
    private void resetAll()
    {
        playerMoney = 100;
        wonInRound = 0;
        jackpot = 10000;
        playerBet = 0;

        playerMoneyText.setText(String.valueOf(playerMoney));
        Bet.setText(String.valueOf(playerBet));
        jackPotText.setText(String.valueOf(jackpot));
        wonInRoundText.setText(String.valueOf(wonInRound));
    }

    // Check if the player won the jackpot
    private void checkJackPot()
    {
        int  jackPotTry = (int)Math.floor(Math.random()* 51 +1);
        int  jackPotWin = (int)Math.floor(Math.random()* 51 +1);
        if(jackPotTry == jackPotWin)
        {
            jackPotSound.start();
            //Add Jackpot text change to "You won the jack pot"
            playerMoney +=  jackpot;
            jackpot = 500;
            jackPotText.setText(String.valueOf(jackpot));

            wonTheJackpot = new AlertDialog.Builder(this);
            wonTheJackpot.setTitle("You Won The JACKPOT!!");
            // zeroBet.setMessage("");
            wonTheJackpot.setPositiveButton("OK",null);
            wonTheJackpot.show();
        }
    }

    // If won add funds to player money and attempt a jackpot win
    private void won()
    {
        playerMoney += wonInRound;
        resetFruitTally();
        checkJackPot();
        playerMoneyText.setText(String.valueOf(playerMoney));
        wonInRoundText.setText(String.valueOf(wonInRound));
        playerBet = 0;
        Bet.setText(String.valueOf(playerBet));
    }

    // If lost subtract the bet from player money
    private void lost()
    {
        playerMoney -= playerBet;
        wonInRound = 0;
        resetFruitTally();
        playerMoneyText.setText(String.valueOf(playerMoney));
        wonInRoundText.setText(String.valueOf(wonInRound));
        playerBet = 0;
        Bet.setText(String.valueOf(playerBet));

        if(playerMoney <= 0)
        {
            //Add on screen message with an option to restart
            zeroMoney = new AlertDialog.Builder(this);
            zeroMoney.setTitle("You Lose!");
            zeroMoney.setMessage("Restarting Game");
            zeroMoney.setPositiveButton("OK",null);
            zeroMoney.show();
            resetAll();
        }

    }

    // Check if the value falls withing a range
    private int checkRange(int value, int lowerBounds, int upperBounds )
    {
        if(value >= lowerBounds && value <= upperBounds)
        {
            return value;
        }
        else
        {
            return 0;// If number does not fall in range return 0 to move to the next case
        }
    }

    //Determine the betline Results
    private void Reels()
    {
        int outCome[] = {0, 0 , 0};

        for(int spin = 0; spin < 3; spin++)
        {
            outCome[spin] = (int)Math.floor((Math.random() * 65) + 1);

            if(outCome[spin] == checkRange(outCome[spin],1,27))// 41.5% probability
            {
                blanks++;
                reelImgArray[spin].setImageResource(ReelImages[0]);
                spinResult[spin] = "blank";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 28, 37)) // 15.4% probability
            {
                grapes++;
                reelImgArray[spin].setImageResource(ReelImages[1]);
                spinResult[spin] = "Grapes";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 38, 46)) // 13.8% probability )
            {
                bananas++;
                reelImgArray[spin].setImageResource(ReelImages[2]);
                spinResult[spin] = "Banana";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 47,54))  // 12.3% probability
            {
                oranges++;
                reelImgArray[spin].setImageResource(ReelImages[3]);
                spinResult[spin] = "Orange";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 55,59))// 7.7% probability
            {
                cherries++;
                reelImgArray[spin].setImageResource(ReelImages[4]);
                spinResult[spin] = "Cherry";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 60,62))// 4.6% probability
            {
                bars++;
                reelImgArray[spin].setImageResource(ReelImages[5]);
                spinResult[spin] = "Bar";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 63,64))// 3.1% probability
            {
                bells++;
                reelImgArray[spin].setImageResource(ReelImages[6]);
                spinResult[spin] = "Bell";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 65,65))// 1.5% probability
            {
                sevens++;
                reelImgArray[spin].setImageResource(ReelImages[7]);
                spinResult[spin] = "Seven";
            }
        }
    }

    /*This function calculates the player's winnings, if any*/
    private void determineWinnings()
    {
        if (blanks == 0)
        {
            if (grapes == 3) {
                wonInRound = playerBet * 10;
            }
            else if(bananas == 3) {
                wonInRound = playerBet * 20;
            }
            else if (oranges == 3) {
                wonInRound = playerBet * 30;
            }
            else if (cherries == 3) {
                wonInRound = playerBet * 40;
            }
            else if (bars == 3) {
                wonInRound = playerBet * 50;
            }
            else if (bells == 3) {
                wonInRound = playerBet * 75;
            }
            else if (sevens == 3) {
                wonInRound = playerBet * 100;
            }
            else if (grapes == 2) {
                wonInRound = playerBet * 2;
            }
            else if (bananas == 2) {
                wonInRound = playerBet * 2;
            }
            else if (oranges == 2) {
                wonInRound = playerBet * 3;
            }
            else if (cherries == 2) {
                wonInRound = playerBet * 4;
            }
            else if (bars == 2) {
                wonInRound = playerBet * 5;
            }
            else if (bells == 2) {
                wonInRound = playerBet * 10;
            }
            else if (sevens == 2) {
                wonInRound = playerBet * 20;
            }
            else if (sevens == 1) {
                wonInRound = playerBet * 5;
            }
            else {
                wonInRound = playerBet;
            }

            won();
        }
        else
        {
            lost();
        }
    }
    private void Spin()
    {

        if(playerBet > playerMoney)
        {
            noMoney = new AlertDialog.Builder(this);
            noMoney.setTitle("Not Enough Money!");
            //noMoney.setMessage("");
            noMoney.setPositiveButton("OK",null);
            noMoney.show();
            playerBet = 0;
            Bet.setText(String.valueOf(playerBet));
            //Add on screen massage you are poor
        }
        else if(playerBet == 0)
        {
            zeroBet = new AlertDialog.Builder(this);
            zeroBet.setTitle("Please Make a Bet!");
           // zeroBet.setMessage("");
            zeroBet.setPositiveButton("OK",null);
            zeroBet.show();
            playerBet = 0;
            Bet.setText(String.valueOf(playerBet));

        }

        else if(playerBet <= playerMoney)
        {
            Reels();
            fruits = spinResult[0] + " - " +spinResult[1] +" - "+spinResult[2];
            determineWinnings();
        }
        else
        {
            //Please enter a valid bet amount
        }
    }

    //Plus one to the bet money
    private class plusOneBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            playerBet ++;
            buttonClick.start();
            Bet.setText(String.valueOf(playerBet));
        }
    }
    //Plus five to the bet money
    private class plusFiveBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            playerBet += 5;
            buttonClick.start();
            Bet.setText(String.valueOf(playerBet));
        }
    }
    //Plus ten to the bet money
    private class plusTenBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            playerBet += 10;
            buttonClick.start();
            Bet.setText(String.valueOf(playerBet));
        }
    }
    //Restart defautl values
    private class restartBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            buttonClick.start();
            resetAll();
        }
    }
    //quit game
    private class quitButtonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            buttonClick.start();
            finish();
        }
    }

    //this function is charge of lever, so it checks if the input in range of the lever image to
    // apply the animation and also calls the spin function
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
               // Toast.makeText(this,"Action was DOWN",Toast.LENGTH_LONG).show();
                xInitial = event.getRawX();
                yInitial = event.getRawY();

                if( xInitial >= ((width * 0.92) - 200) && (yInitial >= (height * 0.50 - 300) && (yInitial <= (height* 0.50 - 50))) )
                {
                    rightPosition = true;
                }
                return true;
            case (MotionEvent.ACTION_MOVE) :
             //   Toast.makeText(this,"Action was MOVE",Toast.LENGTH_LONG).show();
                xFinal = event.getRawX();
                yFinal = event.getRawY();

                if(xFinal < (xInitial - 150))
                {
                    rightPosition = false;
                }
                else if((xFinal >= xInitial) && yFinal > (yInitial + 100) && rightPosition)
                {
                    leverOne.setVisibility(View.INVISIBLE);
                    leverTwo.setVisibility(View.VISIBLE);

                    leverPull.start();


                    rightPosition = false;
                    Handler  handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[1]);
                            reelImgArray[1].setImageResource(ReelImages[2]);
                            reelImgArray[2].setImageResource(ReelImages[3]);
                            wheelspin.seekTo(0);
                            wheelspin.start();
                        }
                    }, 200);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[4]);
                            reelImgArray[1].setImageResource(ReelImages[5]);
                            reelImgArray[2].setImageResource(ReelImages[6]);
                        }
                    }, 400);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[7]);
                            reelImgArray[1].setImageResource(ReelImages[0]);
                            reelImgArray[2].setImageResource(ReelImages[2]);
                        }
                    }, 600);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[3]);
                            reelImgArray[1].setImageResource(ReelImages[1]);
                            reelImgArray[2].setImageResource(ReelImages[7]);
                        }
                    }, 800);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[1]);
                            reelImgArray[1].setImageResource(ReelImages[2]);
                            reelImgArray[2].setImageResource(ReelImages[3]);
                            wheelspin.start();
                        }
                    }, 1000);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[4]);
                            reelImgArray[1].setImageResource(ReelImages[5]);
                            reelImgArray[2].setImageResource(ReelImages[6]);
                        }
                    }, 1200);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[7]);
                            reelImgArray[1].setImageResource(ReelImages[0]);
                            reelImgArray[2].setImageResource(ReelImages[2]);
                        }
                    }, 1400);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reelImgArray[0].setImageResource(ReelImages[3]);
                            reelImgArray[1].setImageResource(ReelImages[1]);
                            reelImgArray[2].setImageResource(ReelImages[7]);
                        }
                    }, 1600);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            wheelspin.pause();
                            Spin();
                        }
                    }, 1800);


                }
                return true;
            case (MotionEvent.ACTION_UP) :

                rightPosition = false;
                leverOne.setVisibility(View.VISIBLE);
                leverTwo.setVisibility(View.INVISIBLE);
                return true;
            case (MotionEvent.ACTION_SCROLL):
               Toast.makeText(this,"Taha Scroll",Toast.LENGTH_LONG).show();
                return true;
            default :
                return super.onTouchEvent(event);
        }

       // return super.onTouchEvent(event);
    }


}
