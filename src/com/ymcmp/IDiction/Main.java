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
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author plank
 */
public class Main {

    private static final String HTML_HORIZN = "<hr />";
    private static final Dictionary<Object, Object> dictionary = new Dictionary<>();
    private static final String[] FRESH_LIB_SRC = {
        "https://raw.githubusercontent.com/plankp/Dictionary/master/src/com/ymcmp/IDiction/Library.properties",
        "https://raw.githubusercontent.com/plankp/Dictionary/master/src/com/ymcmp/IDiction/Duplicates.properties"
    };

    static {
        dictionary.addAll(initRead("Library.properties"));
        dictionary.addAll(initRead("Duplicates.properties"));
    }

    private static Properties initRead(String path) throws RuntimeException {
        Properties prop = new Properties();
        InputStream in = Main.class
                .getResourceAsStream(path);

        try {
            prop.load(in);
            in.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot load nessesary files. Quitting");
            throw new RuntimeException("Cannot load nessesary files. Quitting");
        }
        return prop;
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
            private boolean displayEnglish = true;

            private void redrawWordList() {
                this.getWordList().clear();
                List<Object> append;
                if (displayEnglish) {
                    append = dictionary.getKeys();
                } else {
                    append = dictionary.getValues();
                }
                Collection<Object> set = new TreeSet<>(append);
                this.getWordList().addAll(set);
                this.setFooterText("Word count: " + set.size());
                this.refreshWordList();
            }

            @Override
            public void postInit() {
                ButtonGroup bgroup = new ButtonGroup();
                JRadioButtonMenuItem displayEng = new JRadioButtonMenuItem("Show English List", true);
                JRadioButtonMenuItem displayPja = new JRadioButtonMenuItem("Show Pjarg List");
                bgroup.add(displayEng);
                bgroup.add(displayPja);

                displayEng.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_DOWN_MASK, true));
                displayPja.setAccelerator(KeyStroke.getKeyStroke('P', KeyEvent.CTRL_DOWN_MASK, true));

                displayEng.addActionListener((ActionEvent e) -> {
                    displayEnglish = true;
                    redrawWordList();
                });
                displayPja.addActionListener((ActionEvent e) -> {
                    displayEnglish = false;
                    redrawWordList();
                });

                JMenu configMenu = new JMenu("Config");
                configMenu.add(displayEng);
                configMenu.add(displayPja);
                this.addMenuToBar(configMenu);

