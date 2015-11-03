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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author plank
 */
public abstract class Screen extends JFrame implements WindowListener {

    private String author;

    public final String getAuthor() {
        return author;
    }

    public final void setAuthor(String newAuthor) {
        this.author = newAuthor;
    }

    private static final int FRAME_WIDTH = 450;
    private static final int FRAME_HEIGHT = 300;
    private boolean clearSFDuringQuery = true;

    public final boolean isClearSFDuringQuery() {
        return clearSFDuringQuery;
    }

    public final void setClearSFDuringQuery(boolean clearSFDuringQuery) {
        this.clearSFDuringQuery = clearSFDuringQuery;
    }

    private final JMenuBar MenuBar = new JMenuBar();

    public final static int getFRAME_WIDTH() {
        return FRAME_WIDTH;
    }

    public final static int getFRAME_HEIGHT() {
        return FRAME_HEIGHT;
    }

    public final JMenu getHelpMenu() {
        return HelpMenu;
    }

    public final void addMenuToBar(JMenu nmu) {
        this.MenuBar.add(nmu);
    }

    private final Vector<Object> wordList = new Vector<>();

    @SuppressWarnings("unchecked")
    public final void refreshWordList() {
        this.WordBank.setListData(wordList);
    }

    public final Vector<Object> getWordList() {
        return this.wordList;
    }

    @SuppressWarnings("unchecked")
    private final JList WordBank = new JList(wordList);
    private final JMenu HelpMenu = new JMenu("Help");
    private final JMenuItem CreditsItem = new JMenuItem("Created by " + author);

    private final JPanel LeftPanel = new JPanel(new BorderLayout());
    private final JTextField SearchField = new JTextField();

    private final JEditorPane DescriptionPane = new JEditorPane();

    private final JScrollPane ScrollWBnk = new JScrollPane(WordBank);
    private final JScrollPane ScrollBody = new JScrollPane(DescriptionPane);
    private final JSplitPane SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, LeftPanel, ScrollBody);

    public Screen(String name) {
        this(name, "UNKNOWN");
    }

    public Screen(String name, String newAuthor) {
        super("PAGE: " + name);
        if (newAuthor.length() > 0) {
            newAuthor = (newAuthor.charAt(0) + "").toUpperCase() + newAuthor.substring(1);
        }
        this.author = newAuthor;
        this.initializeComponents();
        this.postInit();
        this.MenuBar.add(HelpMenu);
        this.setVisible(true);
    }

    private void initializeComponents() {
        //this.MenuBar
        this.setJMenuBar(this.MenuBar);
        this.CreditsItem.setText("Created by " + author);
        this.CreditsItem.addActionListener((ActionEvent e) -> creditsItemAction(e));
        this.CreditsItem.setAccelerator(KeyStroke.getKeyStroke('H', KeyEvent.CTRL_DOWN_MASK, true));
        this.HelpMenu.add(CreditsItem);

        //this.SplitPane
        this.SplitPane.setOneTouchExpandable(true);
        int locWidth = FRAME_WIDTH / 4;
        this.SplitPane.setDividerLocation(locWidth);
        Dimension spMinDimension = new Dimension(locWidth, FRAME_HEIGHT);
        this.LeftPanel.setMinimumSize(spMinDimension);
        this.ScrollBody.setMinimumSize(spMinDimension);
        this.add(SplitPane, BorderLayout.CENTER);

        // this.DescriptionPane
        this.DescriptionPane.setEditable(false);
        this.DescriptionPane.setContentType("text/html");

        // this.SearchField
        this.SearchField.setToolTipText("Search from list");
        this.SearchField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    internalQuery();
                }
            }

        });
        this.LeftPanel.add(this.SearchField, BorderLayout.NORTH);

        //this.WordBank
        this.WordBank.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.WordBank.addListSelectionListener((ListSelectionEvent e) -> querySearchField(this.WordBank.getSelectedValue() + ""));
        this.LeftPanel.add(ScrollWBnk, BorderLayout.CENTER);

        // Last line of method addComponent
        this.addWindowListener(this);
        Dimension d = new Dimension(FRAME_WIDTH, FRAME_HEIGHT);
        this.setMinimumSize(d);
        this.setSize(d);
    }

    private void internalQuery() {
        String qs = this.SearchField.getText();
        if (clearSFDuringQuery) {
            this.SearchField.setText("");
        }
        querySearchField(qs);
    }

    public final void setSearchFieldTooltip(String s) {
        this.SearchField.setToolTipText(s);
    }

    public final void setDescriptionPaneText(String s) {
        this.DescriptionPane.setText(s);
    }

    public final void setCreditsItemAccelerator(KeyStroke ks) {
        this.CreditsItem.setAccelerator(ks);
    }

    public void creditsItemAction(ActionEvent e) {
        //
    }

    // Implemented Methods
    @Override
    public void windowOpened(WindowEvent e) {
        //
    }

    @Override
    public void windowClosed(WindowEvent e) {
        //
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.dispose();
        System.exit(0);
    }

    @Override

    public void windowIconified(WindowEvent e) {
        //
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        //
    }

    @Override
    public void windowActivated(WindowEvent e) {
        //
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        //
    }

    // Abstract Methods
    public abstract void postInit();

    public abstract void querySearchField(String s);
}
