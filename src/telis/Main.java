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
	
	static final String INPUT_REQUEST_MSG = """
			Bitte eingeben (eine ganze Zahl groesser oder gleich '0') wieviele 
			Exemplare von Einheit %d erworben werden sollen.%n""";
	
	static final String OUTSIDE_LIMIT_MSG = String.format("""
			%nFehler!! Die eingegeben Zahl ist invalide. Valide Zahlen 
			liegen zwischen 1 und %d. Die Eingabe kann im weiteren nicht
			beruecksichtigt werden.%n%n""", SELECTION);
	
	
	/**
	 * Der Nutzer wird aufgefordert, fuer jeden Verfuegbaren Titel einzugeben, wie 
	 * 	viele Exemplare davon erworben werden sollen 
	 * 
	 * Falls mit der Eingabe alles stimmt, wird sie einer Auswahl-Map hinzugefuegt.
	 * 
	 * Falls der Nutzer eine leere Eingabe macht.....
	 * .... wenn bereit ein Eintrag zur Auswahl bereitgestellt wurde, kann der Nutzer
	 * 	keine Lehreingabe machen. Er wird solange zur EIngabe aufgefordert, bis er 
	 * 	eine (legitime) EIngabe macht) 
	 * .... wenn kein Eintrag zur Verfuegung gestellt wurde, wird das Programm 
	 * 	beeendet
	 */
	public static void main(String[] args) {
		
		try(Scanner scanner = new Scanner(System.in)){
			
			activeProgram:
			while(true) {
			
				int currUnit = 1;
				Map<Integer, Integer> unitAvailability = new HashMap<>();
			
				while(currUnit <= SELECTION) {
				
					System.out.printf(INPUT_REQUEST_MSG, currUnit);
				
					if(unitAvailability.isEmpty()) {
					
						System.out.println("""
						Um das Programm zu beenden, bitte ohne weitere Eingabe die 'Enter'
						-Taste druecken!""");
					}
				
					String input = scanner.nextLine();
				
					if(isEnter(input) && unitAvailability.isEmpty()) {
		    		
						break activeProgram;
					}else if(isEnter(input) && !unitAvailability.isEmpty()) {
					
						System.out.println("""
							Fehler!!! Die Eingabe kann an dieser Stelle nicht leer 
							sein. Bitte die aktuelle Evaluation fertig stellen!!
							""");
					}else if(isInputValid(input)) {
						
						unitAvailability.put(currUnit, Integer.valueOf(input));
						
						System.out.printf("""
							%nFuer Einheit %d wurden %s Exemplare der Auswahl hinzugefuegt. 
								Aktuelle Auswahl: """+unitAvailability+"%n%n", 
								currUnit, input);
						currUnit++;
					}
				}
			
				System.out.println("""
					Erworben werden sollen Exemplare mit den folgenden 
					Verfuegbarkeiten: """+ unitAvailability);
    	    
				@SuppressWarnings("static-access")
				List<Set<Integer>> result = 
						DISCOUNTOPTIMIZER.purchaseAllUnits(unitAvailability);
    	    
				System.out.println(resultPresentation(result));
				
				System.out.println("""
						-------------------------
						Ab hier beginnt eine neue Evaluation
						-------------------------
						""");
				unitAvailability = new HashMap<>();
			}
	    }catch(Exception e){
	    	
	    	e.printStackTrace();
	    }finally {
	    	
	    	System.out.println("\nDas Programm wurde beendet!\n");
	    }
	}
	
	
	/**
	 * garantiert, dass eine Zahl eingeben wurde, dass sie ganz ist und nicht negativ
	 */
	public static boolean isInputValid(String input) {
		
		if(!input.matches("\\d+")) {
    		
			System.out.println("""
				\nFehler!! Der Input war keine ganze Zahl. Die Eingabe kann 
				im weiteren nicht beruecksichtigt werden.\n""");
			
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * war die Eingabe ein reines "enter, d.h. Enter ohne weiteren Input?
	 */
	public static boolean isEnter(String fInput) {
		
		return "".equals(fInput);
	}
	
	
	/**
	 * Ein Ergebnis wird in Textform beschrieben (fuer die cmd-Ausgabe).
	 */
	@SuppressWarnings("static-access")
	private static String resultPresentation(List<Set<Integer>> result) {
		
	    StringBuilder sb = new StringBuilder();
	    
	    sb.append("""
	    	\nUm alle Exemplare moeglichst kostenguenstig zu erwerben, sollten sie 
	    	in folgenden Kombinationen gekauft werden:\n""");
	    
	    result.stream().distinct().forEach(entry -> {
	    	long count = result.stream().filter(elmnt -> elmnt.equals(entry)).count();
	    	
	    	sb.append(String.format("%d Mal Kombination ", count)).append(entry)
	    	.append(String.format(" fuer den Preis von %.2f %n", 
	    	DISCOUNTOPTIMIZER.getPrizeForUnitNumber(entry.size()).doubleValue()));
	    });
	    
	    sb.append("Der Gesamtpreis betraegt ")
	    	.append(String.format("%.2f", 
	    			DISCOUNTOPTIMIZER.calculateTotalSum(result).doubleValue()))
	    	.append("\n");
	    
	    return sb.toString();
	}
}
