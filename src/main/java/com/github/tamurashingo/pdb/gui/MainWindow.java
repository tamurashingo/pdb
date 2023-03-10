/*-
 * The MIT License (MIT)
 *
 * Copyright (c) 2010 tamura shingo
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
package com.github.tamurashingo.pdb.gui;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import com.github.tamurashingo.pdb.Globals;
import com.github.tamurashingo.pdb.bean.BreakPointBean;
import com.github.tamurashingo.pdb.bean.ProceduresBean;
import com.github.tamurashingo.pdb.bean.VariablesBean;
import com.github.tamurashingo.pdb.db.ConnectConfig;
import com.github.tamurashingo.pdb.gui.parts.AboutDialog;
import com.github.tamurashingo.pdb.gui.parts.InformationWindow;
import com.github.tamurashingo.pdb.gui.parts.PdbMenuBar;
import com.github.tamurashingo.pdb.gui.parts.SourceWindow;
import com.github.tamurashingo.pdb.gui.parts.TreeWindow;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.BackGroundProcess;
import com.github.tamurashingo.pdb.util.GuiUtil;

import static com.github.tamurashingo.pdb.SystemConstants.APP_FULL_NAME;
import static com.github.tamurashingo.pdb.SystemConstants.NEWLINE;
import static com.github.tamurashingo.pdb.SystemConstants.icon;
import static com.github.tamurashingo.pdb.gui.GUIConstants.MAINWINDOW_HEIGHT;
import static com.github.tamurashingo.pdb.gui.GUIConstants.MAINWINDOW_WIDTH;
import static com.github.tamurashingo.pdb.gui.GUIConstants.SOURCEWINDOW_HEIGHT;
import static com.github.tamurashingo.pdb.gui.GUIConstants.TREEWINDOW_WIDTH;

/**
 * GUI???????????????
 * ?????????????????????????????????????????????????????????????????????
 *
 * @author tamura shingo
 */
public class MainWindow extends JFrame implements GUIRequestHandler {

    private static final long serialVersionUID = -8118923395794599147L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    /**
     * GUI????????????????????????????????????????????????
     */
    private GUIEventHandler guiHandler;

    /**
     * ????????????
     */
    private PdbMenuBar menuBar;
    /**
     * ?????????????????????????????????
     */
    private TreeWindow treeWin;
    /**
     * ?????????????????????????????????
     */
    private SourceWindow sourceWin;
    /**
     * ??????????????????????????????
     */
    private InformationWindow infoWin;

    private PdbProgressBar progressBar;

    /**
     * ??????????????????
     */
    private AboutDialog aboutDialog;


    /**
     * ?????????????????????
     */
    public MainWindow() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(GUIEventHandler handler) {
        log.trace("??????");
        guiHandler = handler;
        menuBar = new PdbMenuBar(handler);
        treeWin = new TreeWindow();
        sourceWin = new SourceWindow(handler);
        infoWin = new InformationWindow();
        aboutDialog = new AboutDialog();
        makeWindow();
        menuInit();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWindow() {
        log.trace("??????");
        GuiUtil.showCenter(this);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideWindow() {
        log.trace("??????");
        setVisible(false);
        log.trace("??????");
    }


    private void makeWindow() {
        log.trace("??????");

        updateTitlebar(null);
        setSize(MAINWINDOW_WIDTH, MAINWINDOW_HEIGHT);
        setIconImages(icon);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                guiHandler.processGuiEvent(GUIEventHandler.ID.WINDOW_CLOSE, null);
                dispose();
            }

            @Override
            public void windowClosed(WindowEvent event) {
                System.exit(0);
            }
        });

        Container contentPane = getContentPane();
        setJMenuBar(menuBar.makeMenu());

        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane subPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        mainPane.setLeftComponent(treeWin);
        mainPane.setRightComponent(subPane);
        subPane.setLeftComponent(sourceWin);
        subPane.setRightComponent(infoWin);

        mainPane.setDividerLocation(TREEWINDOW_WIDTH);
        subPane.setDividerLocation(SOURCEWINDOW_HEIGHT);

        contentPane.add(menuBar.makeToolBar(), BorderLayout.NORTH);
        contentPane.add(mainPane, BorderLayout.CENTER);

