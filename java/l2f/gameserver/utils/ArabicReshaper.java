package l2f.gameserver.utils;

public class ArabicReshaper {
	/**
	 * The reshaped Word String
	 */
	private String _returnString = "";

	/**
	 * The Reshaped Word
	 * 
	 * @return reshaped Word
	 */
	public String getReshapedWord() {

		return _returnString;
	}

	public static char DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_MDD = 0x0622;

	public static char DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_HAMAZA = 0x0623;

	public static char DEFINED_CHARACTERS_ORGINAL_ALF_LOWER_HAMAZA = 0x0625;

	public static char DEFINED_CHARACTERS_ORGINAL_ALF = 0x0627;

	public static char DEFINED_CHARACTERS_ORGINAL_LAM = 0x0644;

	public static final char RIGHT_LEFT_CHAR = 0x0001;
	public static final char RIGHT_NOLEFT_CHAR_ALEF = 0x0006;
	public static final char RIGHT_NOLEFT_CHAR = 0x0004;
	public static final char RIGHT_LEFT_CHAR_LAM = 0x0003;
	public static final char TANWEEN = 0x000C;
	public static final char TASHKEEL = 0x000A;
	public static final char TATWEEL_CHAR = 0x0008;
	public static final char NORIGHT_NOLEFT_CHAR = 0x0007;
	public static final char NOTUSED_CHAR = 0x000F;
	public static final char NOTARABIC_CHAR = 0x0000;

	public static final char RIGHT_LEFT_CHAR_MASK = 0x0880;
	public static final char RIGHT_NOLEFT_CHAR_MASK = 0x0800;
	public static final char LEFT_CHAR_MASK = 0x0080;

	public static char[][] LAM_ALEF_GLPHIES = { { 15270, 65270, 65269 }, { 15271, 65272, 65271 },
			{ 1575, 65276, 65275 }, { 1573, 65274, 65273 } };

	public static char[] HARAKATE = { '\u0600', '\u0601', '\u0602', '\u0603', '\u0606', '\u0607', '\u0608', '\u0609',
			'\u060A', '\u060B', '\u060D', '\u060E', '\u0610', '\u0611', '\u0612', '\u0613', '\u0614', '\u0615',
			'\u0616', '\u0617', '\u0618', '\u0619', '\u061A', '\u061B', '\u061E', '\u061F', '\u0621', '\u063B',
			'\u063C', '\u063D', '\u063E', '\u063F', '\u0640', '\u064B', '\u064C', '\u064D', '\u064E', '\u064F',
			'\u0650', '\u0651', '\u0652', '\u0653', '\u0654', '\u0655', '\u0656', '\u0657', '\u0658', '\u0659',
			'\u065A', '\u065B', '\u065C', '\u065D', '\u065E', '\u0660', '\u066A', '\u066B', '\u066C', '\u066F',
			'\u0670', '\u0672', '\u06D4', '\u06D5', '\u06D6', '\u06D7', '\u06D8', '\u06D9', '\u06DA', '\u06DB',
			'\u06DC', '\u06DF', '\u06E0', '\u06E1', '\u06E2', '\u06E3', '\u06E4', '\u06E5', '\u06E6', '\u06E7',
			'\u06E8', '\u06E9', '\u06EA', '\u06EB', '\u06EC', '\u06ED', '\u06EE', '\u06EF', '\u06D6', '\u06D7',
			'\u06D8', '\u06D9', '\u06DA', '\u06DB', '\u06DC', '\u06DD', '\u06DE', '\u06DF', '\u06F0', '\u06FD',
			'\uFE70', '\uFE71', '\uFE72', '\uFE73', '\uFE74', '\uFE75', '\uFE76', '\uFE77', '\uFE78', '\uFE79',
			'\uFE7A', '\uFE7B', '\uFE7C', '\uFE7D', '\uFE7E', '\uFE7F', '\uFC5E', '\uFC5F', '\uFC60', '\uFC61',
			'\uFC62', '\uFC63' };