                JMenuItem sematicRules = new JMenuItem("Sematic rules");
                sematicRules.addActionListener((ActionEvent e) -> {
                    JOptionPane.showMessageDialog(null, "Here are some rules that can help you from messing up:\n"
                            + "\"C\" is prononced as \"Ch\" in \"Cheese\" and \"CC\" is pronounced as \"K\"\n"
                            + "Verbs do not have tenses not conjugation. They are always infinitive and time is what matters\n"
                            + "Articles of the start of the phrase must be omitted\n"
                            + "\"The\" does not exist because it is always omitted\n"
                            + "During speech, \"He\" is always used (not \"She\" or \"It\"). Not during writing however\n"
                            + "\"Pjok\" is pronounced like \"Peeyok\"");
                });
                sematicRules.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_DOWN_MASK, true));

                JMenuItem updateDictionary = new JMenuItem("Check for update");
                updateDictionary.setAccelerator(KeyStroke.getKeyStroke('U', KeyEvent.CTRL_DOWN_MASK, true));
                updateDictionary.addActionListener((ActionEvent e) -> {
                    if (!isUpdating) {
                        System.out.println("Update started...");
                        isUpdating = true;
                        try {
                            dictionary.clear();
                            for (String src : FRESH_LIB_SRC) {
                                Properties newProp = readWebProp(src);
                                System.out.println("Applying patch...");
                                dictionary.addAll(newProp);
                                redrawWordList();
                            }
                            JOptionPane.showMessageDialog(null, "Update done");
                        } catch (RuntimeException ex) {
                            System.out.println("Update failed " + ex.getMessage());
                            JOptionPane.showMessageDialog(null, "Update failed " + ex.getMessage());
                            System.out.println("Re-invoke init read...");
                            dictionary.addAll(initRead("Library.properties"));
                            dictionary.addAll(initRead("Duplicates.properties"));
                            this.getWordList().clear();

                        }
                        isUpdating = false;
                    } else {
                        JOptionPane.showMessageDialog(null, "Already started updating");
                    }
                });

                this.getHelpMenu().add(updateDictionary);
                this.getHelpMenu().add(sematicRules);

                this.getWordList().addAll(new TreeSet<>(dictionary.getKeys()));
                this.setSearchFieldTooltip("Search from list / Trove de largern");
                this.setDescriptionPaneText(HTMLDocument("Hello", "Welcome to the dictionary!!!") + HTMLDocument("Oi", "Welkomen ga larjernok!!!"));

                displayEnglish = true;
                redrawWordList();
            }

            private String appendPjargPlural(char last) {
                String msg = "";
                switch (last) {
                case 's':
                case 'v':
                case 'g':
                    msg += "e";
                default:
                    msg += "s";
                }
                return msg;
            }

            private String appendEngPlural(String last) {
                if (last.equals("us")) {
                    return "i";
                }
                if (last.equals("s") | last.equals("x")) {
                    return "es";
                }
                return "s";
            }

            private List<Object> getVocab(String txt) {
                return displayEnglish ? dictionary.getValues(txt) : dictionary.getKeys(stmtCase(txt));
            }

            private void appendText(String txt, StringBuilder sb) {
                boolean caps = txt.matches("[A-Z].*");
                txt = txt.toLowerCase();
                List<Object> vList = getVocab(txt);
                if (vList != null) {
                    // word exists -- Append it
                    AppendWordQuery(vList, caps, sb);
                } else if (txt.matches(".+((e?s)|(i))")) {
                    // Cannot find because of plural?
                    String txt2 = "";
                    if (txt.matches("e?s$")) {
                        txt2 = txt.split("e?s$")[0];
                    } else if (txt.matches("i$") && displayEnglish) {
                        txt2 = txt.split("i$")[0] + "us";
                    }
                    vList = getVocab(txt2);
                    if (vList != null) {
                        // word exists -- Append it
                        AppendWordQuery(vList, caps, sb);
                        if (displayEnglish) {
                            sb.append(appendPjargPlural(sb.charAt(sb.length() - 1)));
                        } else {
                            sb.append(appendEngPlural(sb.substring(sb.length() - 2)));
                        }
                    } else {
                        txt2 = txt.split("s$")[0];
                        vList = getVocab(txt2);
                        if (vList != null) {
                            // word exists -- Append it
                            AppendWordQuery(vList, caps, sb);
                            if (displayEnglish) {
                                sb.append(appendPjargPlural(sb.charAt(sb.length() - 1)));
                            } else {
                                sb.append(appendEngPlural(sb.substring(sb.length() - 2)));
                            }
                        } else {
                            invalidTerm(sb, caps, txt);
                        }
                    }
                } else {
                    invalidTerm(sb, caps, txt);
                }
            }

            private void invalidTerm(StringBuilder sb, boolean caps, String txt) {
                // show word not found
                sb.append("`");
                if (!caps) {
                    sb.append(txt);
                } else {
                    sb.append(stmtCase(txt));
                }
                sb.append("'");
            }

            @Override
            public void querySearchField(String s) {
                String[] wList = s.trim().split("\\s+");
                // Parse: Hello people. -> ["Hello", "people."]
                StringBuilder sb = new StringBuilder();
                boolean nextwordCaps = false;
                for (String txt : wList) {
                    if (txt.trim().length() == 0 || txt.equals("the")) {
                        continue; // the does not exist
                    }
                    if (txt.equals("The")) {
                        nextwordCaps = true;
                        continue;
                    }
                    String remain = "";
                    if (txt.matches(".*\\W+$")) {
                        // Parse: help?
                        String[] txtSplit = txt.split("\\W+$");
                        if (txtSplit.length > 0) {
                            String splits = txtSplit[0];
                            remain += txt.substring(splits.length());
                            txt = splits;
                        } else {
                            sb.append(txt);
                            continue;
                        }
                    }
                    if (txt.trim().matches("\\d+(\\.\\d+)?$")) {
                        sb.append(txt);
                    } else {
                        if (nextwordCaps) {
                            txt = stmtCase(txt);
                            nextwordCaps = false;
                        }
                        if (displayEnglish && (txt.endsWith("'s") || txt.endsWith("s'"))) {
                            sb.append(txt.substring(0, txt.length() - 2)).append(" ");
                            appendText(K_APOSTROPHE_S, sb);
                        } else {
                            appendText(txt, sb);
                        }
                    }
                    sb.append(remain).append(" ");
                }
                String tmp = sb.toString();
                sb.setLength(0);
                sb.append(HTMLDocument(s, tmp + "<br />Words with ` ' do not exist. Mail 'plankp@outlook.com' about it..."));
                this.setDescriptionPaneText(sb.toString());
            }
            private static final String K_APOSTROPHE_S = "-belong";

            private void AppendWordQuery(List<Object> vList, boolean caps, StringBuilder sb) {
                String tmp;
                if (vList.size() == 1) {
                    tmp = vList.get(0) + "";
                    if (!caps) {
                        tmp = tmp.toLowerCase();
                    }
                } else {
                    tmp = vList + "";
                }
                sb.append(tmp);
            }

            private String stmtCase(String txt) {
                if (txt == null || txt.length() == 0) {
                    return txt;
                }
                if (txt.length() > 1) {
                    return ("" + txt.charAt(0)).toUpperCase() + txt.substring(1);
                } else {
                    return txt.toUpperCase();
                }
            }

            @Override
            public void creditsItemAction(ActionEvent e) {
                JOptionPane.showMessageDialog(null, CREDITS);
            }
            private static final String CREDITS = "People involved:\nPlankp, Anexb, Guiu, Ryan, Jliao";

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