        log.trace("??????");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void openNewSource(String sourceName, String sourceType, String source) {
        log.trace("??????");
        sourceWin.addSource(sourceName, sourceType, source);
        List<BreakPointBean> list = Globals.getBreakPointManager().getBreakPoint(sourceName, sourceType);
        sourceWin.updateBreakPoint(sourceName, sourceType, list);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeSource() {
        log.trace("??????");
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] login() {
        log.trace("??????");
        LoginDialog dialog = new LoginDialog(this);
        dialog.showDialog();
        if (dialog.isLogin()) {
            String[] ret = new String[5];
            ret[0] = dialog.getUserid();
            ret[1] = dialog.getPassword();
            ret[2] = dialog.getDbname();
            ret[3] = dialog.getServer();
            ret[4] = dialog.getPort();

            dialog.dispose();

            log.trace("??????");
            return ret;
        }
        dialog.dispose();
        log.trace("??????");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgressBar(String title, String message, BackGroundProcess proc) {
        log.trace("??????");
        progressBar = new PdbProgressBar(null, title, message, proc);
        progressBar.showProgressBar();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTreeWindow(List<ProceduresBean> list) {
        log.trace("??????");
        treeWin.updateWindow(list, guiHandler);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBreakPoint(String sourceName, String sourceType) {
        log.trace("??????");
        List<BreakPointBean> list = Globals.getBreakPointManager().getBreakPoint(sourceName, sourceType);
        sourceWin.updateBreakPoint(sourceName, sourceType, list);
        List<BreakPointBean> allList = Globals.getBreakPointManager().getAllBreakPoints();
        infoWin.updateBreakPoint(allList);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBreakPointSourceView(String sourceName, String sourceType, int line) {
        log.trace("??????");
        sourceWin.updateBreakPointSourceView(sourceName, sourceType, line);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateVariableView(List<VariablesBean> list) {
        log.trace("??????");
        infoWin.updateVariable(list);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuInit() {
        log.trace("??????");
        menuBar.init();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuDBConnect() {
        log.trace("??????");
        menuBar.DBConnect();
        updateTitlebar(null);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuDBClose() {
        log.trace("??????");
        menuBar.DBClose();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuRun() {
        log.trace("??????");
        menuBar.Run();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuBreak() {
        log.trace("??????");
        menuBar.Break();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuResume() {
        log.trace("??????");
        menuBar.Resume();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuStep() {
        log.trace("??????");
        menuBar.Step();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuStepOut() {
        log.trace("??????");
        menuBar.StepOut();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuStepOver() {
        log.trace("??????");
        menuBar.StepOver();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuRunEnd() {
        log.trace("??????");
        menuBar.RunEnd();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceView() {
        log.trace("??????");
        sourceWin.updateSourceView();
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProcedureException(String source, int line, String sqlerrm) {
        log.trace("??????");
        StringBuilder buf = new StringBuilder();
        buf.append("??????????????????????????????");
        buf.append(NEWLINE);
        buf.append("  ???????????????").append(source);
        buf.append("(").append(line).append(")");
        buf.append(NEWLINE);
        buf.append("--------------------");
        buf.append(NEWLINE);
        buf.append(sqlerrm);

        GuiUtil.showAlert(APP_FULL_NAME, buf.toString());
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceTree() {
        log.trace("??????");
        guiHandler.processGuiEvent(GUIEventHandler.ID.UPDATE_SOURCETREE, null);
        log.trace("??????");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTitlebar(String title) {
        log.trace("??????");
        StringBuilder buf = new StringBuilder();
        buf.append(APP_FULL_NAME);

        ConnectConfig config = ConnectConfig.getInstance();
        if (config.getConnected()) {
            buf.append(" - ");
            buf.append(config.getUserid());
            buf.append("@");
            buf.append(config.getDbname());
        }

        if (!(title == null || title.isEmpty())) {
            buf.append(" ");
            buf.append(title);
        }

        setTitle(buf.toString());
        log.trace("??????");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void showAbout() {
        log.trace("??????");
        aboutDialog.setVisible(true);
        log.trace("??????");
    }
}