	public static char[][] ARABIC_GLPHIES = { { '\u0622', '\uFE81', '\uFE81', '\uFE82', '\uFE82', 2 },
			{ '\u0623', '\uFE83', '\uFE83', '\uFE84', '\uFE84', 2 },
			{ '\u0624', '\uFE85', '\uFE85', '\uFE86', '\uFE86', 2 },
			{ '\u0625', '\uFE87', '\uFE87', '\uFE88', '\uFE88', 2 },
			{ '\u0626', '\uFE89', '\uFE8B', '\uFE8C', '\uFE8A', 4 },
			{ '\u0627', '\u0627', '\u0627', '\uFE8E', '\uFE8E', 2 },
			{ '\u0628', '\uFE8F', '\uFE91', '\uFE92', '\uFE90', 4 },
			{ '\u0629', '\uFE93', '\uFE93', '\uFE94', '\uFE94', 2 },
			{ '\u062A', '\uFE95', '\uFE97', '\uFE98', '\uFE96', 4 },
			{ '\u062B', '\uFE99', '\uFE9B', '\uFE9C', '\uFE9A', 4 },
			{ '\u062C', '\uFE9D', '\uFE9F', '\uFEA0', '\uFE9E', 4 },
			{ '\u062D', '\uFEA1', '\uFEA3', '\uFEA4', '\uFEA2', 4 },
			{ '\u062E', '\uFEA5', '\uFEA7', '\uFEA8', '\uFEA6', 4 },
			{ '\u062F', '\uFEA9', '\uFEA9', '\uFEAA', '\uFEAA', 2 },
			{ '\u0630', '\uFEAB', '\uFEAB', '\uFEAC', '\uFEAC', 2 },
			{ '\u0631', '\uFEAD', '\uFEAD', '\uFEAE', '\uFEAE', 2 },
			{ '\u0632', '\uFEAF', '\uFEAF', '\uFEB0', '\uFEB0', 2 },
			{ '\u0633', '\uFEB1', '\uFEB3', '\uFEB4', '\uFEB2', 4 },
			{ '\u0634', '\uFEB5', '\uFEB7', '\uFEB8', '\uFEB6', 4 },
			{ '\u0635', '\uFEB9', '\uFEBB', '\uFEBC', '\uFEBA', 4 },
			{ '\u0636', '\uFEBD', '\uFEBF', '\uFEC0', '\uFEBE', 4 },
			{ '\u0637', '\uFEC1', '\uFEC3', '\uFEC4', '\uFEC2', 4 },
			{ '\u0638', '\uFEC5', '\uFEC7', '\uFEC8', '\uFEC6', 4 },
			{ '\u0639', '\uFEC9', '\uFECB', '\uFECC', '\uFECA', 4 },
			{ '\u063A', '\uFECD', '\uFECF', '\uFED0', '\uFECE', 4 },
			{ '\u0641', '\uFED1', '\uFED3', '\uFED4', '\uFED2', 4 },
			{ '\u0642', '\uFED5', '\uFED7', '\uFED8', '\uFED6', 4 },
			{ '\u0643', '\uFED9', '\uFEDB', '\uFEDC', '\uFEDA', 4 },
			{ '\u0644', '\uFEDD', '\uFEDF', '\uFEE0', '\uFEDE', 4 },
			{ '\u0645', '\uFEE1', '\uFEE3', '\uFEE4', '\uFEE2', 4 },
			{ '\u0646', '\uFEE5', '\uFEE7', '\uFEE8', '\uFEE6', 4 },
			{ '\u0647', '\uFEE9', '\uFEEB', '\uFEEC', '\uFEEA', 4 },
			{ '\u0648', '\uFEED', '\uFEED', '\uFEEE', '\uFEEE', 2 },
			{ '\u0649', '\uFEEF', '\uFEEF', '\uFEF0', '\uFEF0', 2 },
			{ '\u0671', '\u0671', '\u0671', '\uFB51', '\uFB51', 2 },
			{ '\u064A', '\uFEF1', '\uFEF3', '\uFEF4', '\uFEF2', 4 },
			{ '\u066E', '\uFBE4', '\uFBE8', '\uFBE9', '\uFBE5', 4 },
			{ '\u0671', '\u0671', '\u0671', '\uFB51', '\uFB51', 2 },
			{ '\u06AA', '\uFB8E', '\uFB90', '\uFB91', '\uFB8F', 4 },
			{ '\u06C1', '\uFBA6', '\uFBA8', '\uFBA9', '\uFBA7', 4 },
			{ '\u06E4', '\u06E4', '\u06E4', '\u06E4', '\uFEEE', 2 } };

