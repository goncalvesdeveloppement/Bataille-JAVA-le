import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatailleJavale {
	public static int[] yourBoard = new int[100], opponentBoard = new int[100];
	public static int[] boatsLengths = { 2, 3, 3, 4, 5 };
	public static int[] yourBoats = { 2, 3, 3, 4, 5 }, opponentBoats = { 2, 3, 3, 4, 5 };
	public static boolean gameOver = false;

	public static Scanner scan = new Scanner(System.in);
	public static ArrayList<Integer> nextTargets = new ArrayList<Integer>();
	public static ArrayList<Integer> lastTouchTarget = new ArrayList<Integer>();

	public static void main(String[] args) {

		for (int i = 0; i < yourBoard.length; i++) {
			yourBoard[i] = 0;
			opponentBoard[i] = 0;
		}

		PlaceOpponentBoats();
		UpdateGrids();
		PlaceYourBoats();

		while (!gameOver) {
			gameOver = true;

			for (int i = 0; i < 5; i++) {
				if (opponentBoats[i] > 0)
					gameOver = false;
			}

			if (gameOver)
				System.out.println("\n🏆 BRAVO, VOUS AVEZ GAGNÉ !");
			else {
				gameOver = true;

				for (int i = 0; i < 5; i++) {
					if (yourBoats[i] > 0)
						gameOver = false;
				}

				if (gameOver)
					System.out.println("\n☠ VOUS AVEZ PERDU, DOMMAGE !");
				else {
					YourTurn();
					OpponentTurn();
				}
			}
		}

		scan.close();
	}

	// Dessine les grilles de jeu à jour
	public static void UpdateGrids() {
		// Effacer la console
		ClearConsole();

		// Dessiner les grilles
		System.out.println("              Adversaire :                  Vous :        ");
		System.out.println("         ---------------------       ---------------------       ");

		for (int y = 0; y < 10; y++) {
			System.out.print("       " + (char) (65 + y) + "| ");
			for (int x = 0; x < 10; x++) {
				switch (opponentBoard[y * 10 + x]) {
				case 0:
					System.out.print("  ");
					break;
				case -1:
					System.out.print("○ ");
					break;
				case -2:
					System.out.print("× ");
					break;
				default:
					System.out.print("  ");
					break;
				}
			}

			System.out.print("|    " + (char) (65 + y) + "| ");

			for (int x = 0; x < 10; x++) {

				switch (yourBoard[y * 10 + x]) {
				case 0:
					System.out.print("  ");
					break;
				case -1:
					System.out.print("○ ");
					break;
				case -2:
					System.out.print("× ");
					break;
				default:
					System.out.print("■ ");
					break;
				}
			}

			System.out.println("|");
		}

		System.out.println("         ---------------------       ---------------------       ");
		System.out.println("          1 2 3 4 5 6 7 8 9 10        1 2 3 4 5 6 7 8 9 10       ");
	}

	// Place aléatoirement les bateaux de l'adversaire
	public static void PlaceOpponentBoats() {
		boolean ok = true;
		int target, angle, coef = 0, target_ = -1;

		// Pour chaque bateau
		for (int i = 0; i < boatsLengths.length; i++) {
			do {
				target = (int) Math.floor(Math.random() * 100);
				angle = (int) Math.floor(Math.random() * 4);

				target_ = target;

				ok = true;

				switch (angle) {
				case 0:
					coef = 1;
					break;
				case 1:
					coef = 10;
					break;
				case 2:
					coef = -1;
					break;
				case 3:
					coef = -10;
					break;
				}

				for (int j = 0; j < boatsLengths[i]; j++) {
					if (target_ < 0 || target_ > 99 || target_ % 10 == 0)
						ok = false;
					else if (opponentBoard[target_] != 0)
						ok = false;

					target_ += coef;
				}

			} while (!ok); // Choisir des coordonnées valables et non utilisées pour placer le bateau
							// actuel

			for (int j = 0; j < boatsLengths[i]; j++) {
				opponentBoard[target] = i + 1;
				target += coef;
			}
		}
	}

	// Permet au joueur de placer ses bateaux
	public static void PlaceYourBoats() {
		boolean ok = true;
		int target = -1, angle = 0, coef = 0, target_ = -1;
		boolean[] possibleAngles = { true, true, true, true };
		ArrayList<Integer> possibleCoords = new ArrayList<Integer>();
		String coord = "A0", coord2 = "A0";

		// Pour chaque bateau
		for (int i = 0; i < boatsLengths.length; i++) {
			do {
				coord = "A0";
				possibleCoords = new ArrayList<Integer>();

				for (int k = 0; k < 4; k++)
					possibleAngles[k] = true;

				target = -1;

				System.out.println("\nEntrez une coordonnée libre (format A1, A2...) pour y placer un bateau de "
						+ boatsLengths[i] + " cases :");

				while (!IsFreeCoord(coord)) {
					coord = ReadCoord(scan);
					target = coordToInt(coord);

					if (!IsFreeCoord(coord)) {
						System.out.println("Veuillez entrez une coordonnée libre (format A1, A2...) :");
					}
				}

				List<String> oris = Arrays.asList("N", "E", "S", "W");

				for (String ori : oris) {
					if (!IsPlaceable(coord, boatsLengths[i], ori))
						possibleAngles[oris.indexOf(ori)] = false;
					else
						possibleCoords.add(coordToInt(coord) + OriToInt(ori) * (boatsLengths[i] - 1));
				}

				if (possibleCoords.size() == 0) {
					ok = false;
					System.out.println("Impossible de placer le bateau dans cette zone. Recommencez.");
				}
			} while (!ok);

			System.out.print("Placer le bateau jusqu'en (entrer la coordonnée) :  ");

			for (int j = 0; j < possibleCoords.size(); j++) {
				System.out.print(intToCoord(possibleCoords.get(j)) + (j < possibleCoords.size() - 1 ? ", " : ""));
			}

			System.out.print(" ? : \n");

			coord2 = "A0";

			while (!IsFreeCoord(coord2) || !possibleCoords.contains(coordToInt(coord2))) {
				coord2 = ReadCoord(scan);
			}

			int posA = coordToInt(coord), posB = coordToInt(coord2), inc = 1;

			if (Math.abs(posB - posA) >= 10)
				inc = 10;

			if (posB < posA)
				inc = -inc;

			yourBoard[posA] = i + 1;
			yourBoard[posB] = i + 1;

			target = posA;

			for (int j = 1; j < boatsLengths[i] - 1; j++) {
				target += inc;
				yourBoard[target] = i + 1;
			}

			UpdateGrids();
		}
	}

	// Convertit un entier de 0 à 99 en sa coordonnée (A1, A2...)
	private static String intToCoord(int index) {
		return ((char) (65 + Math.floor(index / 10))) + Integer.toString(index % 10 + 1);
	}

	// Convertit une coordonnée en entier de 0 à 99
	private static int coordToInt(String coord) {

		if ((coord.length() == 3 && !coord.substring(1).equals("10")) || coord.length() < 2 || coord.charAt(0) > 74
				|| coord.charAt(0) < 65 || coord.charAt(1) < 48 || coord.charAt(1) > 57)
			return -1;
		else if (coord.length() == 3 && coord.substring(1).equals("10"))
			return (coord.charAt(0) - 65) * 10 + 9;
		else
			return (coord.charAt(0) - 65) * 10 + (coord.charAt(1) - 49);
	}

	// Simule un effacement de console
	private static void ClearConsole() {
		for (int i = 0; i < 4; ++i)
			System.out.println();
	}

	// Lire une coordonnée
	private static String ReadCoord(Scanner scan) {
		boolean ok = false;

		while (!ok) {
			scan = new Scanner(System.in);

			if (scan.hasNextLine()) {
				String coord = scan.nextLine();

				if (coordToInt(coord) > -1) {
					return coord;
				} else {
					return "";
				}
			}
		}

		return "";
	}

	// Renvoie si une coordonnée est libre (pour placement uniquement)
	private static boolean IsFreeCoord(String coord) {
		if (coordToInt(coord) != -1 && coordToInt(coord) >= 0 && coordToInt(coord) < 100) {
			return (yourBoard[coordToInt(coord)] == 0);
		} else
			return false;
	}

	// Renvoie si un bateau est plaçable selon sa coordonnée de départ, sa taille et
	// son orientation
	private static boolean IsPlaceable(String coord, int size, String orientation) {
		if (IsFreeCoord(coord)) {
			int incr = OriToInt(orientation);
			int coordConverted = coordToInt(coord), finalCoord = coordConverted + incr * (size - 1);

			if (orientation == "W")

				if ((MathFloorNeg(finalCoord / 10) != MathFloorNeg(coordConverted / 10)
						&& finalCoord % 10 != coordConverted % 10)) {
					return false;
				}

			if ((orientation == "W" && coordConverted % 10 < finalCoord % 10)
					|| (orientation == "E" && coordConverted % 10 > finalCoord % 10))
				return false;

			if (!IsFreeCoord(intToCoord(finalCoord))) {
				return false;
			}

			for (int i = 1; i < size; i++) {
				if (!IsFreeCoord(intToCoord(coordToInt(coord) + i * incr)))
					return false;
			}

			return true;
		} else
			return false;
	}

	// Convertit une orientation NESW en incrémental
	private static int OriToInt(String ori) {
		switch (ori) {
		case "N":
			return -10;
		case "E":
			return 1;
		case "S":
			return 10;
		case "W":
			return -1;
		default:
			return 0;
		}
	}

	// Math.floor en tenant compte du négatif
	private static int MathFloorNeg(double number) {
		return number >= 0 ? (int) Math.floor(number) : (int) -Math.floor(-number);
	}

	// Joue le tour de l'adversaire
	private static void OpponentTurn() {
		int target = OpponentChoice();

		if (yourBoard[target] > 0) {
			yourBoats[yourBoard[target] - 1]--;

			if (yourBoats[yourBoard[target] - 1] == 0)
				System.out.println("❌ ALERTE ! Coulé en " + intToCoord(target) + " !!!");
			else
				System.out.println("⚠ Attention : Touché en " + intToCoord(target) + " !");

			yourBoard[target] = -2;
		} else {
			yourBoard[target] = -1;
			System.out.println("💦 Votre adversaire a fait plouf en " + intToCoord(target) + "...");
		}

		UpdateGrids();
	}

	// Fait choisir une case intelligemment à l'adversaire
	private static int OpponentChoice() {
		int newChoice = -1;

		if (nextTargets.size() > 0) {
			do {
				newChoice = nextTargets.get(nextTargets.size() - 1);
				nextTargets.remove((Object) newChoice);
			} while (yourBoard[newChoice] < 0);
		} else {
			do {
				newChoice = (int) Math.floor(Math.random() * 100);
			} while (yourBoard[newChoice] < 0);
		}

		if (yourBoard[newChoice] > 0) {
			ArrayList<Integer> targetsAround = new ArrayList<Integer>();
			targetsAround.add(newChoice + 1);
			targetsAround.add(newChoice + 10);
			targetsAround.add(newChoice - 1);
			targetsAround.add(newChoice - 10);

			for (int targetAround : targetsAround) {
				if (targetAround >= 0 && targetAround < 100 && yourBoard[targetAround] >= 0)
					nextTargets.add(targetAround);
			}

			if (nextTargets.size() == 0) {
				do {
					newChoice = (int) Math.floor(Math.random() * 100);
				} while (yourBoard[newChoice] < 0);
			}
		}

		return newChoice;
	}

	// Joue le tour du joueur
	private static void YourTurn() {
		String coord = "A0";

		System.out.println("\n➡ À vous ! Entrez une coordonnée à cibler : ");
		while (coordToInt(coord) == -1 || opponentBoard[coordToInt(coord)] < 0) {
			coord = ReadCoord(scan);
		}

		int target = coordToInt(coord);

		if (opponentBoard[target] > 0) {
			opponentBoats[opponentBoard[target] - 1]--;
			System.out.println("Santé bateau touché : " + opponentBoats[opponentBoard[target] - 1]);

			if (opponentBoats[opponentBoard[target] - 1] == 0)
				System.out.println("💥 Coulé !!!");
			else
				System.out.println("🎯 Touché !");

			opponentBoard[target] = -2;

		} else {
			opponentBoard[target] = -1;
			System.out.println("💦 Vous avez fait plouf...");
		}
	}
}