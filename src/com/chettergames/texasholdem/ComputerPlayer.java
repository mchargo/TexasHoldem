package com.chettergames.texasholdem;

import java.util.Random;

import com.chettergames.net.Output;

public class ComputerPlayer extends Player
{
	public ComputerPlayer(int number) 
	{
		super(number);
	}

	public void newRound()
	{
		cpuPause();
		super.newRound();
		Output.gameln(name + " is ready for a new round of poker.");
	}

	public int ante(int ante)
	{
		cpuPause();
		int playerAnte = super.ante(ante);

		if(ante == -1)
			Output.gameln(name + " doesn't have enough chips to play.");
		else Output.gameln(name + " anted " + playerAnte + " chips.");

		return playerAnte;
	}

	public void wonPot(int chipsWon)
	{
		cpuPause();
		super.wonPot(chipsWon);
		Output.gameln(name + " won " + chipsWon + " chips.");
	}

	public void newBettingRound()
	{
		cpuPause();
		super.newBettingRound();
		Output.gameln(name + " is ready for a new betting round.");
	}

	@Override
	public int getBet(int currentBet) 
	{
		cpuPause();
		Random random = new Random(System.nanoTime());
		int max = 0;

		if(currentBet == chips + getRoundBet())
		{
			max = chips;
			Output.gameln(name + " is all in.");
		} else if(random.nextInt(6) == 0)
		{
			// we are going to raise
			max = currentBet - getRoundBet();
			// minimum raise = 5
			max += random.nextInt(50) + 5;

			if(max >= chips)
			{
				max = chips;
				Output.gameln(name + " is all in.");
			} else Output.gameln(name + " has raised.");
		}else{
			// we will just call
			max = currentBet - getRoundBet();
			if(max > chips)
			{
				myRoundBet = 0;
				folded = true;
				max = -1;
				Output.gameln(name + " has folded.");
			}else{
				Output.gameln(name + " has called.");
			}
		}

		return max;
	}

	@Override
	public void recieveCard(Card card)
	{
		cpuPause();
		super.recieveCard(card);
		Output.gameln(name + " got card: " + card);
	} 

	public void joinGame(int startChips)
	{
		super.joinGame(startChips);
		Output.gameln(name + " has joined the game with " + startChips + " chips.");
	}

	@Override
	public void promptForName() 
	{
		name = "Computer Player " + number;
	}

	@Override
	public boolean isReady() 
	{
		return name != null;
	}

	private void cpuPause()
	{
		try
		{
			//Thread.sleep(1000);
		}catch(Exception e){}
	}
}
