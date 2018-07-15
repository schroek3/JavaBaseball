import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;
// Author: Ken Schroeder
// Project Start Date: August 31, 2014
// Most Recent Update: February 19, 2015
// File Name: BaseballV2Main.java

/*
 * Baseball Main
 * Driver for Version 2 of Baseball Simulator
 * Version notes:
 *  	Added: 
 * 			Pitching Rotations
 * 			Interactive Game Play
 * 			Batter, tracking events
 * 			BoxScore
 * 			Season Stats
 * 		Modified: 
 * 			Advance Multiple Bases
 * 			Run parameter for players
 * Teams and rosters inspired by Bad News Baseball Video Game
 * --File found here: http://www.gamefaqs.com/nes/587104-bad-news-baseball/faqs/19322/
 * 
 * Functions of Program:
 * --Play various "seasons" with 12 teams
 * 	 -1 game
 * 		Interactive Play Mode:
 * 			-playGame - probabilities displayed, for future interactive version
 * 		Simulation Play Modes:
 * 			-play by play mode - result of play, outs, runs, base states after each play
 * 			-quick PBP sim - simpler version of pbp, doesn't track outs, bases, etc
 * 			-quick sim - plays game, prints line score
 * 			-tournament sim - prints in one line, including team records
 * 			-invisble sim - prints nothing, simply plays the game
 * 		Helper functions to reset inning states, display scoreboard, etc
 * 		Box Score Printout
 *  --Season Modes:
 *   	11 game schedule
 *   	16 game schedule
 *   	154 game schedule
 *   	162 game schedule
 *   	Best of n playoff series
 * 		Compiles player stats over the course of the season
 * 		Ability to print standings with or without two six-team divisions
 
 * 
 * To-Do: 
 * 		Create a pitcher class
 * 		Read Pitchers method, followed by read players method?
 * 		Save Pitcher Stats
 * 		Balance walks a bit better, some players don't walk. 
                       More XBH still as well.
 * 		Interactive game play
 * 		Subs
 * 		Bunts
 * 		Steals
 * 		Defense
 * 		Batted Balls driven
 * 		WAR for hitters/pitchers
 * 		wOBA
 * 		initializeGame function
 * 		recapGame function
 * 		one game function with bools for various functionality
 * 			-pa probabilities
 * 			-base/out state
 * 			-box score
 *			-etc
 *		fix standings strings so output is better formatted
 *		
 */


public class BaseballV2Main {
    static final String lineSeparator = "****************";
    static final int GAMELENGTH = 9;
    static boolean topBottom;
    static int inning;
    static int awayScore;
    static int homeScore;
    static int[] scoreboard = new int [GAMELENGTH*2];
    static Player[] bases = new Player [3];
    
    static double bb;
    static double hh;
    static double db;
    static double tb;
    static double hr;
    static double sing;
    static double out;
    static String play;
    static double result;
    
    //@SuppressWarnings("resource")
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
    	Scanner console = new Scanner(System.in);
    	//System.out.print("Enter the text file containing the teams: ");
    	//String teamInput = "TeamList.txt";
    	//readTeamList()
    	
    	/*
    	Team A = new Team();
    	Team B = new Team();
    	readTeam(A,"Atlanta.txt");
    	readTeam(B,"Boston.txt");
    	tournamentSim(A,B);
    	Team C = new Team();
    	C = bestOf12(A,B,3);
    	System.out.println(C.getName() + " was the winner!");
    	*/
    	
    	roundRobin11();
    	//roundRobin154();
	