	/**
	 * Searching for the letter and Get the right shape for the character depends on
	 * the location specified
	 * 
	 * @param target
	 *            The character that needs to get its form
	 * @param location
	 *            The location of the Form letter
	 * @return The letter with its right shape
	 */
	private char getReshapedGlphy(char target, int location) {
		// Iterate over the 36 characters in the GLPHIES Matrix
		for (int n = 0; n < ARABIC_GLPHIES.length; n++) {
			// Check if the character equals the target character
			if (ARABIC_GLPHIES[n][0] == target) {
				// Get the right shape for the character, depends on the location
				return ARABIC_GLPHIES[n][location];
			}
		}
		// get the same character, If not found in the GLPHIES Matrix
		return target;
	}

	/**
	 * Define which Character Type is This, that has 2,3 or 4 Forms variation?
	 * 
	 * @param target
	 *            The character, that needed
	 * @return the integer number indicated the Number of forms the Character has,
	 *         return 2 otherwise
	 */
	private int getGlphyType(char target) {
		// Iterate over the 36 characters in the GLPHIES Matrix
		for (int n = 0; n < ARABIC_GLPHIES.length; n++) {
			// Check if the character equals the target character
			if (ARABIC_GLPHIES[n][0] == target)
				// Get the number of Forms that the character has
				return ARABIC_GLPHIES[n][5];
		}
		// Return the number 2 Otherwise
		return 2;
	}

	public static int getCase(char ch) {
		if (ch < 0x0621 || ch > 0x06d2) {
			return 0;
		}

		return ARABIC_GLPHIES[ch - 0x0621][1];
	}

	public static char getShape(char ch, int which_shape) {
		return ARABIC_GLPHIES[ch - 0x0621][2 + which_shape];
	}

