package telis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DiscountOptimizer {
	
	final BigDecimal BOOK_PRIZE;
	final double[] DISCOUNTS;
	final int SELECTION;

	static final Map<String, BigDecimal> prizeMap = new HashMap<>();
	
	//Erwerbsanzahlen, sortiert nach Lukrativitaet
	static List<String> lucrRanking = new ArrayList<>();
	
	public DiscountOptimizer(double prize, double[] discounts, int selection) {
		
		this.BOOK_PRIZE = new BigDecimal(prize);
		this.DISCOUNTS = discounts;
		this.SELECTION = selection;
		validityCheck();
		initalization();
	}

	
	/**
	 * Die Gesamtkosten fuer den Erwerb verfuegbarer Einheiten wird berechnet
	 * 
	 * Dazu wird fuer jede erworbenes Set geschaut wie viele Einheiten gekauft wurden
	 * und anschliesende, wieviel man fuer ein Set mit dieser Anzahl von Einheiten 
	 * 	zahlt
	 * 
	 * Diese Werte werden addiert und ergeben die Gesamtkosten
	 * 
	 */
	public static BigDecimal calculateTotalSum(List<Set<String>> fPurchasedCombos) {
		
		BigDecimal finalSum = new BigDecimal("0.00");
		
		for(Set<String> currSet: fPurchasedCombos) {
			
			finalSum = finalSum.add(prizeMap.get(String.valueOf(currSet.size())));
		}
		
		return finalSum;
	}
	
	
	/**
	 * Es wird ermittelt mit welchen Erwerbskombinationen sich der Kaufpreis minimieren
	 * laesst. Die Grundauswahl ist in 'availUnits' hinterlegt
	 * 
	 * Der folgende Prozess wird solange durchgefuehrt, bis kein Eintrag mehr erworben 
	 * 	werden kann / bookAvail leer ist
	 */
	public static List<Set<String>> purchaseAllUnits(Map<String, Integer> availUnits){
		
		if(availUnits == null) {
			return new ArrayList<>();
		}
		
		List<Set<String>> resultSet = new ArrayList<>();
		List<String> currentRanking = new ArrayList<>(lucrRanking);
		
		while(!availUnits.isEmpty()) {
			
			currentRanking = currentRanking.stream()
				.filter(ent -> Integer.valueOf(ent) <= availUnits.entrySet().size())
				.toList();
			
			resultSet.add(
				newPurchase(calcUnitNumber(availUnits, currentRanking), availUnits)
			);
		}
		
		return resultSet;
	}
	
	
	/**
	 * Die Einheiten welche mit diesem Set/diesem Einkauf erworben werden sollen 
	 * 	werden hier festgelegt. Ein Set wird kreiert und ihm wird solange ein neues 
	 * 	Exemplar hinzugefuegt, bis das gegebene Setlimit erreicht wurde
	 * 
	 * 1) Fuer einen neuen Erwerb wird ermittelt, wieviele Distinkte Eintraege mit ihm 
	 * 	erworben werden sollen
	 * 2) diesem neuen Erwerbsset werden solange Tital hinzugefueht, bis die Anzahl
	 * 	aus 1) erreicht wurde. Die hinzugefuegten Tital werden 'availUnits' abgezogen.
	 * 3) dieses Erwerbsset wird 'newPurchaseSet' hinzugefuegt
	 * 4) Das Lukrativitaetsranking wird kontinuierlich angepasst, damit die Zahlen im 
	 * 	Ranking den Restbestand an distinkten Eintraegen nie ueberschreiten
	 * 
	 * Fuer Details zu Schritten 1 und 2 seht bitte die Beschreibungen unter 
	 * 	nextUnitToPurchase und calcUnitNumber
	 * 
	 * Parameter: setLimit: wie VIELE Einheiten werden mit diesem Set/Einkauf erworben
	 * 			  availUnits: welche Einheiten stehen zur Auswahl?
	 * 
	 * Zurueckgegebn wird 'newPurchaseSet': Es ist ein Set welches darlegt welche 
	 * 	Einheiten mit einem Einkauf erworben werden
	 */
	private static Set<String> newPurchase(int setLimit, 
			Map<String, Integer> availUnits){
		
		Set<String> newPurchaseSet = new HashSet<>();
		
		while(newPurchaseSet.size() != setLimit) {
			
			String nextUnit = nextUnitToPurchase(newPurchaseSet, availUnits);
			newPurchaseSet.add(nextUnit);
			availUnits.put(nextUnit, availUnits.get(nextUnit)-1);
			
			if(availUnits.get(nextUnit)==0) {
				
				availUnits.remove(nextUnit);
			}
		}
		
		return newPurchaseSet;
	}
	
	
	/**
	 * Hier wird entschieden, welcher Tital als naechstes erworben werden soll.
	 * Hier erfolgt nur die Entscheidung, der Erwerb/das hinzufuegen zum Set
	 * erfolgt in der Klasse welche diese Methode aufruft
	 * 
	 * Es ist der Eintrag, der noch nicht dem aktuellen Erwerbsset hinzugefuegt wurde,
	 * 	und vom dem noch am meisten Bestand vorhanden ist,
	 * 	Bei Gleichstand entscheidet der Zufall
	 */
	private static String nextUnitToPurchase(Set<String> purchasedSet, 
			Map<String, Integer> availUnits) {
		
		Set<Entry<String, Integer>> currentSet = 
			new HashSet<>(availUnits.entrySet());
			
		currentSet = currentSet.stream()
			.filter(entry -> ! purchasedSet.contains(entry.getKey()))
			.collect(Collectors.toSet());
		
		return Collections.max(currentSet, 
			Map.Entry.comparingByValue()).getKey();
	}
	
	
	/**
	 * Wieviele Einheiten sollen mit dem naechsten Set(dem naechsten Kauft 
	 * 	erworben werden? Es ist das Maxiumum aus.....
	 * 
	 * A) Wieviele Eintraege haben noch den maximalen Restbestand?
	 * B) Welcher Eintrag, unter beruecksichtigung wieviele distinkte Eintraege noch 
	 * 	insgesamt zur Auswahl stehen, fuehrt im Lukrativitaetsranking?
	 */
	private static int calcUnitNumber(Map<String, Integer> availUnits, 
			List<String> currentRanking) {
		
		Optional<Integer> maxRemain =
				availUnits.values().stream().max(Comparator.comparing(x -> x));
		
		List<Integer> numbers = new ArrayList<>(availUnits.values());
		int frequency = Collections.frequency(numbers, maxRemain.get());
		
		return Math.max(frequency, Integer.valueOf(currentRanking.get(0)));
	}
	
	
	/**
	 * Fuer jede moegliche Anzahl an distinkten Buechern/Einheiten in einem Erwerbsset 
	 * wird ermittelt:
	 * A) "Wieviel Kosten ein Einkauf mit x Einheiten?" Speichere die Werte, um sie 
	 * 	spaeter schneller abrufen zu koennen
	 * B) Der Mehrpreis fuer jede Kombinationsanzahl, d.h. wieviel teurer ist ein 
	 * 	Kauf mit x Einheiten verglichen mit einem Kauf mit einer Einheit weniger
	 * c) Sortiere diese Anzahlen/Mehrpreis Kombinationen vom niedrigsten zum hoechsten 
	 * 	Mehrpreis. Je niedriger der Mehrpreis, desdo lukrativer ist diese 
	 * 	Kombinationsanzahl. Speichere dieses "Ranking" in der Liste 'lucrRanking'
	 * 
	 * Dieses Ranking der Mehrpreise vom niedrigsten zum hoechsten wird im 
	 * 	verbleibenden Programm als 'Lukrativitaet' bezeichnet
	 */
	public void initalization() {
		
		Map<String, BigDecimal> valueIncreases = new HashMap<>();
		
		IntStream.range(1, DISCOUNTS.length+1).forEach(x -> {
			
			prizeMap.put(String.valueOf(x), 
					BigDecimal.valueOf(BOOK_PRIZE.intValue()*x*(1-DISCOUNTS[x-1])));
			
			valueIncreases.put(String.valueOf(x), prizeMap.get(String.valueOf(x))
					.subtract(prizeMap.getOrDefault(String.valueOf(x-1), 
					new BigDecimal("0.00"))));
		});
		
		List<Entry<String, BigDecimal>> list 
			= new ArrayList<>(valueIncreases.entrySet());
		list.sort(Entry.comparingByValue());
		
		lucrRanking = list.stream().map(Entry::getKey).toList();
	}
	
	
	/**
	 * 1) fuer jede Anzahl an distinkten erwerbbaren Einheiten muss ein Discount 
	 * 	gegeben sein, d.h. die Laenge des DiscountArrays muss gleich der Anzahl der
	 * 	erwerbbaren Einheiten sein
	 * 2) Der Rabatt muss mit zunehmender Anzahl an erworbenen Einheiten steigen, bzw. 
	 * 	darf nicht sinken, da sonst mein Algorithmus nicht funktioniert
	 * 
	 * Falls eines dieser Dinge nicht zutrifft, wird das Programm nicht starten und 
	 * 	eine Exception schmeissen 
	 */
	public void validityCheck() {
		
		if(this.SELECTION != this.DISCOUNTS.length) {
			
			throw new IllegalArgumentException("""
				Die Laenge des Arrays 'Discounts' ist nicht identisch der 
				Selektion!""");
		}else if(IntStream.range(1, this.DISCOUNTS.length)
				.anyMatch( x-> this.DISCOUNTS[x-1]>this.DISCOUNTS[x])) {
			
			throw new IllegalArgumentException("""
					Mit diesem Programm soll ein Problem gelöst werden, bei dem der 
					Rabatt mit der Anzahl der gekauften Einheiten steigt. Die 
					angegebenen Rabatte erhöhen sich nicht mit der Anzahl der 
					gekauften Einheiten.""");
		}
	}
	
	public BigDecimal getPrizeForUnitNumber(int numberUnits) {
		
		return prizeMap.get(String.valueOf(numberUnits));
	}
}
