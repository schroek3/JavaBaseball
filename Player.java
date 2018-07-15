import java.text.DecimalFormat;
import java.util.Scanner;


public class Player {
	private String name;
	private String pos;
	private double pa;
	private double walks;
	private double hits;
	private double doubles;
	private double triples;
	private double homeRuns;
	private double run;
	private String team;
	//private double cardOutPerc;
	private double cardWalks;
	private double cardHits;
	private double cardDoubles;
	private double cardTriples;
	private double cardHomeRuns;
	
	private int gamePA;
	private int gameWalks;
	private int gameHits;
	private int gameDoubles;
	private int gameTriples;
	private int gameHomeRuns;
	private int gameRuns;
	private int gameRBIs;
	
	private int seasonPA;
	private int seasonWalks;
	private int seasonHits;
	private int seasonDoubles;
	private int seasonTriples;
	private int seasonHomeRuns;
	private int seasonRuns;
	private int seasonRBIs;
	
	private int gameIP;
	private int seasonIP;
	private int seasonW;
	private int seasonD;
	private int seasonL;
	
	DecimalFormat avg = new DecimalFormat("#.###");
	
	private static final double avWalkPerc = .0866;
	private static final double avHitPerc = .2551;
	private static final double avDoublePerc = .0460;
	private static final double avTriplePerc = .0042;
	private static final double avHomeRunPerc = .0262;
	private static final double wOBA1B = .89;
	private static final double wOBA2B = 1.24;
	private static final double wOBA3B = 1.59;
	private static final double wOBAHR = 2.07;
	private static final double wOBABB = 0.69;
	private static final double scale = 1.24;
	//private static final double avOutPerc = (1-avWalkPerc-avHitPerc-avDoublePerc-avTriplePerc-avHomeRunPerc);
	
	
	public Player (){
		name = "";
		pos = "";
		pa = 0;
		walks = 0;
		hits = 0;
		doubles = 0;
		triples = 0;
		homeRuns = 0;
		run = 0;
	}
	
	public Player (String n){
		name = n;
	}
	
	public Player(String n, String pos, double p, double w, double h, double d, double t, double hr, double r){
		name = n;
		this.pos = pos;
		pa = p;
		walks = w;
		hits = h;
		doubles = d;
		triples = t;
		homeRuns = hr;
		run = r;
	}
	
	public void setTeam(String s){
		team = s;
	}
	
	public void clearGameStats(){
		gamePA = 0;
		gameWalks = 0;
		gameHits = 0;
		gameDoubles = 0;
		gameTriples = 0;
		gameHomeRuns = 0;
		gameRuns = 0;
		gameRBIs = 0;
	}
	
	public String boxString(){
		return getPaddedName()+"\t"+gamePA+"\t"+gameWalks+"\t"+gameHits+ "\t" + gameDoubles + "\t" +gameTriples + "\t" +gameHomeRuns + "\t" + gameRuns + "\t" + gameRBIs;
	}
	
	public String seasonString(){
		return getPaddedName()+"\t"+seasonPA+"\t"+seasonWalks+"\t"+seasonHits+ "\t" + seasonDoubles + "\t" +seasonTriples + "\t" +seasonHomeRuns+ "\t" + seasonRuns + "\t" + seasonRBIs;
	}
	
	public String seasonCSV(){
		return getPaddedName()+","+team+","+pos+","+seasonPA+","+seasonWalks+","+seasonHits+ "," + seasonDoubles + "," +seasonTriples + "," +seasonHomeRuns+ "," + seasonRuns + "," + seasonRBIs + "," + getWOBA() + "," + getAvg() + "," + getOBP() + "," + getSLG() + "," + (getOBP() + getSLG());
	}
	
	public String pitcherCSV(){
		return getPaddedName()+","+team+","+seasonW+","+seasonD+","+seasonL;
	}
	public void incrementPA(){
		gamePA++;
	}
	
	public void incrementWalk(){
		gameWalks++;
		gamePA++;
	}
	
	public void incrementHits(){
		gameHits++;
		gamePA++;
	}
	
