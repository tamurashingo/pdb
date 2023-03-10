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

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import com.github.tamurashingo.pdb.SystemConstants;
import com.github.tamurashingo.pdb.bean.DBConnectInfoBean;
import com.github.tamurashingo.pdb.log.Log;
import com.github.tamurashingo.pdb.log.Logger;
import com.github.tamurashingo.pdb.util.GuiUtil;

import static com.github.tamurashingo.pdb.SystemConstants.icon;


/**
 * Oracle????????????????????????????????????????????????????????????
 *
 * @author tamura shingo
 */
public class LoginDialog extends JDialog {

    private static final long serialVersionUID = 8982761915774012029L;

    private static final Log log;

    static {
        log = Logger.getLogger();
    }

    /**
     * ?????????ID
     */
    private JTextField userid = new JTextField(15);

    /**
     * ???????????????
     */
    private JPasswordField password = new JPasswordField(15);

    /**
     * ????????????
     */
    private JTextField server = new JTextField(15);

    /**
     * ???????????????
     */
    private JTextField port = new JTextField(15);

    /**
     * DB???
     */
    private JTextField dbname = new JTextField(15);

    /**
     * ?????????????????????????????????
     */
    private JComboBox connectList = new JComboBox();

    /**
     * ??????????????????????????????????????????true
     */
    private boolean login = false;

    /**
     * ?????????????????????
     *
     * @param parent ???????????????
     */
    public LoginDialog(Frame parent) {
        super(parent, true);
        makeDialog();
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @return ??????????????????????????????????????????true
     */
    public boolean isLogin() {
        log.trace("??????");
        log.trace("??????");
        return login;
    }

    /**
     * ?????????ID??????????????????
     *
     * @return ?????????ID
     */
    public String getUserid() {
        log.trace("??????");
        log.trace("??????");
        return userid.getText();
    }

    public String getPassword() {
        log.trace("??????");
        char[] pass = password.getPassword();
        String ret = new String(pass);
        for (int ix = 0; ix < pass.length; ix++) {
            pass[ix] = 0;
        }
        log.trace("??????");
        return ret;
    }

    /**
     * ???????????????????????????
     *
     * @return ????????????
     */
    public String getServer() {
        log.trace("??????");
        log.trace("??????");
        return server.getText();
    }

    /**
     * ??????????????????????????????
     *
     * @return ???????????????
     */
    public String getPort() {
        log.trace("??????");
        log.trace("??????");
        return port.getText();
    }

    /**
     * DB??????????????????
     *
     * @return DB???
     */
    public String getDbname() {
        log.trace("??????");
        log.trace("??????");
        return dbname.getText();
    }

    /**
     * ?????????????????????????????????
     */
    private void makeDialog() {
        log.trace("??????");

        setTitle("Login");
        setIconImages(icon);
        setLayout(new BorderLayout(0, 5));
        Container contentPane = getContentPane();

        // ???????????????
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        titlePanel.add(new JLabel("?????????????????????????????????"));

        // ???????????????????????????
        JPanel infoPanel = new JPanel();
        GroupLayout layout = new GroupLayout(infoPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        infoPanel.setLayout(layout);

        JLabel labelConn = new JLabel("????????????");
        JLabel labelUser = new JLabel("????????????");
        JLabel labelPassword = new JLabel("???????????????");
        JLabel labelDatabase = new JLabel("?????????????????????");
        JLabel labelServer = new JLabel("?????????");
        JLabel labelPort = new JLabel("???????????????");

        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        hGroup.addGroup(layout.createParallelGroup().addComponent(labelConn)
                .addComponent(labelUser)
                .addComponent(labelPassword)
                .addComponent(labelDatabase)
                .addComponent(labelServer)
                .addComponent(labelPort));
        hGroup.addGroup(layout.createParallelGroup().addComponent(connectList)
                .addComponent(userid)
                .addComponent(password)
                .addComponent(dbname)
                .addComponent(server)
                .addComponent(port));
        layout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelConn).addComponent(connectList));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelUser).addComponent(userid));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelPassword).addComponent(password));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelDatabase).addComponent(dbname));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelServer).addComponent(server));
        vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(labelPort).addComponent(port));
        layout.setVerticalGroup(vGroup);


        // ????????????????????????
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                login = true;
                setVisible(false);
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                login = false;
                setVisible(false);
            }
        });
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        contentPane.add(titlePanel, BorderLayout.NORTH);
        contentPane.add(infoPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // ????????????????????????
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Map<String, DBConnectInfoBean> map = SystemConstants.config.getConnectInfo();
        for (String key : map.keySet()) {
            model.addElement(key);
        }
        connectList.setModel(model);
        connectList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                log.trace("??????");
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    log.debug("??????");
                    DBConnectInfoBean bean = SystemConstants.config.getConnectInfo().get(event.getItem());
                    userid.setText(bean.getUsername());
                    password.setText(bean.getPassword());
                    dbname.setText(bean.getDbname());
                    server.setText(bean.getServer());
                    port.setText(bean.getPort());
                }
                log.trace("??????");
            }
        });

        pack();
        log.trace("??????");
    }

    public void showDialog() {
        log.trace("??????");
        GuiUtil.showCenter(this);
        log.trace("??????");
    }
}