    	console.close();

	
	}
    
    //--------------------------------------------------------------------
    // readTeam
    // checks inputted file, if it exists, reads in players/pitchers
    // sets lineup and pitching rotation
    //--------------------------------------------------------------------
    public static void readTeam(Team t, String inputFileName){
    	File inputFile = new File(inputFileName);
    	try(Scanner infile = new Scanner(inputFile)){
		if (t.readTeam(infile)==false){
		    System.out.println("ERROR: Team could not be created. Please check the input file");
		}	
		else{
		    while (infile.hasNext()){
			Player p = new Player();
			if (p.readPlayer(infile)){
			    p.createCard();
			    t.addPlayer(p);
			}
			else{
			    System.out.println("Player could not be created. Please check the input file.");
			}
		    }	
		}
		t.setRotation();
		t.setAce();
		t.setLeadoff();
	    }
    	catch (FileNotFoundException fnfe){
    		System.out.println("ERROR: File Not Found, please verify file name");
    	}
    }
    
    //--------------------------------------------------------------------
    // printInning
    // prints out what inning we're in, top/bottom of inning n
    // prints out scores
    //--------------------------------------------------------------------    
    public static void printInning(Team away, Team home){
    	if (topBottom){
    		System.out.println("\n\nTop of the "+ (1+ inning / 2));
    	}
    	else{
    		System.out.println("\n\nBottom of the " + (1 + inning/2));
    	}
    	System.out.println(away.getName()+": "+awayScore);
    	System.out.println(home.getName()+": "+homeScore);
    }
    
    //--------------------------------------------------------------------
    // matchup
    // determines the probability of events occuring
    // for all events (bb, hh, db, tb, hr):
    //  --computes average of pitchers rates and hitters rates
    // sets global variables for that plate appearance
    //-------------------------------------------------------------------- 
    
    public static void matchup(Player h, Player p){
    	DecimalFormat df = new DecimalFormat("#.#");
    	DecimalFormat avg = new DecimalFormat("#.###");
    	bb = (h.getWalksRate()+p.getWalksRate())/2;
    	hh = (h.getHitsRate()+p.getHitsRate())/2;
    	db = (h.getDoublesRate()+p.getDoublesRate())/2;
    	tb = (h.getTriplesRate()+p.getTriplesRate())/2;
    	hr = (h.getHomeRunsRate()+p.getHomeRunsRate())/2;
    	sing = hh-db-tb-hr;
    	out = 1-bb-hh;
    	System.out.println(lineSeparator);
    	System.out.println("\n\nBatter: " + h.getName()+ "\nPitcher: " + p.getName());
    	System.out.println("Outcome Odds: ");
    	System.out.print("\tOn Base:  " + df.format(100*(bb+hh)));
    	System.out.print("  Out:      " + df.format(100*out));
    	System.out.println("  Walk:     " + df.format(100*bb));
    	System.out.print("\tSingle:   "+ df.format(100*sing));
    	System.out.print("  Double:   "+ df.format(100*db));
    	System.out.print("  Triple:   "+ df.format(100*tb));
    	System.out.println("  Home Run: " + df.format(100*hr));
    	System.out.println(lineSeparator);
    	System.out.print("\tAVG: " + avg.format(h.getAvg()));
    	System.out.print("  OBP: " + avg.format(h.getOBP()));
    	System.out.print("  SLG: " + avg.format(h.getSLG()));
    	System.out.println(lineSeparator);
    }
    
    //--------------------------------------------------------------------
    // result
    // matchup without printing functionality
    // return string that displays what happened on a given play
    // increments batters stat totals based on result
    //-------------------------------------------------------------------- 
    
    public static String result(Player h, Player p){
    	//Removed matchup method and inserted into the gameplay methods
    	//matchup(h,p);
    	bb = (h.getWalksRate()+p.getWalksRate())/2;
    	hh = (h.getHitsRate()+p.getHitsRate())/2;
    	db = (h.getDoublesRate()+p.getDoublesRate())/2;
    	tb = (h.getTriplesRate()+p.getTriplesRate())/2;
    	hr = (h.getHomeRunsRate()+p.getHomeRunsRate())/2;
    	sing = hh-db-tb-hr;
    	out = 1-bb-hh;
    	String play = "";
	
    	double result = Math.random();
    	if (result < out){
    		play = "out";
    		h.incrementPA();
    	}
    	else if (result < bb+out){
    		play = "WALK";
    		h.incrementWalk();
    	}
    	else if (result < bb+out+sing){
    		play = "SINGLE";
    		h.incrementHits();
    	}
    	else if (result < bb+out+sing+db){
    		play = "DOUBLE";
    		h.incrementDoubles();
    	}
    	else if (result < bb+out+sing+db+tb){
    		play = "TRIPLE";
    		h.incrementTriples();
    	}
    	else{
    		play = "HOMERUN!!!";
    		h.incrementHomeRuns();
    	}
    	return play;
    	}
    
    //--------------------------------------------------------------------
    // run scores
    // used in pbp mode
    // prints out runner who scores
    // increments batter RBIs and runner runs
    //-------------------------------------------------------------------- 
    public static void runScores(Player runner, Player batter){
    	runner.incrementRuns();
    	batter.incrementRBIs();
    	System.out.println("\t"+runner.getName()+" scores.");
    }
    
    //--------------------------------------------------------------------
    // quickRunScores
    // when a run scores, increment player stats
    // used in non-pbp modes
    //-------------------------------------------------------------------- 
    public static void quickRunScores(Player runner, Player batter){
    	runner.incrementRuns();
    	batter.incrementRBIs();
    }
    
    public static int advanceBases(String play,Player batter,int runs){
    	double advancer = Math.random();
	
    	if (play.equals("WALK")){
    		System.out.println(batter.getName()+" walks.");
    		if (bases[2]!=null && bases[1] != null && bases[0] != null){
    			runScores(bases[2],batter);
    			//System.out.println("\t"+bases[2].getName()+" scores.");
    			bases[2] = bases[1];
    			bases[1] = bases[0];
    			bases[0] = batter;
    			runs++;
    		}
    		else if (bases[1] != null && bases[0] != null){
    			bases[2] = bases[1];
    			bases[1] = bases[0];
    			bases[0] = batter;
    		}
    		else if (bases[0] != null){
    			bases[1] = bases[0];
    			bases[0] = batter;
    		}
    		else{
    			bases[0] = batter;
    		}
    	}
    	else if (play.equals("SINGLE")){
    		System.out.println(batter.getName()+" singles.");
    		if (bases[2] != null){
    			runScores(bases[2],batter);
    			//System.out.println("\t"+bases[2].getName()+" scores");
    			runs++;
    			bases[2] = null;
    		}
    		if (bases[1] != null){
    			if (advancer < (bases[1].getRun()*.05+.25)){
    				runScores(bases[1],batter);
    				//System.out.println("\t"+bases[1].getName()+" scores");
    				runs++;
    				bases[1] = null;
    				bases[2] = bases[0];
    				bases[0] = null;
    			}
    			else{
    				bases[2] = bases[1];
    				bases[1] = bases[0];
    				bases[0] = null;
    			}
    		}
    		if (bases[0] != null){
    			if (advancer < (bases[0].getRun()*.05+.20)){
    				bases[2] = bases[0];
    			}else{
    				bases[1] = bases[0];
    			}
    		}
    		bases[0] = batter;
    	}
    	else if (play.equals("DOUBLE")){
    		System.out.println(batter.getName()+" doubles.");
    		if (bases[2] != null){
    			runScores(bases[2],batter);
    			//System.out.println("\t"+bases[2].getName()+ " scores");
    			runs++;
    		}
    		if (bases[1] != null){
    			runScores(bases[1],batter);
    			//System.out.println("\t"+bases[1].getName()+" scores.");
    			runs++;
    		}
    		if (bases[0] != null){
    			if (advancer < (bases[0].getRun()*.05+.2)){
    				runScores(bases[0],batter);
    				//System.out.println("\t"+bases[0].getName()+" scores.");
    				runs++;
    				bases[0] = null;
    			}
    		}
    		bases[2] = bases[0];
    		bases[1] = batter;
    		bases[0] = null;
    	}
    	else if (play.equals("TRIPLE")){
    		//System.out.println(batter.getName()+" triples.");
    		for (int i = 0; i < 3; i++){
    			if (bases[i] != null){
    				runScores(bases[i],batter);
    				//System.out.println("\t"+ bases[i].getName()+" scores.");
    				runs++;
    			}
    		}
    		bases[2] = batter;
    		bases[1] = null;
    		bases[0] = null;
    	}
    	else if (play.equals("HOMERUN!!!")){
    		System.out.println(batter.getName()+" hits a homerun!");
    		runs++;
    		for (int i = 0; i < 3; i++){
    			if (bases[i] != null){
    				runScores(bases[i],batter);
    				//System.out.println("\t"+ bases[i].getName()+" scores.");
    				runs++;
    			}
    		}
    		bases[2] = null;
    		bases[1] = null;
    		bases[0] = null;
    		runScores(batter,batter);
    		//System.out.println("\t"+batter.getName()+" scores.");
    	}
    	else{
    		System.out.println(batter.getName()+ " makes an out.");
    	}
    	return runs;
    }
    
    //--------------------------------------------------------------------
    // quickAdvance
    // advance runners, do not print output
    // non-print version of runScores
    //-------------------------------------------------------------------- 
    public static int quickAdvance(String play,Player batter,  int runs){
    	double advancer = Math.random();
	
    	if (play.equals("WALK")){
    		if (bases[2]!=null && bases[1] != null && bases[0] != null){
    			quickRunScores(bases[2],batter);
    			bases[2] = bases[1];
    			bases[1] = bases[0];
    			bases[0] = batter;
    			runs++;
    		}
    		else if (bases[1] != null && bases[0] != null){
    			bases[2] = bases[1];
    			bases[1] = bases[0];
    			bases[0] = batter;
    		}
    		else if (bases[0] != null){
    			bases[1] = bases[0];
    			bases[0] = batter;
    		}
    		else{
    			bases[0] = batter;
    		}
    	}
    	else if (play.equals("SINGLE")){
    		if (bases[2] != null){
    			quickRunScores(bases[2],batter);
    			runs++;
    			bases[2] = null;
    		}
    		if (bases[1] != null){
    			if (advancer < (bases[1].getRun()*.05+.25)){
    				quickRunScores(bases[1],batter);
    				runs++;
    				bases[1] = null;
    				bases[2] = bases[0];
    				bases[0] = null;
    			}
    			else{
    				bases[2] = bases[1];
    				bases[1] = bases[0];
    				bases[0] = null;
    			}
    		}
    		if (bases[0] != null){
    			if (advancer < (bases[0].getRun()*.05+.20)){
    				bases[2] = bases[0];
    			}else{
    				bases[1] = bases[0];
    			}
    		}
    		bases[0] = batter;
    	}
    	else if (play.equals("DOUBLE")){
    		if (bases[2] != null){
    			quickRunScores(bases[2],batter);
    			runs++;
    		}
    		if (bases[1] != null){
    			quickRunScores(bases[1],batter);
    			runs++;
    		}
    		if (bases[0]!= null){
    			if (advancer < (bases[0].getRun()*.05+.2)){
    				quickRunScores(bases[0],batter);
    				runs++;
    				bases[0] = null;
    			}
    		}
    		bases[2] = bases[0];
    		bases[1] = batter;
    		bases[0] = null;
    	}
    	else if (play.equals("TRIPLE")){
    		for (int i = 0; i < 3; i++){
    			if (bases[i] != null){
    				quickRunScores(bases[i],batter);
    				runs++;
    			}
    		}
    		bases[2] = batter;
    		bases[1] = null;
    		bases[0] = null;
    	}
    	else if (play.equals("HOMERUN!!!")){
    		runs++;
    		for (int i = 0; i < 3; i++){
    			if (bases[i] != null){
    				quickRunScores(bases[i],batter);
    				runs++;
    			}
    		}
    		bases[2] = null;
    		bases[1] = null;
    		bases[0] = null;
    		quickRunScores(batter,batter);
    	}
    	else{
    		//System.out.println(batter.getName()+ " makes an out.");
    	}
    	return runs;
    }
    	
    //--------------------------------------------------------------------
    // displayBases
    // print out and run states
    // used in pbp mode to keep user updated on game state
    //-------------------------------------------------------------------- 
    
    public static void displayBases(int outs, int runs){
    	System.out.println("\tOuts: "+ outs + "\tRuns: " + runs);
    	System.out.print("\tOn First: ");
    	if (bases[0] != null){
    		System.out.print(bases[0]);
    	}
    	System.out.print("\tOn Second: ");
    	if (bases[1] != null){
    		System.out.print(bases[1]);
    	}
    	System.out.print("\tOn Third: ");
    	if (bases[2] != null){
    		System.out.print(bases[2]);
    	}
    	System.out.println();
    }
    
    //--------------------------------------------------------------------
    // displayScorboard
    // print scoreboard version of a game
    //-------------------------------------------------------------------- 
    public static void displayScoreboard(Team a, Team b){
    	String space = " ";
    	String away = a.getName().trim();
    	String home = b.getName().trim();
    	for (int i = 0; i < Math.max(a.getName().length(),b.getName().length())+3-a.getName().length(); i++){
    		away += space;
    	}
    	System.out.println(lineSeparator);
    	System.out.print("\n"+away);
    	for (int i = 0; i < scoreboard.length; i = i + 2){
    		if ( i % 3 == 0){
    			System.out.print(" ");
    		}
    		System.out.print (scoreboard[i]);
    	}
    	System.out.println("  "+awayScore);
    	for (int i = 0; i < Math.max(a.getName().length(),b.getName().length())+3-b.getName().length(); i++){
    		home += space;
    	}
    	System.out.print(home);
    	for (int i = 1; i < scoreboard.length; i = i + 2){
    		if ( i % 3 == 1){
    			System.out.print(" ");
    		}
    		System.out.print (scoreboard[i]);
    	}
    	System.out.println("  "+homeScore);
    }
    
    //--------------------------------------------------------------------
    // clearScoreboard
    // clears runs off of scoreboard
    // used since scoreboad is a global variable as of yet
    //-------------------------------------------------------------------- 
    public static void clearScoreboard(){
    	for (int i = 0; i < GAMELENGTH*2; i++){
    		scoreboard[i] = 0;
    	}
    }
    
    //--------------------------------------------------------------------
    // clearBases
    // sets bases = null for all 3 bases
    // used between innings
    //-------------------------------------------------------------------- 
    public static void clearBases(){
    	for (int i = 0; i < bases.length; i++){
    		bases[i] = null;
    	}
    }
    
    //need to set different sleeps for different gameplays
    public static void playGame(Team away, Team home, boolean displayTitle, boolean displayInning,
    		boolean displayMatchup, boolean displayAdvance, boolean showBases, boolean displayInnSum, 
    		boolean showScoreboard, boolean displayBoxScore, boolean lineScore) throws InterruptedException{
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	
    	if (displayTitle){
    		System.out.println("\n"+lineSeparator+"\n"+away.getName() + " vs. " + home.getName());
    	}
    	
    	//reset game conditions to top of first
    	inning = 0;
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
    	
    	while (inning < GAMELENGTH*2){
    		//clear bases, reset outs and runs
    		clearBases();
    		int outs = 0;
    		int runs = 0;
    		
    		if (displayInning){
    			printInning(away,home);
    		}
    		
    		// do until outs = 3
    		while (outs < 3){
    			//check to see if inning needs to be played
    			if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    				break;
    			}
    			//Thread.sleep(2500);
    			String play;
    			Player batter;
    			//if top of inning, play with away batter, otherwise home
    			if (topBottom){
    				batter = away.nextBatter();
    				if(displayMatchup){
    					matchup(batter,home.getPitcher());
    				}
    				play = result(batter,home.getPitcher());
    			}
    			else{
    				batter = home.nextBatter();
    				if (displayMatchup){
    					matchup(batter,away.getPitcher());
    				}
    				play = result(batter,away.getPitcher());
    			}
    			
    			if (displayAdvance){
    				runs = advanceBases(play, batter,runs);
    			}
    			else{
    				runs = quickAdvance(play, batter, runs);
    			}
    			
    			if (play.equals("out")){
    				outs++;
    			}
    			
    			if(showBases){
    				displayBases(outs, runs);
    			}
    		}
    		if (topBottom){
    			awayScore += runs;
    			topBottom = false;
    		}
    		else{
    			homeScore += runs;
    			topBottom = true;
    		}
    		scoreboard[inning] = runs;
    		inning++;
    		if (displayInnSum){
    			System.out.println("\t"+runs+" runs scored.");
    			System.out.println(lineSeparator);
    		}
    	}
	
    	//Add to season stats
    	away.addToSeasonStats();
    	home.addToSeasonStats();

    	//Recap Game
    	if (showScoreboard){
    		displayScoreboard(away, home);
    	}
    	if (displayBoxScore){
    		away.printBoxScore();
    		home.printBoxScore();
    	}
    	if (lineScore){
    		printLineScore(away, home);
    	}
    }
    
    public static void printLineScore(Team away, Team home){
    	//create and display print string
    	//help format team name string so that Detroit is a long as Minnesota
    	String awaySpaces = "";
    	String homeSpaces = "";
    	for (int i = 0; i < 18-away.getName().length(); i++){
    		awaySpaces += " ";
    	}
    	for (int i = 0; i < 18-home.getName().length(); i++){
    		homeSpaces += " ";
    	}
    	addToTeamStats(away,home);
    	if (awayScore>homeScore){ 	
    		away.getPitcher().incrementWins();
    		home.getPitcher().incrementLosses();
    		System.out.print(away.getName() + awaySpaces + awayScore + ":"+ homeScore + "\t" + home.getName()+ homeSpaces + "\t\t");
    	}
    	else if (awayScore == homeScore){
    		away.getPitcher().incrementDraws();
    		home.getPitcher().incrementDraws();
    		System.out.print(home.getName() + homeSpaces + homeScore + ":"+ awayScore + "\t" + away.getName()+ awaySpaces + "\t\t");
    	}
    	else{
    		away.getPitcher().incrementLosses();
    		home.getPitcher().incrementWins();
    		System.out.print(home.getName() + homeSpaces + homeScore + ":"+ awayScore + "\t" + away.getName()+ awaySpaces + "\t\t");
    	}
    	System.out.print(away.toString()+awaySpaces+"\t");
    	System.out.println(home.toString());
    }
    
    
    //--------------------------------------------------------------------
    // playGame
    // sims a game
    // most data-intensive sim version
    // for each pa, displays probability of outcomes, bases, outs, etc
    // new for v2:
    //   matchup method is now in a separate call
    //-------------------------------------------------------------------- 
    public static void playGame(Team away, Team home) throws InterruptedException{
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	System.out.println("\n"+lineSeparator+"\n"+away.getName() + " vs. " + home.getName());
	
    	//Reset Game Conditions to top of first
    	inning = 0;
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
	
    	while (inning < GAMELENGTH*2){
    		//clear bases, reset outs and runs
    		clearBases();
    		int outs = 0;
    		int runs = 0;
    		printInning(away,home);
    		// do until outs = 3
    		while (outs < 3){
    			//check to see if inning needs to be played
    			if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    				break;
    			}
    			Thread.sleep(2500);
    			String play;
    			Player batter;
    			//if top of inning, play with away batter, otherwise home
    			if (topBottom){
    				batter = away.nextBatter();
    				matchup(batter,home.getPitcher());
    				play = result(batter,home.getPitcher());
    			}
    			else{
    				batter = home.nextBatter();
    				matchup(batter,away.getPitcher());
    				play = result(batter,away.getPitcher());
    			}
    			runs = advanceBases(play,batter,runs);
    			if (play.equals("out")){
    				outs++;
    			}
    			displayBases(outs, runs);
    		}
    		if (topBottom){
    			awayScore += runs;
    			topBottom = false;
    		}
    		else{
    			homeScore += runs;
    			topBottom = true;
    		}
    		scoreboard[inning] = runs;
    		inning++;
    		System.out.println("\t"+runs+" runs scored.");
    		System.out.println(lineSeparator);
    	}
	
    	//Add to season stats
    	away.addToSeasonStats();
    	home.addToSeasonStats();
    	//Recap Game
    	displayScoreboard(away, home);
    	away.printBoxScore();
    	home.printBoxScore();
    }
    
    //--------------------------------------------------------------------
    // pbpSim
    // sims a game
    // play game without probabilities
    // displays batter, bases, outs for each batter
    //-------------------------------------------------------------------- 
    public static void pbpSim(Team away, Team home) throws InterruptedException{
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	System.out.println("\n"+lineSeparator+"\n"+away.getName() + " vs. " + home.getName());
	
    	//Reset Game Conditions
    	inning = 0;
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
	
    	while (inning < GAMELENGTH*2){
    		Thread.sleep(2000);
    		clearBases();
    		int outs = 0;
    		int runs = 0;
    		printInning(away,home);
    		System.out.println(lineSeparator);
    		while (outs < 3){
    			if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    				break;
    			}
    			Thread.sleep(500);
    			String play;
    			Player batter;
    			if (topBottom){
    				batter = away.nextBatter();
    				play = result(batter,home.getPitcher());
    			}
    			else{
    				batter = home.nextBatter();
    				play = result(batter,away.getPitcher());
    			}
    			runs = advanceBases(play,batter,runs);
    			if (play.equals("out")){
    				outs++;
    			}
    			displayBases(outs, runs);
    		}
    		if (topBottom){
    			awayScore += runs;
    			topBottom = false;
    		}
    		else{
    			homeScore += runs;
    			topBottom = true;
    		}
    		scoreboard[inning] = runs;
    		inning++;
    		System.out.println(runs+" runs scored.");
    		System.out.println(lineSeparator);
    	}
    	displayScoreboard(away, home);
    }
    
    //--------------------------------------------------------------------
    // quickPBPSim
    // sims a game
    // pbpSim - base/out information
    //-------------------------------------------------------------------- 
    public static void quickPBPSim(Team away, Team home) throws InterruptedException{
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	System.out.println("\n"+lineSeparator+"\n"+away.getName() + " vs. " + home.getName());
	
    	//Reset Game Conditions
    	inning = 0;
    	//Set it to the top of the first
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
	
    	while (inning < GAMELENGTH*2){
    		Thread.sleep(2000);
    		//TO DO: What if the home team is winning in the bottom of the last inning
    		clearBases();
    		int outs = 0;
    		int runs = 0;
    		//Do not print inning?
    		printInning(away,home);
    		System.out.println(lineSeparator);
    		while (outs < 3){
    			if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    				break;
    			}
    			Thread.sleep(500);
    			String play;
    			Player batter;
    			if (topBottom){
    				batter = away.nextBatter();
    				play = result(batter,home.getPitcher());
    			}
    			else{
    				batter = home.nextBatter();
    				play = result(batter,away.getPitcher());
    			}
    			runs = advanceBases(play,batter,runs);
    			if (play.equals("out")){
    				outs++;
    			}
    		}
    		if (topBottom){
    			awayScore += runs;
    			topBottom = false;
    		}
    		else{
    			homeScore += runs;
    			topBottom = true;
    		}
    		scoreboard[inning] = runs;
    		inning++;
    		System.out.println(runs+" runs scored.");
    		System.out.println(lineSeparator);
    	}
	
    	away.printBoxScore();
    	home.printBoxScore();
    	displayScoreboard(away, home);
    }	
    
    //--------------------------------------------------------------------
    // quickSim
    // sims a game
    // plays game and prints line score
    //--------------------------------------------------------------------
    public static void quickSim(Team away, Team home){
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	
    	//	Reset Game Conditions
    	inning = 0;
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
	
    	while (inning < GAMELENGTH*2){
    		clearBases();
    		int outs = 0;
    		int runs = 0;
    		while (outs < 3){
    			if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    				break;
    			}
    			String play;
    			Player batter;
    			if (topBottom){
    				batter = away.nextBatter();
    				play = result(batter,home.getPitcher());
    			}
    			else{
    				batter = home.nextBatter();
    				play = result(batter,away.getPitcher());
    			}
    			runs = quickAdvance(play,batter,runs);
    			if (play.equals("out")){
    				outs++;
    			}
    		}
    		if (topBottom){
    			awayScore += runs;
    			topBottom = false;
    		}
    		else{
    			homeScore += runs;
    			topBottom = true;
    		}
    		scoreboard[inning] = runs;
    		inning++;
    	}
    	addToTeamStats(away,home);
    	displayScoreboard(away, home);
    }	
    
    //--------------------------------------------------------------------
    // tournamentSim
    // sims a game
    // prints on one line: score and team records
    //--------------------------------------------------------------------
    public static void tournamentSim(Team away, Team home) throws InterruptedException{
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	
    	//Reset Game Conditions
    	inning = 0;
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
    	
    	while (inning < GAMELENGTH*2){
    		clearBases();
    		int outs = 0;
    		int runs = 0;
 
    		while (outs < 3){
    			if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    				break;
    			}
    			//Thread.sleep(500);
    			String play;
    			Player batter;
    			if (topBottom){
    				batter = away.nextBatter();
    				play = result(batter,home.getPitcher());
    			}
    			else{
    				batter = home.nextBatter();
    				play = result(batter,away.getPitcher());
    			}
    			runs = quickAdvance(play,batter,runs);
    			if (play.equals("out")){
    				outs++;
    			}
    		}
    		if (topBottom){
    			awayScore += runs;
    			topBottom = false;
    		}
    		else{
    			homeScore += runs;
    			topBottom = true;
    		}
    		scoreboard[inning] = runs;
    		inning++;
    	}
    	away.addToSeasonStats();
    	home.addToSeasonStats();
    	
    	//create and display print string
    	//help format team name string so that Detroit is a long as Minnesota
    	String awaySpaces = "";
    	String homeSpaces = "";
    	for (int i = 0; i < 18-away.getName().length(); i++){
    		awaySpaces += " ";
    	}
    	for (int i = 0; i < 18-home.getName().length(); i++){
    		homeSpaces += " ";
    	}
    	addToTeamStats(away,home);
    	if (awayScore>homeScore){ 	
    		away.getPitcher().incrementWins();
    		home.getPitcher().incrementLosses();
    		System.out.print(away.getName() + awaySpaces + awayScore + ":"+ homeScore + "\t" + home.getName()+ homeSpaces + "\t\t");
    	}
    	else if (awayScore == homeScore){
    		away.getPitcher().incrementDraws();
    		home.getPitcher().incrementDraws();
    		System.out.print(home.getName() + homeSpaces + homeScore + ":"+ awayScore + "\t" + away.getName()+ awaySpaces + "\t\t");
    	}
    	else{
    		away.getPitcher().incrementLosses();
    		home.getPitcher().incrementWins();
    		System.out.print(home.getName() + homeSpaces + homeScore + ":"+ awayScore + "\t" + away.getName()+ awaySpaces + "\t\t");
    	}
    	System.out.print(away.toString()+awaySpaces+"\t");
    	System.out.println(home.toString());
    }
    
    //--------------------------------------------------------------------
    // invisibleSim
    // sims a game with no output
    //--------------------------------------------------------------------
    
    public static void invisbleSim(Team away, Team home){
    	away.setPitcher();
    	home.setPitcher();
    	away.setLineup();
    	home.setLineup();
    	
    	//Reset Game Conditions
    	//Set it to the top of the first
    	inning = 0;
    	topBottom = true;
    	awayScore = 0;
    	homeScore = 0;
    	clearScoreboard();
    	
    	while (inning < GAMELENGTH*2){
    	    clearBases();
    	    int outs = 0;
    	    int runs = 0;
    	    while (outs < 3){
    	    	if (inning == 2*GAMELENGTH-1 && homeScore+runs>awayScore){
    	    		break;
    	    	}
    	    	String play;
    	    	Player batter;
    	    	if (topBottom){
    	    		batter = away.nextBatter();
    	    		play = result(batter,home.getPitcher());
    	    	}
    	    	else{
    	    		batter = home.nextBatter();
    	    		play = result(batter,away.getPitcher());
    	    	}
    	    	runs = quickAdvance(play,batter,runs);
    	    	if (play.equals("out")){
    	    		outs++;
    	    	}
    	    }
    	    if (topBottom){
    	    	awayScore += runs;
    	    	topBottom = false;
    	    }
    	    else{
    	    	homeScore += runs;
    	    	topBottom = true;
    	    }
    	    scoreboard[inning] = runs;
    	    inning++;
    	}
    	away.addToSeasonStats();
    	home.addToSeasonStats();
    	
    	addToTeamStats(away,home);
    	if (awayScore>homeScore){ 	
    	    away.getPitcher().incrementWins();
    	    home.getPitcher().incrementLosses();
    	}
    	else if (awayScore == homeScore){
    	    away.getPitcher().incrementDraws();
    	    home.getPitcher().incrementDraws();
    	}
    	else{
    	    away.getPitcher().incrementLosses();
    	    home.getPitcher().incrementWins();
    	}    	
    }
    
    //--------------------------------------------------------------------
    // addToTeamStats
    // increments team stats: runs, runs allowed, wins, losses
    // no output
    //--------------------------------------------------------------------
    public static void addToTeamStats(Team away, Team home){
    	home.addRunsFor(homeScore);
    	home.addRunsAllowed(awayScore);
    	away.addRunsFor(awayScore);
    	away.addRunsAllowed(homeScore);
	
    	if (homeScore > awayScore){
    		home.incrementWins();
    		away.incrementLosses();
    	}
    	else if (homeScore == awayScore){
    		home.incrementDraws();
    		away.incrementDraws();
    	}
    	else{
    		home.incrementLosses();
    		away.incrementWins();
    	}
    }

    //--------------------------------------------------------------------
    // printStandings
    // prints two division team standings
    // allows for the ability to play a world cup style group stage
    // allows for the ability to play with two six team divisions
    //--------------------------------------------------------------------
    public static void printStandings(PriorityQueue<Team> PoolA, PriorityQueue<Team> PoolB ){
    	PriorityQueue<Team> A = PoolA;
    	PriorityQueue<Team> B = PoolB;
    	int ASize = A.size();
    	int BSize = B.size();
	
    	System.out.println(lineSeparator);
    	if (ASize > 4){
    		System.out.println("Ultra");
    	}
    	else{
    		System.out.println("Pool A");
    	}
    	
    	for (int i = 0; i < ASize; i++){
    		Team t = A.remove();
    		System.out.println(t.toStandingsString());
    	}
	
    	System.out.println(lineSeparator);
    	if (BSize > 4){
    		System.out.println("Super");
    	}
    	else{
    		System.out.println("Pool B");
    	}
    	
    	for (int i = 0; i < BSize; i++){
    		Team t = B.remove();
    		System.out.println(t.toStandingsString());
    	}
    }
    
    //--------------------------------------------------------------------
    // printStandings
    // prints standings in one division
    //--------------------------------------------------------------------
    public static void printStandings(PriorityQueue<Team> Standings){
    	PriorityQueue<Team> A = Standings;
    	int ASize = A.size();
    	System.out.println(lineSeparator);
    	System.out.println("Standings");
    	for (int i = 0; i < ASize; i++){
    		Team t = A.remove();
    		System.out.println(t.toStandingsString());
    	}
    }
    
    //--------------------------------------------------------------------
    // createTable
    // helper function to add all teams to the same priority queue
    //--------------------------------------------------------------------
    public static void createTable(Team A, Team B, Team C, Team D, Team E, Team F, Team G, Team H, Team I, Team J, Team K, Team L, PriorityQueue<Team> Ultra){
    	while (Ultra.size() > 0){
    		Ultra.remove();
    	}
    	Ultra.add(A);
    	Ultra.add(B);
    	Ultra.add(C);
    	Ultra.add(D);
    	Ultra.add(E);
    	Ultra.add(F);
    	Ultra.add(G);
    	Ultra.add(H);
    	Ultra.add(I);
    	Ultra.add(J);
    	Ultra.add(K);
    	Ultra.add(L);
    }
    //--------------------------------------------------------------------
    // createTable
    // helper function to add all teams to the same priority queue
    // creates two six team divisions
    //--------------------------------------------------------------------
    public static void createTable(Team A, Team B, Team C, Team D, Team E, Team F, Team G, Team H, Team I, Team J, Team K, Team L, PriorityQueue<Team> Ultra, PriorityQueue<Team> Super){
    	while (Super.size()>0){
    		Ultra.remove();
    		Super.remove();
    	}
    	Ultra.add(A);
    	Ultra.add(B);
    	Ultra.add(C);
    	Ultra.add(D);
    	Ultra.add(E);
    	Ultra.add(F);
    	Super.add(G);
    	Super.add(H);
    	Super.add(I);
    	Super.add(J);
    	Super.add(K);
    	Super.add(L);
    }
    
    //--------------------------------------------------------------------
    // seasonCSV
    // creates two csv sheets:
    //   -season stats with counting stats for all batters on all teams
    //   -pitcher stats with counting stats for all pitchers on all teams
    //--------------------------------------------------------------------
    public static void seasonCSV(Team A, Team B, Team C, Team D, Team E, Team F, Team G, Team H, Team I, Team J, Team K, Team L) throws FileNotFoundException{
    	PrintWriter output = new PrintWriter(new File("SeasonStats.csv"));
    	String playerStat = "Name,Team,Pos,PA,BB,H,2B,3B,HR,R,RBI,wOBA,AVG,OBP,SLG,OPS\n";
    	ArrayList<Team> teamList = new ArrayList<Team>();
    	teamList.add(A);
    	teamList.add(B);
    	teamList.add(C);
    	teamList.add(D);
    	teamList.add(E);
    	teamList.add(F);
    	teamList.add(G);
    	teamList.add(H);
    	teamList.add(I);
    	teamList.add(J);
    	teamList.add(K);
    	teamList.add(L);
    	for (int i = 0; i < teamList.size(); i++){
    		for (int j =0; j < 9; j++){
    			playerStat += teamList.get(i).nextBatter().seasonCSV()+"\n";
    		}
    	}
    	output.println(playerStat);
    	output.close();
	
    	PrintWriter pitcherStats = new PrintWriter(new File("PitcherStats.csv"));
    	String pitcherStat = "Name,Team,W,D,L\n";
    	for (int i = 0; i < teamList.size(); i++){
    		for (int j =0; j < 4; j++){
    			pitcherStat += teamList.get(i).getPitcher().pitcherCSV()+"\n";
    			teamList.get(i).setPitcher();
    		}
    	}
    	pitcherStats.println(pitcherStat);
    	pitcherStats.close();
    }
    
    //playGame(away,home,false,false,false,false,false,false,false,false,true);
    public static void elevenGames(Team A, Team B, Team C, Team D, Team E, Team F, Team G, Team H, Team I, Team J, Team K, Team L, boolean just11, PriorityQueue<Team> Super) throws InterruptedException{
    	//Week 1 of the cycle
		playGame(B,A,false,false,false,false,false,false,false,false,true);
		playGame(C,L,false,false,false,false,false,false,false,false,true);
		playGame(D,K,false,false,false,false,false,false,false,false,true);
		playGame(E,J,false,false,false,false,false,false,false,false,true);
		playGame(F,I,false,false,false,false,false,false,false,false,true);
		playGame(G,H,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 2 of the cycle
		playGame(L,B,false,false,false,false,false,false,false,false,true);
		playGame(A,G,false,false,false,false,false,false,false,false,true);
		playGame(H,F,false,false,false,false,false,false,false,false,true);
		playGame(I,E,false,false,false,false,false,false,false,false,true);
		playGame(J,D,false,false,false,false,false,false,false,false,true);
		playGame(K,C,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 3 of the cycle
		playGame(E,H,false,false,false,false,false,false,false,false,true);
		playGame(F,G,false,false,false,false,false,false,false,false,true);
		playGame(L,A,false,false,false,false,false,false,false,false,true);
		playGame(B,K,false,false,false,false,false,false,false,false,true);
		playGame(C,J,false,false,false,false,false,false,false,false,true);
		playGame(D,I,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 4 of the cycle
		playGame(H,B,false,false,false,false,false,false,false,false,true);
		playGame(I,L,false,false,false,false,false,false,false,false,true);
		playGame(J,K,false,false,false,false,false,false,false,false,true);
		playGame(A,E,false,false,false,false,false,false,false,false,true);
		playGame(F,D,false,false,false,false,false,false,false,false,true);
		playGame(G,C,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 5 of the cycle
		playGame(L,H,false,false,false,false,false,false,false,false,true);
		playGame(B,G,false,false,false,false,false,false,false,false,true);
		playGame(C,F,false,false,false,false,false,false,false,false,true);
		playGame(D,E,false,false,false,false,false,false,false,false,true);
		playGame(J,A,false,false,false,false,false,false,false,false,true);
		playGame(K,I,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 6 of the cycle
		playGame(D,B,false,false,false,false,false,false,false,false,true);
		playGame(E,L,false,false,false,false,false,false,false,false,true);
		playGame(F,K,false,false,false,false,false,false,false,false,true);
		playGame(G,J,false,false,false,false,false,false,false,false,true);
		playGame(H,I,false,false,false,false,false,false,false,false,true);
		playGame(A,C,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 7 of the cycle
		playGame(H,A,false,false,false,false,false,false,false,false,true);
		playGame(I,G,false,false,false,false,false,false,false,false,true);
		playGame(J,F,false,false,false,false,false,false,false,false,true);
		playGame(K,E,false,false,false,false,false,false,false,false,true);
		playGame(L,D,false,false,false,false,false,false,false,false,true);
		playGame(B,C,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 8 of the cycle
		playGame(K,L,false,false,false,false,false,false,false,false,true);
		playGame(F,A,false,false,false,false,false,false,false,false,true);
		playGame(G,E,false,false,false,false,false,false,false,false,true);
		playGame(H,D,false,false,false,false,false,false,false,false,true);
		playGame(I,C,false,false,false,false,false,false,false,false,true);
		playGame(J,B,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 9 of the cycle
		playGame(D,G,false,false,false,false,false,false,false,false,true);
		playGame(E,F,false,false,false,false,false,false,false,false,true);
		playGame(A,K,false,false,false,false,false,false,false,false,true);
		playGame(L,J,false,false,false,false,false,false,false,false,true);
		playGame(B,I,false,false,false,false,false,false,false,false,true);
		playGame(C,H,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 10 of the cycle
		playGame(G,L,false,false,false,false,false,false,false,false,true);
		playGame(H,K,false,false,false,false,false,false,false,false,true);
		playGame(I,J,false,false,false,false,false,false,false,false,true);
		playGame(D,A,false,false,false,false,false,false,false,false,true);
		playGame(E,C,false,false,false,false,false,false,false,false,true);
		playGame(F,B,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		if (just11){
			Thread.sleep(1000);
		}
		
		//Week 11 of the cycle
		playGame(K,G,false,false,false,false,false,false,false,false,true);
		playGame(L,F,false,false,false,false,false,false,false,false,true);
		playGame(B,E,false,false,false,false,false,false,false,false,true);
		playGame(C,D,false,false,false,false,false,false,false,false,true);
		playGame(A,I,false,false,false,false,false,false,false,false,true);
		playGame(J,H,false,false,false,false,false,false,false,false,true);
		createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
		printStandings(Super);
		Thread.sleep(3000);
    }
    public static void roundRobin11() throws InterruptedException, FileNotFoundException{
    	Team A = new Team();
    	Team B = new Team();
    	Team C = new Team();
    	Team D = new Team();
    	Team E = new Team();
    	Team F = new Team();
    	Team G = new Team();
    	Team H = new Team();
    	Team I = new Team();
    	Team J = new Team();
    	Team K = new Team();
    	Team L = new Team();
    	
    	PriorityQueue<Team> Super = new PriorityQueue<Team>();
    	
    	
    	//Create the teams, currently hardcoded to represent the 12 BNB Teams
    	readTeam(A,"Atlanta.txt");
    	readTeam(B,"Chicago.txt");
    	readTeam(C,"LosAngeles.txt");
    	readTeam(D,"NewYork.txt");
    	readTeam(E,"StLouis.txt");
    	readTeam(F,"SanFrancisco.txt");
    	readTeam(G,"Boston.txt");
    	readTeam(H,"Detroit.txt");
    	readTeam(I,"Minnesota.txt");
    	readTeam(J,"Oakland.txt");
    	readTeam(K,"Texas.txt");
    	readTeam(L,"Toronto.txt");
    	
    	elevenGames(A,B,C,D,E,F,G,H,I,J,K,L,true,Super);
    	
    	//Recreate table so that the playoffs can run without a hitch
    	createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
    	
    	seasonCSV(A,B,C,D,E,F,G,H,I,J,K,L);
    	Thread.sleep(3000);
    	playoff12Teams(1,1,1,1,2,2,Super);

    }
    
    public static void roundRobin154() throws InterruptedException, FileNotFoundException{
    	Team A = new Team();
    	Team B = new Team();
    	Team C = new Team();
    	Team D = new Team();
    	Team E = new Team();
    	Team F = new Team();
    	Team G = new Team();
    	Team H = new Team();
    	Team I = new Team();
    	Team J = new Team();
    	Team K = new Team();
    	Team L = new Team();
    	
    	PriorityQueue<Team> Super = new PriorityQueue<Team>();
    	
    	
    	//Create the teams, currently hardcoded to represent the 12 BNB Teams
    	readTeam(A,"Atlanta.txt");
    	readTeam(B,"Chicago.txt");
    	readTeam(C,"LosAngeles.txt");
    	readTeam(D,"NewYork.txt");
    	readTeam(E,"StLouis.txt");
    	readTeam(F,"SanFrancisco.txt");
    	readTeam(G,"Boston.txt");
    	readTeam(H,"Detroit.txt");
    	readTeam(I,"Minnesota.txt");
    	readTeam(J,"Oakland.txt");
    	readTeam(K,"Texas.txt");
    	readTeam(L,"Toronto.txt");

    	for (int i = 0; i < 14; i++){
    		elevenGames(A,B,C,D,E,F,G,H,I,J,K,L,false,Super);
    	}
    	
    	seasonCSV(A,B,C,D,E,F,G,H,I,J,K,L);
    	Thread.sleep(3000);

    	//Recreate table so that the playoffs can run without a hitch
    	createTable(A,B,C,D,E,F,G,H,I,J,K,L,Super);
    	
    	A.resetRotation();
    	B.resetRotation();
    	C.resetRotation();
    	D.resetRotation();
    	E.resetRotation();
    	F.resetRotation();
    	G.resetRotation();
    	H.resetRotation();
    	I.resetRotation();
    	J.resetRotation();
    	K.resetRotation();
    	L.resetRotation();
    	playoff12Teams(1,1,2,2,4,4,Super);

    }
    
    public static void playoff12Teams(int first, int second, int third, int fourth, int semi, int finals, PriorityQueue<Team> league) throws InterruptedException{
    	System.out.println("\n\nPlayoffs");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	Team A = league.remove();
    	A.setName("#1 " + A.getName());
    	Team B = league.remove();
    	B.setName("#2 " + B.getName());
    	Team C = league.remove();
    	C.setName("#3 " + C.getName());
    	Team D = league.remove();
    	D.setName("#4 " + D.getName());
    	Team E = league.remove();
    	E.setName("#5 " + E.getName());
    	Team F = league.remove();
    	F.setName("#6 " + F.getName());
    	Team G = league.remove();
    	G.setName("#7 " + G.getName());
    	Team H = league.remove();
    	H.setName("#8 " + H.getName());
    	Team I = league.remove();
    	I.setName("#9 " + I.getName());
    	Team J = league.remove();
    	J.setName("#10 " + J.getName());
    	Team K = league.remove();
    	K.setName("#11 " + K.getName());
    	Team L = league.remove();
    	L.setName("#12 " + L.getName());
    	Team S1 = new Team();
    	Team S2 = new Team();
    	Team S3 = new Team();
     	Team S4 = new Team();
    	Team S5 = new Team();
    	Team S6 = new Team();
     	Team S7 = new Team();
    	Team S8 = new Team();
    	Team S9 = new Team();
     	Team S10 = new Team();
    	Team S11 = new Team();
    	
    	Thread.sleep(1000);
    	System.out.println("\n\nFirst Round");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	S1 = bestOf12(I,L,first);
    	S2 = bestOf12(J,K,first);
    	System.out.println("\n\nSecond Round");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	Thread.sleep(3000);
    	S3 = bestOf12(H,S1,second);
    	S4 = bestOf12(G,S2,second);
    	System.out.println("\n\nThirdRound");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	Thread.sleep(3000);
    	S5 = bestOf12(E,S3,third);
    	S6 = bestOf12(F,S4,third);
    	System.out.println("\n\nFourth Round");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	Thread.sleep(3000);
    	S7 = bestOf12(D,S5,fourth);
    	S8 = bestOf12(C,S6,fourth);
    	System.out.println("\n\nFinal Four");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	Thread.sleep(3000);
    	S9 = bestOf12(A,S7,semi);
    	S10 = bestOf12(B,S8,semi);
    	System.out.println("\n\nChampionship");
		System.out.println(lineSeparator);
		System.out.println("*              *");
		System.out.println(lineSeparator);
    	Thread.sleep(5000);
    	S11 = bestOf12(S9, S10, finals);
    	
    	System.out.println(S11.getName() + " is the champion!!");
    	
    	
    }
    public static Team bestOf12(Team A, Team B, int n) throws InterruptedException{
    	A.setWins(0);
		A.setDraws(0);
		A.setLosses(0);
		B.setWins(0);
		B.setDraws(0);
		B.setLosses(0);
		
		while (B.getPoints() < 2*n && A.getPoints() < 2*n){
			playGame(B,A,false,false,false,false,false,false,false,false,true);
			Thread.sleep(1500);
		}
		
		if (A.getPoints() >= B.getPoints()){
			if (n > 1){
				System.out.println(A.getName()+" wins the series "+ A.getWins()+"-"+A.getLosses()+"-"+A.getDraws());
			}
			return A;
		}
		else{
			if (n >1){
				System.out.println(B.getName()+" wins the series "+ B.getWins()+"-"+B.getLosses()+"-"+B.getDraws());
			}
			return B;
		} 	
    }
    

}