	public static String reshape_reverse(String Str) {
		String Temp = " " + Str + "   ";
		char pre, at, post;
		StringBuilder reshapedString = new StringBuilder();
		int i = 0;
		int len = Str.length();
		char post_post;

		while (i < len) {
			pre = Temp.charAt(i + 2);
			at = Temp.charAt(i + 1);
			post = Temp.charAt(i);

			int which_case = getCase(at);
			int what_case_post = getCase(post);
			int what_case_pre = getCase(pre);
			int what_case_post_post;
			int pre_step = 0;

			if (what_case_pre == TASHKEEL) {
				pre = Temp.charAt(i + 3);
				what_case_pre = getCase(pre);
			}

			if ((what_case_pre & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
				pre_step = 1;
			}

			switch (which_case & 0x000F) {
			case NOTUSED_CHAR:
			case NOTARABIC_CHAR:
				reshapedString.append(at);
				i++;
				continue;
			case NORIGHT_NOLEFT_CHAR:
			case TATWEEL_CHAR:
				reshapedString.append(getShape(at, 0));
				i++;
				continue;
			case RIGHT_NOLEFT_CHAR_ALEF:
				if ((what_case_pre & 0x000F) == RIGHT_LEFT_CHAR_LAM) {
					pre = Temp.charAt(i + 3);
					what_case_pre = getCase(pre);
					pre_step = 0;

					if ((what_case_pre & LEFT_CHAR_MASK) == LEFT_CHAR_MASK) {
						pre_step = 1;
					}

					reshapedString.append(getShape(at, pre_step + 2));
					i = i + 2;
					continue;
				}
			case RIGHT_LEFT_CHAR_LAM:
			case RIGHT_LEFT_CHAR:
				if ((what_case_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
					reshapedString.append(getShape(at, 2 + pre_step));
					i = i + 1;
					continue;
				} else if (what_case_post == TANWEEN) {
					reshapedString.append(getShape(at, pre_step));
					i = i + 1;
					continue;
				} else if (what_case_post == TASHKEEL) {
					post_post = Temp.charAt(i + 3);
					what_case_post_post = getCase(post_post);

					if ((what_case_post_post & RIGHT_NOLEFT_CHAR_MASK) == RIGHT_NOLEFT_CHAR_MASK) {
						reshapedString.append(getShape(at, 2 + pre_step));
						i = i + 1;
						continue;
					}

					reshapedString.append(getShape(at, pre_step));
					i = i + 1;
					continue;
				} else {
					reshapedString.append(getShape(at, pre_step));
					i = i + 1;
					continue;
				}
			case RIGHT_NOLEFT_CHAR:
				reshapedString.append(getShape(at, pre_step));
				i = i + 1;
				continue;
			case TASHKEEL:
				reshapedString.append(getShape(at, 0));
				i++;
				continue;
			case TANWEEN:
				reshapedString.append(getShape(at, 0));
				i++;
				continue;
			default:
				reshapedString.append(getShape(at, 0));
				i++;
			}
		}

		return reshapedString.toString();
	}

	/**
	 * returns true if the target character is a haraka
	 * 
	 * @param target
	 * @return
	 */
	boolean isHaraka(char target) {

		for (int n = 0; n < HARAKATE.length; n++) {
			// Check if the character equals the target character
			if (HARAKATE[n] == target)
				// Get the number of Forms that the character has
				return true;
		}
		return false;
	}

	private String replaceLamAlef(String unshapedWord) {
		int wordLength = unshapedWord.length();
		char[] wordLetters = new char[wordLength];
		unshapedWord.getChars(0, wordLength, wordLetters, 0);
		char letterBefore = 0;
		for (int index = 0; index < wordLetters.length - 1; index++) {
			if (!isHaraka(wordLetters[index]) && DEFINED_CHARACTERS_ORGINAL_LAM != wordLetters[index]) {
				letterBefore = wordLetters[index];
			}
			if (DEFINED_CHARACTERS_ORGINAL_LAM == wordLetters[index]) {
				char candidateLam = wordLetters[index];
				int lamPosition = index;
				int harakaPosition = lamPosition + 1;

				while (harakaPosition < wordLetters.length && isHaraka(wordLetters[harakaPosition])) {
					harakaPosition++;
				}
				if (harakaPosition < wordLetters.length) {
					char lamAlef = 0;
					if (lamPosition > 0 && getGlphyType(letterBefore) > 2)
						lamAlef = getLamAlef(wordLetters[harakaPosition], candidateLam, false);
					else {
						lamAlef = getLamAlef(wordLetters[harakaPosition], candidateLam, true);
					}
					if (lamAlef != (char) 0) {
						wordLetters[lamPosition] = lamAlef;
						wordLetters[harakaPosition] = ' ';
					}
				}
			}

		}
		unshapedWord = new String(wordLetters);
		unshapedWord = unshapedWord.replaceAll(" ", "");

		return unshapedWord.trim();
	}

	/**
	 * Get LamAlef right Character Presentation of the character
	 * 
	 * @param candidateAlef
	 *            The letter that is supposed to Alef
	 * @param candidateLam
	 *            The letter that is supposed to Lam
	 * @param isEndOfWord
	 *            Is those characters at the end of the Word, to get its right form
	 * @return Reshaped character of the LamAlef
	 */
	private char getLamAlef(char candidateAlef, char candidateLam, boolean isEndOfWord) {
		// The shift rate, depends if the the end of the word or not!
		int shiftRate = 1;

		// The reshaped Lam Alef
		char reshapedLamAlef = 0;

		// Check if at the end of the word
		if (isEndOfWord)
			shiftRate++;

		// check if the Lam is matching the candidate Lam
		if (DEFINED_CHARACTERS_ORGINAL_LAM == candidateLam) {

			// Check which Alef is matching after the Lam and get Its form
			if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_MDD) {
				reshapedLamAlef = LAM_ALEF_GLPHIES[0][shiftRate];
			}

			if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF_UPPER_HAMAZA) {
				reshapedLamAlef = LAM_ALEF_GLPHIES[1][shiftRate];
			}

			if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF_LOWER_HAMAZA) {
				reshapedLamAlef = LAM_ALEF_GLPHIES[3][shiftRate];
			}

			if (candidateAlef == DEFINED_CHARACTERS_ORGINAL_ALF) {
				reshapedLamAlef = LAM_ALEF_GLPHIES[2][shiftRate];
			}

		}
		// return the ReshapedLamAlef
		return reshapedLamAlef;
	}

	/**
	 * Constructor of the Class
	 * 
	 * @param unshapedWord
	 *            The unShaped Word
	 */
	public ArabicReshaper(String unshapedWord) {
		unshapedWord = replaceLamAlef(unshapedWord);
		DecomposedWord decomposedWord = new DecomposedWord(unshapedWord);
		if (decomposedWord.stripedRegularLetters.length > 0) {
			_returnString = reshapeIt(new String(decomposedWord.stripedRegularLetters));
		}
		_returnString = decomposedWord.reconstructWord(_returnString);
	}

	/**
	 * Decompose the word into two parts: - simple letters with their positions -
	 * Tashkil alone with their position
	 *
	 */
	class DecomposedWord {
		char[] stripedHarakates;
		int[] harakatesPositions;
		char[] stripedRegularLetters;
		int[] lettersPositions;

		/**
		 * decompose the word
		 * 
		 * @param unshapedWord
		 */
		DecomposedWord(String unshapedWord) {
			int wordLength = unshapedWord.length();
			int harakatesCount = 0;
			for (int index = 0; index < wordLength; index++) {
				if (isHaraka(unshapedWord.charAt(index))) {
					harakatesCount++;
				}
			}
			harakatesPositions = new int[harakatesCount];
			stripedHarakates = new char[harakatesCount];
			lettersPositions = new int[wordLength - harakatesCount];
			stripedRegularLetters = new char[wordLength - harakatesCount];

			harakatesCount = 0;
			int letterCount = 0;
			for (int index = 0; index < unshapedWord.length(); index++) {
				if (isHaraka(unshapedWord.charAt(index))) {
					harakatesPositions[harakatesCount] = index;
					stripedHarakates[harakatesCount] = unshapedWord.charAt(index);
					harakatesCount++;
				} else {
					lettersPositions[letterCount] = index;
					stripedRegularLetters[letterCount] = unshapedWord.charAt(index);
					letterCount++;
				}
			}
		}

		/**
		 * reconstruct the word when the reshaping ahs been done
		 * 
		 * @param reshapedWord
		 * @return
		 */
		String reconstructWord(String reshapedWord) {
			char[] wordWithHarakates = null;
			wordWithHarakates = new char[reshapedWord.length() + stripedHarakates.length];
			for (int index = 0; index < lettersPositions.length; index++) {
				wordWithHarakates[lettersPositions[index]] = reshapedWord.charAt(index);
			}

			for (int index = 0; index < harakatesPositions.length; index++) {
				wordWithHarakates[harakatesPositions[index]] = stripedHarakates[index];
			}

			return new String(wordWithHarakates);

		}
	}

	/**
	 * Main Reshaping function, Doesn't Support LamAlef
	 * 
	 * @param unshapedWord
	 *            The unReshaped Word to Reshape
	 * @return The Reshaped Word without the LamAlef Support
	 */
	public String reshapeIt(String unshapedWord) {

		// The reshaped Word to Return
		StringBuffer reshapedWord = new StringBuffer("");
		int wordLength = unshapedWord.length();

		// The Word Letters
		char[] wordLetters = new char[wordLength];

		// Copy the unreshapedWord to the WordLetters Character Array
		unshapedWord.getChars(0, wordLength, wordLetters, 0);

		// for the first letter
		reshapedWord.append(getReshapedGlphy(wordLetters[0], 2));// 2 is the Form when the Letter is at the start of the
																	// word

		// iteration from the second till the second to last
		for (int i = 1; i < wordLength - 1; i++) {
			int beforeLast = i - 1;
			// Check if the Letter Before Last has only 2 Forms, for the current Letter to
			// be as a start for a new Word!
			if (getGlphyType(wordLetters[beforeLast]) == 2) { // checking if it's only has 2 shapes
				// If the letter has only 2 shapes, then it doesnt matter which position it is,
				// It'll be always the second form
				reshapedWord.append(getReshapedGlphy(wordLetters[i], 2));
			} else {
				// Then it should be in the middle which should be placed in its right form [3]
				reshapedWord.append(getReshapedGlphy(wordLetters[i], 3));
			}
		}

		// check for the last letter Before last has 2 forms, that means that the last
		// Letter will be alone.
		if (wordLength >= 2) {
			if (getGlphyType(wordLetters[wordLength - 2]) == 2) {
				// If the letter has only 2 shapes, then it doesnt matter which position it is,
				// It'll be always the second form
				reshapedWord.append(getReshapedGlphy(wordLetters[wordLength - 1], 1));
			} else {
				// Put the right form of the character, 4 for the last letter in the word
				reshapedWord.append(getReshapedGlphy(wordLetters[wordLength - 1], 4));
			}
		}
		// Return the ReshapedWord
		return reshapedWord.toString();
	}

}
