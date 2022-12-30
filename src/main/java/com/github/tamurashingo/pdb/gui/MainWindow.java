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
 * GUI部の実装。
 * 主画面の表示と、各子画面・副画面の管理を行う。
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
     * GUI部からのイベントを受け付ける部分
     */
    private GUIEventHandler guiHandler;

    /**
     * メニュー
     */
    private PdbMenuBar menuBar;
    /**
     * 左側のツリーウィンドウ
     */
    private TreeWindow treeWin;
    /**
     * 右側のソースウィンドウ
     */
    private SourceWindow sourceWin;
    /**
     * 右下の情報ウィンドウ
     */
    private InformationWindow infoWin;

    private PdbProgressBar progressBar;

    /**
     * アバウト画面
     */
    private AboutDialog aboutDialog;


    /**
     * コンストラクタ
     */
    public MainWindow() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(GUIEventHandler handler) {
        log.trace("開始");
        guiHandler = handler;
        menuBar = new PdbMenuBar(handler);
        treeWin = new TreeWindow();
        sourceWin = new SourceWindow(handler);
        infoWin = new InformationWindow();
        aboutDialog = new AboutDialog();
        makeWindow();
        menuInit();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWindow() {
        log.trace("開始");
        GuiUtil.showCenter(this);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideWindow() {
        log.trace("開始");
        setVisible(false);
        log.trace("終了");
    }


    private void makeWindow() {
        log.trace("開始");

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

        log.trace("終了");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void openNewSource(String sourceName, String sourceType, String source) {
        log.trace("開始");
        sourceWin.addSource(sourceName, sourceType, source);
        List<BreakPointBean> list = Globals.getBreakPointManager().getBreakPoint(sourceName, sourceType);
        sourceWin.updateBreakPoint(sourceName, sourceType, list);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeSource() {
        log.trace("開始");
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] login() {
        log.trace("開始");
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

            log.trace("終了");
            return ret;
        }
        dialog.dispose();
        log.trace("終了");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProgressBar(String title, String message, BackGroundProcess proc) {
        log.trace("開始");
        progressBar = new PdbProgressBar(null, title, message, proc);
        progressBar.showProgressBar();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTreeWindow(List<ProceduresBean> list) {
        log.trace("開始");
        treeWin.updateWindow(list, guiHandler);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBreakPoint(String sourceName, String sourceType) {
        log.trace("開始");
        List<BreakPointBean> list = Globals.getBreakPointManager().getBreakPoint(sourceName, sourceType);
        sourceWin.updateBreakPoint(sourceName, sourceType, list);
        List<BreakPointBean> allList = Globals.getBreakPointManager().getAllBreakPoints();
        infoWin.updateBreakPoint(allList);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBreakPointSourceView(String sourceName, String sourceType, int line) {
        log.trace("開始");
        sourceWin.updateBreakPointSourceView(sourceName, sourceType, line);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateVariableView(List<VariablesBean> list) {
        log.trace("開始");
        infoWin.updateVariable(list);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuInit() {
        log.trace("開始");
        menuBar.init();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuDBConnect() {
        log.trace("開始");
        menuBar.DBConnect();
        updateTitlebar(null);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuDBClose() {
        log.trace("開始");
        menuBar.DBClose();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuRun() {
        log.trace("開始");
        menuBar.Run();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuBreak() {
        log.trace("開始");
        menuBar.Break();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuResume() {
        log.trace("開始");
        menuBar.Resume();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuStep() {
        log.trace("開始");
        menuBar.Step();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuStepOut() {
        log.trace("開始");
        menuBar.StepOut();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuStepOver() {
        log.trace("開始");
        menuBar.StepOver();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuRunEnd() {
        log.trace("開始");
        menuBar.RunEnd();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceView() {
        log.trace("開始");
        sourceWin.updateSourceView();
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showProcedureException(String source, int line, String sqlerrm) {
        log.trace("開始");
        StringBuilder buf = new StringBuilder();
        buf.append("例外が発生しました。");
        buf.append(NEWLINE);
        buf.append("  発生箇所：").append(source);
        buf.append("(").append(line).append(")");
        buf.append(NEWLINE);
        buf.append("--------------------");
        buf.append(NEWLINE);
        buf.append(sqlerrm);

        GuiUtil.showAlert(APP_FULL_NAME, buf.toString());
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSourceTree() {
        log.trace("開始");
        guiHandler.processGuiEvent(GUIEventHandler.ID.UPDATE_SOURCETREE, null);
        log.trace("終了");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTitlebar(String title) {
        log.trace("開始");
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
        log.trace("終了");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void showAbout() {
        log.trace("開始");
        aboutDialog.setVisible(true);
        log.trace("終了");
    }
}
