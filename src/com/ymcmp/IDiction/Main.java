/*
 * The MIT License
 *
 * Copyright 2015 plank.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ymcmp.IDiction;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author plank
 */
public class Main {

    private static final String HTML_INDENT = "&nbsp;&nbsp;&nbsp;&nbsp;"; // Four spaces...
    private static final String HTML_HORIZN = "<hr />";
    private static final Map<Object, Object> definitions = new HashMap<>();

    static {
        Properties prop = new Properties();
        InputStream in = Main.class.getResourceAsStream("Library.properties");
        try {
            prop.load(in);
            in.close();
        } catch (java.io.IOException ex) {
            throw new RuntimeException("Cannot load nessesary files. Quitting");
        }
        definitions.putAll(prop);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // L&F
        try {
            System.out.println("Attempt to use System L&F");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            System.err.println("Fallback using Cross Platform L&F -> Metal");
        }

        new Screen("Custom Spoken Language Translator", "Plankp") {
            private boolean isUpdating = false;

            @Override
            public void postInit() {
                JMenuItem symaticRules = new JMenuItem("Sematic rules");
                symaticRules.addActionListener((ActionEvent e) -> {
                    JOptionPane.showMessageDialog(null, "Here are some rules that can help you from messing up:\n"
                            + "\"C\" is prononced as \"Ch\" in \"Cheese\" and \"CC\" is pronounced as \"K\"\n"
                            + "Verbs do not have tenses not conjugation. They are always infinitive and time is what matters\n"
                            + "Articles of the start of the phrase must be omitted\n"
                            + "\"The\" does not exist because it is always omitted\n"
                            + "During speech, \"He\" is always used (not \"She\" or \"It\"). Not during writing however"
                            + "\"Pjok\" is pronounced like \"Peeyok\"");
                });

                JMenuItem updateDictionary = new JMenuItem("Check for update");
                updateDictionary.setAccelerator(KeyStroke.getKeyStroke('U', KeyEvent.CTRL_DOWN_MASK, true));
                updateDictionary.addActionListener((ActionEvent e) -> {
                    if (!isUpdating) {
                        System.out.println("Update started...");
                        isUpdating = true;
                        // Attempts to download from
                        try {
                            Properties newProp = readWebProp("https://raw.githubusercontent.com/plankp/Dictionary/master/src/com/ymcmp/IDiction/Library.properties");
                            System.out.println("Applying patch...");
                            if (definitions.entrySet().equals(newProp.entrySet())) {
                                JOptionPane.showMessageDialog(null, "Already newest");
                            } else {
                                definitions.putAll(newProp);
                                this.getWordList().clear();
                                this.getWordList().addAll(new TreeSet<>(definitions.keySet()));
                                this.refreshWordList();
                                JOptionPane.showMessageDialog(null, "Update done");
                            }
                        } catch (RuntimeException ex) {
                            System.out.println("Update failed " + ex.getMessage());
                            JOptionPane.showMessageDialog(null, "Update failed " + ex.getMessage());
                        }
                        isUpdating = false;
                    } else {
                        JOptionPane.showMessageDialog(null, "Already started updating");
                    }
                });

                this.getHelpMenu().add(updateDictionary);
                this.getHelpMenu().add(symaticRules);

                this.getWordList().addAll(new TreeSet<>(definitions.keySet()));
                this.setSearchFieldTooltip("Search from list / Trove de largern");
                this.setDescriptionPaneText(HTMLDocument("Hello", "Welcome to the dictionary!!!") + HTMLDocument("Oi", "Welkomen ga larjernok!!!"));
            }

            @Override
            public void querySearchField(String s) {
                String[] wList = s.trim().split("\\s+|\\W+");
                StringBuilder sb = new StringBuilder();
                for (String txt : wList) {
                    if (txt.equals("the")) {
                        continue; // the does not exist
                    }
                    boolean caps = txt.matches("[A-Z].*");
                    txt = txt.toLowerCase();
                    if (definitions.containsKey(txt)) {
                        String tmp = definitions.get(txt) + "";
                        if (!caps) {
                            tmp = tmp.toLowerCase();
                        }
                        sb.append(tmp);
                    } else {
                        sb.append("`").append(txt).append("'");
                    }
                    sb.append(" ");
                }
                String tmp = sb.toString();
                sb.setLength(0);
                sb.append(HTMLDocument(s, tmp + "<br />Words with ` ' do not exist. Mail 'plankp@outlook.com' about it..."));
                this.setDescriptionPaneText(sb.toString());
            }

            @Override
            public void creditsItemAction(ActionEvent e) {
                JOptionPane.showMessageDialog(null, CREDITS);
            }
            private static final String CREDITS = "People involved:\nPlankp, Guiu, Jliao, Anexb";

        };
    }

    private static Properties readWebProp(String rawUrl) {
        Properties prop = new Properties();
        try {
            URL url = new URL(rawUrl);
            try (InputStreamReader isr = new InputStreamReader(url.openStream())) {
                prop.load(isr);
            } // Just for auto close
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return prop;
    }

    private static String HTMLDocument(String header, String body) {
        return HTMLHeader(header) + HTML_HORIZN + body + "<br />";
    }

    private static String HTMLHeader(String s) {
        return HTMLHeader(s, 1);
    }

    private static String HTMLHeader(String s, int level) {
        if (level < 1 || level > 6) {
            throw new IllegalArgumentException("Parameter `level' must be range 1..6:" + level);
        }
        String label = "h" + level + ">";
        return "<" + label + s + "</" + label;
    }

}
