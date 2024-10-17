package telis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
	
	//wie viele Einheiten hat man im Angebot
	static final int SELECTION = 5;
	static final double PRIZE = 8.00;
	/*wieviel Rabatt bei x gekauften einheiten? double[0] ist fuer ein Examplar
	 double[1] fuer zwei, usw.*/
	static final double[] DISCOUNTS = new double[] {0, 0.05, 0.1, 0.2, 0.25};
	
	static final DiscountOptimizer DISCOUNTOPTIMIZER = 
			new DiscountOptimizer(PRIZE, DISCOUNTS, SELECTION);
	
	static final String INPUT_REQUEST_MSG = String.format("""
			Um der Auswahl eine Einheit hinzuzufuegen, bitte eine ganze Zahl 
			zwischen zwischen 1 und %d eingeben.""", SELECTION);
	
	static final String OUTSIDE_LIMIT_MSG = String.format("""
			\nFehler!! Die eingegeben Zahl ist invalide. Valide Zahlen 
			liegen zwischen 1 und %d. Die Eingabe kann im weiteren nicht
			beruecksichtigt werden.\n\n""", SELECTION);
	
	
	/**
	 * 1) Angaben werden Legitimitaet geprueft, falls diese nicht gegeben ist, wird 
	 * 	abgebrochen
	 * 2) die notwendigen Grunddaten werden initialisiert
	 * 
	 * Der Nutzer wird aufgefordert, ganze Zahlen zwischen 1 und der hoechstmoeglichen
	 * 	Auswahlmoeglichkeit einzugeben. Dies kann er so lange machen, bis er sein 
	 * 	gewuenschtes Auswahlset vollstaendig hat. 
	 * 
	 * Falls er eine ungueltige Zahl oder
	 * 	etwas anderes als eine Zahl eingibt, wird er darauf hingewiesen und die 
	 * 	Eingabe wird ignoriert
	 * 
	 * Falls der Nutzer eine leere Eingabe macht.....
	 * .... wenn mindestens ein Eintrag zur Auswahl bereitgestellt wurde, beginnt das
	 * 	Programm mit der Ermittlung der guenstigen Erwerbskombination. Anschliesend 
	 * 	beginnt die Eintragseingabe von neuem. 
	 * .... wenn kein Eintrag zur Verfuegung gestellt wurde, wird das Programm 
	 * 	beeendet
	 */
	public static void main(String[] args) {
		
		try(Scanner scanner = new Scanner(System.in)){
			
			Map<String, Integer> unitAvailability = new HashMap<>();
	    
			while(true) {
				
				System.out.println(INPUT_REQUEST_MSG);
				
				if(unitAvailability.isEmpty()) {
					
					System.out.println("""
						Um das Programm zu beenden, bitte ohne weitere Eingabe die 
						Enter-Taste druecken!""");
				}else {
					
					System.out.println("""
						Um mit der Berechnung zu beginnen, bitte ohne weitere Eingabe 
						die Enter-Taste druecken!""");
				}
				
				String input = scanner.nextLine();
	    	
				if(isEnter(input) && unitAvailability.isEmpty()) {
	    		
					break;
				}else if(isEnter(input) && !unitAvailability.isEmpty()) {
	    		
					System.out.println("""
						Erworben werden sollen Exemplare mit den folgenden 
						Verfuegbarkeiten: """+ unitAvailability);
	    	    
					@SuppressWarnings("static-access")
					List<Set<String>> result = 
							DISCOUNTOPTIMIZER.purchaseAllUnits(unitAvailability);
	    	    
					System.out.println(resultPresentation(result));
					
					System.out.println("""
							-------------------------
							Ab hier beginnt eine neue Evaluation
							-------------------------
							""");
					unitAvailability = new HashMap<>();
				}else if(!input.matches("\\d+")) {
	    		
					System.out.println("""
						\nFehler!! Der Input war keine ganze Zahl. Die Eingabe kann 
						im weiteren nicht beruecksichtigt werden.\n""");
				}else if(Integer.valueOf(input)<1 
						|| Integer.valueOf(input) > SELECTION) {
					
					System.out.println(OUTSIDE_LIMIT_MSG);
				}else {
	    		
					unitAvailability.put(input, 
							unitAvailability.getOrDefault(input, 0) + 1);
	    		
					System.out.printf("""
							\n%s wurde der Auswahl hinzugefuegt. Aktuelle Auswahl: """
							+unitAvailability+"\n\n", input);
				}
			}
	    }catch(Exception e){
	    	
	    	e.printStackTrace();
	    }finally {
	    	
	    	System.out.println("\nDas Programm wurde beendet!\n");
	    }
	}
	
	
	/**
	 * war die EIngabe ein reines "enter, d.h. Enter ohne weiteren Input?
	 */
	public static boolean isEnter(String fInput) {
		
		return "".equals(fInput);
	}
	
	
	/**
	 * Ein Ergebnis wird in Textform beschrieben (fuer die cmd-Ausgabe).
	 */
	@SuppressWarnings("static-access")
	private static String resultPresentation(List<Set<String>> fResult) {
		
	    StringBuilder sb = new StringBuilder();
	    
	    sb.append("""
	    	\nUm alle Exemplare moeglichst kostenguenstig zu erwerben, sollten sie 
	    	in folgenden Kombinationen gekauft werden:\n""");
	    
	    fResult.forEach(entry -> 
	    	sb.append("Kombination ").append(entry)
	    		.append(String.format(" fuer den Preis von %.2f %n", 
	    				DISCOUNTOPTIMIZER.getPrizeForUnitNumber(entry.size())
	    		.doubleValue()))
	    );
	    
	    sb.append("Der Gesamtpreis betraegt ")
	    	.append(String.format("%.2f", 
	    			DISCOUNTOPTIMIZER.calculateTotalSum(fResult).doubleValue()))
	    	.append("\n");
	    
	    return sb.toString();
	}
}
