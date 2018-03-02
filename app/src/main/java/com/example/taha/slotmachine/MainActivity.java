package com.example.taha.slotmachine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

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

    //Gesture
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ReelOne = (TextView)findViewById(R.id.RailOne);
        ReelTwo = (TextView) findViewById(R.id.RailTwo);
        ReelThree = (TextView)findViewById(R.id.RailThree);
        playerMoneyText = (TextView)findViewById(R.id.PlayerMoney);

        //Gesture detector

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
    }

    // Check if the player won the jackpot
    private void checkJackPot()
    {
        int  jackPotTry = (int)Math.floor(Math.random()* 51 +1);
        int  jackPotWin = (int)Math.floor(Math.random()* 51 +1);
        if(jackPotTry == jackPotWin)
        {
            //Add Jackpot text change to "You won the jack pot"
            playerMoney +=  jackpot;
            jackpot = 500;
        }
    }

    // If won add funds to player money and attempt a jackpot win
    private void won()
    {
        playerMoney += wonInRound;
        resetFruitTally();
        checkJackPot();
        playerMoneyText.setText(String.valueOf(playerMoney));
    }

    // If lost subtract the bet from player money
    private void lost()
    {
        playerMoney -= playerBet;
        resetFruitTally();
        playerMoneyText.setText(String.valueOf(playerMoney));
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
                spinResult[spin] = "blank";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 28, 37)) // 15.4% probability
            {
                grapes++;
                spinResult[spin] = "Grapes";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 38, 46)) // 13.8% probability )
            {
                bananas++;
                spinResult[spin] = "Banana";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 47,54))  // 12.3% probability
            {
                oranges++;
                spinResult[spin] = "Orange";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 55,59))// 7.7% probability
            {
                cherries++;
                spinResult[spin] = "Cherry";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 60,62))// 4.6% probability
            {
                bars++;
                spinResult[spin] = "Bar";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 63,64))// 3.1% probability
            {
                bells++;
                spinResult[spin] = "Bell";
            }
            else if(outCome[spin] == checkRange(outCome[spin], 65,65))// 1.5% probability
            {
                sevens++;
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
        //playerBet = resultButton;

        if(playerMoney == 0)
        {
            //Add on screen message with an option to restart
        }
        else if(playerBet > playerMoney)
        {
            //Add on screen massage you are poor
        }
        else if(playerBet <= playerMoney)
        {
            Reels();
            fruits = spinResult[0] + " - " +spinResult[1] +" - "+spinResult[2];
            ReelOne.setText(spinResult[0]);
            ReelTwo.setText(spinResult[1]);
            ReelThree.setText(spinResult[2]);
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
            Bet.setText(String.valueOf(playerBet));
            Spin();

        }
    }
    //Plus five to the bet money
    private class plusFiveBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            playerBet += 5;
            Bet.setText(String.valueOf(playerBet));
        }
    }
    //Plus ten to the bet money
    private class plusTenBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            playerBet += 10;
            Bet.setText(String.valueOf(playerBet));
        }
    }
    //Plus one to the bet money
    private class restartBtnListener implements View.OnClickListener{
        @Override
        public void onClick(View v)
        {
            resetAll();
        }
    }

    private class quitButtonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
               // Toast.makeText(this,"Action was DOWN",Toast.LENGTH_LONG).show();
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Toast.makeText(this,"Action was MOVE",Toast.LENGTH_LONG).show();
                return true;
            case (MotionEvent.ACTION_UP) :
               // Toast.makeText(this,"Action was UP",Toast.LENGTH_LONG).show();
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Toast.makeText(this,"Action was CANCEL",Toast.LENGTH_LONG).show();
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Toast.makeText(this,"Movement occurred outside bounds",Toast.LENGTH_LONG).show();
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
