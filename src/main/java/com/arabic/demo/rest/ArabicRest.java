package com.arabic.demo.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qcri.farasa.diacritize.DiacritizeText;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.segmenter.Farasa;

@RestController
@RequestMapping("/api/process")
public class ArabicRest {

	private static Farasa farasaSegmenter = null;
	private static FarasaPOSTagger farasaPOSTagger = null;
	private static DiacritizeText tagger = null;

	static {
		try {
			farasaSegmenter = new Farasa();
			farasaPOSTagger = new FarasaPOSTagger(farasaSegmenter);
			tagger = new DiacritizeText(farasaSegmenter, farasaPOSTagger);
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@GetMapping()
	public ResponseEntity<String> getArabicWord(@RequestParam(name = "text", required = true) String text) throws Exception {
		String result = processOfDiacritics(text.trim(), tagger);
		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: processOfDiacritics Purpose: Diacritics Arabic text Author: Rawan
	 * Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan Alsaaran & Imam Mohammed
	 * Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	public static String processOfDiacritics(String text, DiacritizeText tagger)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String line = "";
		line = Normaliz(text);
		line = tagger.diacritize(line, true);
		line = AllUpDate(line);
		return line;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: AllUpDate Purpose: function to call all function up date diacritcs
	 * for farasa Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan
	 * Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String AllUpDate(String text)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String line = "";
		line = DiacriticsTanwin(text);
		line = DiacriticsShadah(line);
		line = UpDateHamzah(line);
		line = UpDateDii(line);
		line = UpDate(line);
		line = UpDateDiacriticsAWE(line);
		return line;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: NumberToWord Purpose: convert Arabic number into Arabic word for
	 * example عشرين <-٢٠ Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright:
	 * (c) Rawan Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String NumberToWord(String text) {
		NumberToArabic c = new NumberToArabic();
		int foo = Integer.parseInt(text);
		BigDecimal v = BigDecimal.valueOf(foo);
		String t = c.convertToArabic(v, "SAR");
		String splite[] = t.split("\\s", 0);
		for (int i = 0; i < splite.length; i++) {
			String word = splite[i];
			if (new String(word).equals("ريالاً") || new String(word).equals("لا") || new String(word).equals("غير.")
					|| new String(word).equals("سعودياً") || new String(word).equals("فقط")
					|| new String(word).equals("ريال") || new String(word).equals("سعودي")
					|| new String(word).equals("ريالات") || new String(word).equals("سعودية")) {
				splite[i] = "";
			} else {
				splite[i] = splite[i];
			}
		}
		String last = String.join(" ", splite);
		return last;
	}

	private static void processFile(DiacritizeText dt)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
		processBuffer(br, bw, dt);
		br.close();
		bw.close();
	}

	private static void processBuffer(BufferedReader br, BufferedWriter bw, DiacritizeText tagger)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String line = "";
		while ((line = br.readLine()) != null) {
			line = processOfDiacritics(line, tagger);
			System.out.println(line);
		}
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: Normaliz Purpose: Normalize arabic text remove repete letter also
	 * solve some of speling mistake Author: Rawan Alsaaran (r.alsaaran@gmail.com)
	 * Copyright: (c) Rawan Alsaaran & Imam Mohammed Ibn Saud Islamic university
	 * 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String Normaliz(String Dtext) {
		String Text = Dtext;
		Text = Text.replaceAll("١", "1");
		Text = Text.replaceAll("٢", "2");
		Text = Text.replaceAll("\\s١\\s", "واحد");
		Text = Text.replaceAll("\\s٢\\s", "اثنين");
		Text = Text.replaceAll("\\s1\\s", "واحد");
		Text = Text.replaceAll("\\s2\\s", "اثنين");
		Text = Text.replaceAll("٣", "3");
		Text = Text.replaceAll("٤", "4");
		Text = Text.replaceAll("٥", "5");
		Text = Text.replaceAll("٦", "6");
		Text = Text.replaceAll("٧", "7");
		Text = Text.replaceAll("٨", "8");
		Text = Text.replaceAll("٩", "9");
		Text = Text.replaceAll("٠", "0");
		Text = Text.replaceAll("ااا*", "ا");
		Text = Text.replaceAll("ببب*", "ب");
		Text = Text.replaceAll("تتت*", "ت");
		Text = Text.replaceAll("ههه*", "ه");
		Text = Text.replaceAll("ثثث*", "ث");
		Text = Text.replaceAll("ججج*", "ج");
		Text = Text.replaceAll("ححح*", "ح");
		Text = Text.replaceAll("خخخ*", "خ");
		Text = Text.replaceAll("ددد*", "د");
		Text = Text.replaceAll("ررر*", "ر");
		Text = Text.replaceAll("ككك*", "ك");
		Text = Text.replaceAll("\\sي\\s", " يا ");
		Text = Text.replaceAll("\\sف\\s", " في ");
		Text = Text.replaceAll("\\sع\\s", " على ");
		Text = Text.replaceAll("\\s\\s\\s*", " ");
		Text = Text.replaceAll("\\n\\n*", "\\n");
		Text = Text.replaceAll("[a-zA-Z]", "");
		String spliit[] = Text.split("\\s", 0);
		String word;
		for (int i = 0; i < spliit.length; i++) {
			word = spliit[i];
			boolean tt = word.matches("-?\\d+(\\.\\d+)?");
			if (tt == true) {
				word = NumberToWord(word);
			}
			spliit[i] = word;
		}
		Text = String.join(" ", spliit);
		return Text;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: DiacriticsTanwin Purpose: replace tanwin to add in last letter in
	 * word Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan
	 * Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String DiacriticsTanwin(String Dtext) {
		String Text = Dtext;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			for (int j = 0; j < chars.length; j++) {
				if (word.charAt(j) == 'ً' && !(j == word.length() - 1)) {
					if (word.charAt(j) == 'ً' && word.charAt(j + 1) == 'ا') {
						chars[j] = 'ا';
						chars[j + 1] = 'ً';
					}
				}
			}
			word = String.valueOf(chars);
			spliteText[i] = word;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: DiacriticsShadah Purpose: replace tanwin to add in last letter in
	 * word Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan
	 * Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String DiacriticsShadah(String Dtext) {
		String Text = Dtext;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			int lastchar = word.length() - 1;
			if (word.charAt(lastchar) == 'ّ') {
				word = word.substring(0, lastchar);
			}
			spliteText[i] = word;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: DiacriticsShadah Purpose: replace tanwin to add in last letter in
	 * word Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan
	 * Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String UpDateHamzah(String DiText)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		int Shadah = 0;
		int shadah = 0;
		int Hamzah = 0;
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			for (int j = 0; j < word.length(); j++) {
				if (Character.isLetter(word.charAt(j)) && Shadah < 3) {
					Shadah++;
					if (Shadah == 3) {
						shadah = j;
					}
				}
			}
			int length = word.length();
			char t[] = new char[length];
			if (shadah + 1 < word.length()) {
				if (word.charAt(shadah + 1) == 'ّ' && word.charAt(Hamzah) == 'أ') {
					if (Character.isLetter(word.charAt(1)) || Character.isDigit(word.charAt(1))) {
						if (Character.isLetter(word.charAt(1))) {
							t[0] = 'أ';
							t[1] = 'ُ';
							for (int j = 2; j < t.length; j++) {
								t[j] = word.charAt(j - 1);
							}
							word = String.valueOf(t);
						}
					} else {
						chars[1] = 'ُ';
						word = String.valueOf(chars);
					}
				}
			}
			spliteText[i] = word;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: UpDate Purpose: Add fatha in first letter in first word for each
	 * sentens also add sqone in last letter for each word Author: Rawan Alsaaran
	 * (r.alsaaran@gmail.com) Copyright: (c) Rawan Alsaaran & Imam Mohammed Ibn Saud
	 * Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String UpDate(String DiText)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		int fatha = 1;
		int squen = 0;
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			squen = word.length() - 1;
			if (i == 0) {
				if (fatha < word.length()) {
					if (Character.isLetter(word.charAt(fatha)) || Character.isDigit(word.charAt(fatha))) {
						if (Character.isLetter(word.charAt(fatha))) {
							char t[] = new char[squen + 2];
							if (word.charAt(0) != 'ب' && word.charAt(0) != 'ل' && word.charAt(0) != 'أ') {
								t[0] = word.charAt(0);
								t[fatha] = 'َ';
								for (int j = 2; j < t.length; j++) {
									t[j] = word.charAt(j - 1);
								}
								word = String.valueOf(t);
							}
							spliteText[i] = word;
						}
					} else {
						if (word.charAt(0) != 'ب' && word.charAt(0) != 'ل' && word.charAt(0) != 'أ') {
							chars[fatha] = 'َ';
							word = String.valueOf(chars);
						}
						spliteText[i] = word;
					}
				}
			}

			squen = word.length() - 1;
			chars = word.toCharArray();
			if (Character.isLetter(word.charAt(squen)) || Character.isDigit(word.charAt(squen))) {
				if (Character.isLetter(word.charAt(squen))) {
					if (word.charAt(squen) == 'ا' || word.charAt(squen) == 'ي' || word.charAt(squen) == 'و'
							|| word.charAt(squen) == 'ء' || word.charAt(squen) == 'ى') {
						spliteText[i] = word;
					} else {
						word = word.concat("ْ");
						spliteText[i] = word;
					}
				}
			} else {
				if (squen - 1 > 0) {
					if (word.charAt(squen - 1) == 'ا' || word.charAt(squen - 1) == 'ي' || word.charAt(squen - 1) == 'و'
							|| word.charAt(squen - 1) == 'ء' || word.charAt(squen - 1) == 'ى') {
						spliteText[i] = word;
					} else {
						chars[squen] = 'ْ';
						word = String.valueOf(chars);
						spliteText[i] = word;
					}
				}
			}
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: UpDateDii Purpose: Add damah befor و , add fatha befor ا , and add
	 * kasra befor ي Author: Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c)
	 * Rawan Alsaaran & Imam Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String UpDateDii(String DiText)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] chars = word.toCharArray();
			char[] LetterofWord = new char[word.length() * 2];
			int count = 0;
			for (int j = 0; j < word.length(); j++) {
				if (chars[j] == 'ا' || chars[j] == 'و' || chars[j] == 'ي') {
					if (chars[j] == 'ا' && j != 0) {
						if (Character.isLetter(word.charAt(j - 1)) || Character.isDigit(word.charAt(j - 1))) {
							if (Character.isLetter(word.charAt(j - 1))) {
								if (chars[j - 1] != 'ب' && chars[j - 1] != 'ل') {
									LetterofWord[count] = 'َ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									if (j - 1 == 0) {
										LetterofWord[count] = word.charAt(j);
										count++;
									} else {
										LetterofWord[count] = 'َ';
										count++;
										LetterofWord[count] = word.charAt(j);
										count++;
									}
								}
							}
						} else {
							if (chars[j - 2] != 'ب' && chars[j - 2] != 'ل') {
								LetterofWord[count - 1] = 'َ';
								count++;
								LetterofWord[count] = word.charAt(j);
								count++;
							} else {
								if (j - 2 == 0) {
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									LetterofWord[count - 1] = 'َ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								}
							}
						}
					} else {
						if (chars[j] == 'ا' && j == 0) {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					}

					if (chars[j] == 'و' && j != 0) {
						if (Character.isLetter(word.charAt(j - 1)) || Character.isDigit(word.charAt(j - 1))) {
							if (Character.isLetter(word.charAt(j - 1))) {
								if (chars[j - 1] != 'ب' && chars[j - 1] != 'ل') {
									LetterofWord[count] = 'ُ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									if (j - 1 == 0) {
										LetterofWord[count] = word.charAt(j);
										count++;
									} else {
										LetterofWord[count] = 'ُ';
										count++;
										LetterofWord[count] = word.charAt(j);
										count++;
									}
								}
							}
						} else {
							if (chars[j - 2] != 'ب' && chars[j - 2] != 'ل') {
								LetterofWord[count - 1] = 'ُ';
								count++;
								LetterofWord[count] = word.charAt(j);
								count++;
							} else {
								if (j - 2 == 0) {
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									LetterofWord[count - 1] = 'ُ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								}
							}
						}
					} else {
						if (chars[j] == 'و' && j == 0) {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					}

					if (chars[j] == 'ي' && j != 0) {
						if (Character.isLetter(word.charAt(j - 1)) || Character.isDigit(word.charAt(j - 1))) {
							if (Character.isLetter(word.charAt(j - 1))) {
								if (chars[j - 1] != 'ب' && chars[j - 1] != 'ل') {
									LetterofWord[count] = 'ِ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									if (j - 1 == 0) {
										LetterofWord[count] = word.charAt(j);
										count++;
									} else {
										LetterofWord[count] = 'ِ';
										count++;
										LetterofWord[count] = word.charAt(j);
										count++;
									}
								}
							}
						} else {
							if (chars[j - 2] != 'ب' && chars[j - 2] != 'ل') {
								LetterofWord[count - 1] = 'ِ';
								count++;
								LetterofWord[count] = word.charAt(j);
								count++;
							} else {
								if (j - 2 == 0) {
									LetterofWord[count] = word.charAt(j);
									count++;
								} else {
									LetterofWord[count - 1] = 'ِ';
									count++;
									LetterofWord[count] = word.charAt(j);
									count++;
								}
							}
						}
					} else {
						if (chars[j] == 'ي' && j == 0) {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					}
				} else {
					LetterofWord[count] = word.charAt(j);
					count++;
				}
			}
			String wordLast = "";
			for (int k = 0; k < LetterofWord.length && LetterofWord[k] != ' '; k++) {
				wordLast = wordLast + LetterofWord[k];
			}
			wordLast = wordLast.replaceAll("\0", "");
			spliteText[i] = wordLast;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	/*
	 * -----------------------------------------------------------------------------
	 * -- Name: UpDateDiacriticsAWE Purpose: remove any diacritics on ا،و،ي Author:
	 * Rawan Alsaaran (r.alsaaran@gmail.com) Copyright: (c) Rawan Alsaaran & Imam
	 * Mohammed Ibn Saud Islamic university 2017
	 * -----------------------------------------------------------------------------
	 * --
	 */
	private static String UpDateDiacriticsAWE(String DiText)
			throws FileNotFoundException, InterruptedException, Exception, IOException {
		String Text = DiText;
		String spliteText[];
		String TextUpDate = " ";
		String word = "";
		int count = 0;
		spliteText = Text.split("\\s", 0);
		for (int i = 0; i < spliteText.length; i++) {
			word = spliteText[i];
			char[] LetterofWord = new char[word.length() * 2];
			for (int j = 0; j < word.length(); j = j + 1) {
				if (word.charAt(j) == 'و' || word.charAt(j) == 'ا' || word.charAt(j) == 'ي') {
					if (j + 1 < word.length() - 1) {
						if (!Character.isLetter(word.charAt(j + 1)) && !Character.isDigit(word.charAt(j + 1))) {
							LetterofWord[count] = word.charAt(j);
							count++;
							j = j + 1;
						} else {
							LetterofWord[count] = word.charAt(j);
							count++;
						}
					} else {
						LetterofWord[count] = word.charAt(j);
						count++;
					}
				} else {
					LetterofWord[count] = word.charAt(j);
					count++;
				}
			}
			count = 0;
			String wordLast = "";
			for (int k = 0; k < LetterofWord.length && LetterofWord[k] != ' '; k++) {
				wordLast = wordLast + LetterofWord[k];
			}
			wordLast = wordLast.replaceAll("\0", "");
			spliteText[i] = wordLast;
		}
		TextUpDate = String.join(" ", spliteText);
		return TextUpDate;
	}

	public static BufferedReader openFileForReading(String filename) throws FileNotFoundException {
		BufferedReader sr = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
		return sr;
	}

	public static BufferedWriter openFileForWriting(String filename) throws FileNotFoundException {
		BufferedWriter sw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename))));
		return sw;
	}
}
