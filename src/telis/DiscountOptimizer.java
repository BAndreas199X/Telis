package telis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static java.util.Comparator.naturalOrder;

public class DiscountOptimizer {
	
	/*wieviel Rabatt bei x gekauften einheiten? double[0] ist fuer ein Examplar
	 double[1] fuer zwei, usw.*/
	final BigDecimal BOOK_PRIZE;
	final double[] DISCOUNTS;
	//wie viele Einheiten hat man im Angebot
	final int SELECTION;

	static final Map<Integer, BigDecimal> prizeMap = new HashMap<>();
	
	//Erwerbsanzahlen, sortiert nach Lukrativitaet
	static List<Integer> lucrRanking = new ArrayList<>();
	
	
	/**
	 * Konstruktor, 
	 * 
	 * Variablen werden initialisiert, BUCKPREIS, RABATTE und SELEKTION 
	 * 	(anzahl zur Auswahl)
	 * 
	 * 1) Angaben werden Legitimitaet geprueft, falls diese nicht gegeben ist, wird 
	 * 	abgebrochen
	 * 2) die notwendigen Grunddaten werden initialisiert
	 */
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
	public static BigDecimal calculateTotalSum(List<Set<Integer>> result) {
		
		BigDecimal finalSum = BigDecimal.ZERO;
		
		for (Set<Integer> currSet: result) {
			
			finalSum = finalSum.add(prizeMap.get(currSet.size()));
		}
		
		return finalSum;
	}
	
	
	/**
	 * Es wird ermittelt mit welchen Erwerbskombinationen sich der Kaufpreis minimieren
	 * laesst. Die Grundauswahl ist in 'availUnits' hinterlegt
	 * 
	 * Der folgende Prozess wird solange durchgefuehrt, bis kein Eintrag mehr erworben 
	 * 	werden kann / bookAvail(Copy) leer ist
	 * 
	 * Um zu garantisieren, dass keine Set-groesse mit einer Nummer von Buchern
	 * 	die garnicht (mehr) zur Auswahl stehen, wird das Lukrativitaetsranking
	 *  regelmaessig modifiziert und der Liste einmal alle nulleintraege entfernt
	 */
	public static List<Set<Integer>> purchaseAllUnits(Map<Integer, Integer> availUnits){
		
		if (availUnits == null) {
			return new ArrayList<>();
		}
		
		List<Set<Integer>> resultSet = new ArrayList<>();
		List<Integer> currentRanking = new ArrayList<>(lucrRanking);
		
		Map<Integer, Integer> availUnitsCopy = new HashMap<>(availUnits);
		availUnitsCopy.entrySet().removeIf(entry -> entry.getValue() == 0);
		
		while (!availUnitsCopy.isEmpty()) {
			
			currentRanking = currentRanking.stream()
				.filter(ent -> ent <= availUnitsCopy.entrySet().size()).toList();
			
			resultSet.add(
				newPurchase(calcUnitNumber(availUnitsCopy, currentRanking), 
				availUnitsCopy));
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
	private static Set<Integer> newPurchase(int setLimit, 
			Map<Integer, Integer> availUnits){
		
		Set<Integer> newPurchaseSet = new HashSet<>();
		
		while (newPurchaseSet.size() != setLimit) {
			
			int nextUnit = nextUnitToPurchase(newPurchaseSet, availUnits);
			newPurchaseSet.add(nextUnit);
			availUnits.put(nextUnit, availUnits.get(nextUnit) - 1);
			
			if (availUnits.get(nextUnit) == 0) {
				
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
	private static int nextUnitToPurchase(Set<Integer> purchasedSet, 
			Map<Integer, Integer> availUnits) {
		
		Set<Entry<Integer, Integer>> currentSet = 
			new HashSet<>(availUnits.entrySet());
			
		currentSet = currentSet.stream()
			.filter(entry -> !purchasedSet.contains(entry.getKey()))
			.collect(Collectors.toSet());
		
		return Collections.max(currentSet, 
			Map.Entry.comparingByValue()).getKey();
	}
	
	
	/**
	 * Wieviele Einheiten sollen mit dem naechsten Set (dem naechsten Kauf) 
	 * 	erworben werden? Es ist das Maxiumum aus.....
	 * 
	 * A) Wieviele Eintraege haben noch den maximalen Restbestand?
	 * B) Welcher Eintrag, unter beruecksichtigung wieviele distinkte Eintraege noch 
	 * 	insgesamt zur Auswahl stehen, fuehrt im Lukrativitaetsranking?
	 */
	private static int calcUnitNumber(Map<Integer, Integer> availUnits, 
			List<Integer> currentRanking) {
		
		Optional<Integer> maxRemain =
				availUnits.values().stream().max(naturalOrder());
		
		List<Integer> numbers = new ArrayList<>(availUnits.values());
		int frequency = Collections.frequency(numbers, maxRemain.get());
		
		return Math.max(frequency, currentRanking.get(0));
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
	private void initalization() {
		
		Map<Integer, BigDecimal> valueIncreases = new HashMap<>();
		
		IntStream.range(1, DISCOUNTS.length + 1).forEach(x -> {
			
			prizeMap.put(x, calculatePriceWithDiscount(x));
			
			valueIncreases.put(x, calculateValueIncrease(x));
		});
		
		List<Entry<Integer, BigDecimal>> list 
			= new ArrayList<>(valueIncreases.entrySet());
		list.sort(Entry.comparingByValue());
		
		lucrRanking = list.stream().map(Entry::getKey).toList();
	}
	
	
	/**Berechnet die Kosten fuer eine Anzahl an EInheiten (nmbr) mit Rabatten 
	 * einberechnet
	 */
	private BigDecimal calculatePriceWithDiscount(int nmbr) {
		
		return BigDecimal.valueOf(BOOK_PRIZE.intValue() * nmbr * 
				(1 - DISCOUNTS[nmbr - 1]));
	}
	
	
	/**
	 * Berechnet die Preiserhoehung fuer wenn man eine Anzahl (nmbr) an Einheiten 
	 * 	verglichen mit wenn man eine EInheit weniger kauft
	 */
	private BigDecimal calculateValueIncrease(int nmbr) {
		
		return prizeMap.get(nmbr).subtract(prizeMap.getOrDefault(nmbr-1, 
				BigDecimal.ZERO));
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
	private void validityCheck() {
		
		if (this.SELECTION != this.DISCOUNTS.length) {
			
			throw new IllegalArgumentException("""
				Die Laenge des Arrays 'Discounts' ist nicht identisch der 
				Selektion!""");
		} else if (discountsDontIncrease()) {
			
			throw new IllegalArgumentException("""
					Mit diesem Programm soll ein Problem gelöst werden, bei dem der 
					Rabatt mit der Anzahl der gekauften Einheiten steigt. Die 
					angegebenen Rabatte erhöhen sich nicht mit der Anzahl der 
					gekauften Einheiten.""");
		}
	}
	
	
	private boolean discountsDontIncrease() {
		
		return IntStream.range(1, this.DISCOUNTS.length)
				.anyMatch( x -> this.DISCOUNTS[x - 1]>this.DISCOUNTS[x]);
	}
	
	
	public BigDecimal getPrizeForUnitNumber(int numberUnits) {
		
		return prizeMap.get(numberUnits);
	}
	

	public int getSelection() {
		return SELECTION;
	}


	public BigDecimal getBookPrize() {
		return BOOK_PRIZE;
	}


	public double[] getDiscounts() {
		return DISCOUNTS;
	}
}