	public void incrementDoubles(){
		gameDoubles++;
		gameHits++;
		gamePA++;
	}
	
	public void incrementTriples(){
		gameTriples++;
		gameHits++;
		gamePA++;
	}
	
	public void incrementHomeRuns(){
		gameHomeRuns++;
		gameHits++;
		gamePA++;
	}
	
	public void incrementRuns(){
		gameRuns++;
	}
	
	public void incrementRBIs(){
		gameRBIs++;
	}
	
	public double getAvg(){
		return hits/getABs();
	}
	public double getABs(){
		return pa-walks;
	}
	public double getOBP(){
		return (hits + walks)/pa;
	}
	
	public double getSLG(){
		return (hits + doubles + 2* triples + 3 * homeRuns)/getABs();
	}
	
	public double getWOBA(){
		return (wOBA1B*(hits-doubles-triples-homeRuns)+ wOBABB*walks+wOBA2B*doubles+wOBA3B*triples+wOBAHR*homeRuns)/pa;
	}
	
	public String getTeam(){
		return team;
	}
	public double getWalksRate(){
		return cardWalks;
	}
	
	public double getHitsRate(){
		return cardHits;
	}
	
	public double getDoublesRate(){
		return cardDoubles;
	}
	
	public double getTriplesRate(){
		return cardTriples;
	}
	
	public double getHomeRunsRate(){
		return cardHomeRuns;
	}
	
	public double getRun(){
		return run;
	}
	
	public String getName(){
		return name;
	}
	
	public String getPaddedName(){
		String paddedName = name;
		for (int i = name.length(); i <= 12; i++){
			paddedName += "";
		}
		return paddedName;
	}
	
	public boolean readPlayer(Scanner infile){
		infile.useDelimiter(",");
		String n;
		String pos;
		String p;
		String bb;
		String h;
		String db;
		String tb;
		String hr;
		String r;
		
		
		if (infile.hasNext()){
			n = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			pos = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			p = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			bb = infile.next().trim();
		}
		else {
			return false;
		}
		if (infile.hasNext()){
			h = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			db = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			tb = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			hr = infile.next().trim();
		}
		else{
			return false;
		}
		if (infile.hasNext()){
			r = infile.next().trim();
		}
		else{
			return false;
		}
		
		name = n;
		this.pos = pos;
		pa = Double.parseDouble(p);
		walks = Double.parseDouble(bb);
		hits = Double.parseDouble(h);
		doubles = Double.parseDouble(db);
		triples = Double.parseDouble(tb);
		homeRuns = Double.parseDouble(hr);
		run = Double.parseDouble(r);
		
		return true;
	}
	
	public void createCard(){
		cardWalks = 2*walks/pa-avWalkPerc;
		cardHits = 2*hits/pa-avHitPerc;
		cardDoubles = 2*doubles/pa-avDoublePerc;
		cardTriples = 2*triples/pa-avTriplePerc;
		cardHomeRuns = 2*homeRuns/pa-avHomeRunPerc;
		//cardOutPerc = 1-cardHits-cardWalks;
	}

	public String toString(){
		DecimalFormat avg = new DecimalFormat("#.###");
		String displayOBP = avg.format(getWOBA());
		return pos+" "+name+" "+displayOBP+"/"+(int)homeRuns;
	}

	public void incrementWins(){
		seasonW++;
	}
	public int getWin(){
		return seasonW;
	}
	public int getDraw(){
		return seasonD;
	}
	public int getLoss(){
		return seasonL;
	}
	
	public void incrementLosses(){
		seasonL++;
	}
	public void incrementDraws(){
		seasonD++;
	}
	public void addToSeason(){
		seasonPA += gamePA;
		seasonWalks += gameWalks;
		seasonHits += gameHits;
		seasonDoubles += gameDoubles;
		seasonTriples += gameTriples;
		seasonHomeRuns += gameHomeRuns;
		seasonRuns += gameRuns;
		seasonRBIs += gameRBIs;
	}
	
	
}
