/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package projectmusic;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
/**
 *
 * @author huudu
 */
public class Client extends javax.swing.JFrame {

    /**
     * Creates new form Client
     */
    public Client() {
        initComponents();
//        btnSearch.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                btnSearchActionPerformed(e);
//            }
//        });

        btnSearchOf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = resultTable.getSelectedRow();
                int col = resultTable.getSelectedColumn();
                if (row == -1 || col == -1) {
                    // No cell selected, exit the method
                    return;
                }
                // Display values in JTextFields
                txtTitle.setText(resultTable.getValueAt(row, 0).toString());
                txtArtist.setText(resultTable.getValueAt(row, 1).toString());
                txtLyrics.setText(resultTable.getValueAt(row, 2).toString());
                txtBD.setText(resultTable.getValueAt(row, 3).toString());
                txtBP.setText(resultTable.getValueAt(row, 4).toString());
            }
        });
    }
    
    private void searchSongs(String searchQuery) {
        try (
            Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

//            String searchType = "artist";
            searchQuery = txtSearch.getText();
            
//            out.writeObject(searchType);
            out.writeObject(searchQuery);

            List<Song> searchResults = (List<Song>) in.readObject();
            displaySearchResults(searchResults);
            socket.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
//    private void displaySearchResults(List<Song> searchResults) {
//        DefaultTableModel tableModel = (DefaultTableModel) resultTable.getModel();
//        tableModel.setRowCount(0);
//
//        for (Song song : searchResults) {
//            tableModel.addRow(new Object[]{song.getTitle(), song.getArtist(), song.getLyrics(), song.getYoutubeLink()});
//        }
//    }
    
    private void displaySearchResults(List<?> searchResults) {
        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
        model.setRowCount(0);

        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có kết quả tìm kiếm.");
            return;
        }

        if (searchResults.get(0) instanceof Song) {
            // Nếu kết quả là danh sách bài hát
            for (Object result : searchResults) {
                Song song = (Song) result;
                model.addRow(new Object[]{song.getTitle(), song.getArtist(), song.getLyrics(), song.getYoutubeLink()});
            }
        } else if (searchResults.get(0) instanceof Artist) {
            // Nếu kết quả là danh sách ca sĩ
            for (Object result : searchResults) {
                Artist artist = (Artist) result;
                model.addRow(new Object[]{artist.getArtistName(), artist.getArtistRName(), artist.getArtistInfo(), artist.getArtistBirthD(), artist.getArtistBirthP()});
            }
        } else {
            // Xử lý loại kết quả không xác định (tuỳ thuộc vào yêu cầu của bạn)
            JOptionPane.showMessageDialog(this, "Loại kết quả không xác định.");
        }
    }
    
    private void playVideo() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow >= 0) {
            String youtubeLink = (String) resultTable.getValueAt(selectedRow, 3);
            openWebPage(youtubeLink);
        }
    }
    
    private void openWebPage(String url) {
        Platform.runLater(() -> {
            JFXPanel jfxPanel = new JFXPanel();
            JFrame frame = new JFrame("YouTube Video Player");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.add(jfxPanel);

            WebView webView = new WebView();
            jfxPanel.setScene(new Scene(webView));
            webView.getEngine().load(url);

            frame.setVisible(true);
        });
    }
    
    private void searchSongsByArtist(String searchQuery) {
        try (
            Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

//            String searchType = "artist";
            searchQuery = txtTitle.getText();
            
//            out.writeObject(searchType);
            out.writeObject(searchQuery);

            List<Song> searchResults = (List<Song>) in.readObject();
            displaySearchResults(searchResults);
            socket.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private void searchArtists(String searchQuery) {
        try (
            Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

//            String searchType = "artist";
            searchQuery = txtSearch.getText();
            
//            out.writeObject(searchType);
            out.writeObject(searchQuery);
            
            List<Artist> searchResults = (List<Artist>) in.readObject();
//            displayArtistSearchResultss(searchResults);
            displaySearchResults(searchResults);
            
            socket.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
//    private void displayArtistSearchResults(List<Artist> searchResults) {
//        DefaultTableModel model = (DefaultTableModel) resultTable.getModel();
//        model.setRowCount(0);
//
//        for (Artist artist : searchResults) {
//            model.addRow(new Object[]{artist.getArtistName(), artist.getArtistRName(), artist.getArtistBirthD(), artist.getArtistBirthP(), artist.getArtistInfo()});
//        }
//    }
    
    private void performSearch() {
        String selectedSearchType = (String) btnType.getSelectedItem();
        String searchQuery = txtSearch.getText();

        try (Socket socket = new Socket("localhost", 1234);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(selectedSearchType.toLowerCase()); // Gửi loại tìm kiếm

            // Gửi dữ liệu tìm kiếm (ví dụ: nếu cần)
            out.writeObject(searchQuery);

            // Đọc và xử lý kết quả từ server (ví dụ: nếu cần)
            List<?> searchResults = (List<?>) in.readObject();

            // Hiển thị kết quả trên giao diện người dùng (ví dụ: nếu cần)
            displaySearchResults(searchResults);

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnPlay = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnType = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLyrics = new javax.swing.JTextArea();
        txtArtist = new javax.swing.JTextField();
        txtBD = new javax.swing.JTextField();
        txtBP = new javax.swing.JTextField();
        txtTitle = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        btnSearchOf = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Phần mềm tìm kiếm nhạc");
        setBackground(new java.awt.Color(255, 255, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        btnPlay.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPlay.setText("Play");
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        resultTable.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "", "", "", "", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(resultTable);

        jPanel1.setBackground(new java.awt.Color(153, 204, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("MUSIC LITE");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("______________________________________________________________");

        txtSearch.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        btnSearch.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSearch.setText("Tìm");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnType.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        btnType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nhạc", "Ca sĩ", "Nhạc của ca sĩ" }));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Nhập tên cần tìm:");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Loại tìm kiếm:");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Phần mềm tìm kiếm nhạc và ca sĩ");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setText("v1.0");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setText("______________________________________________________________");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel13.setText("______________________________________________________________");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel8))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(btnType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSearch)
                        .addGap(70, 70, 70))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(37, 37, 37)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 432, Short.MAX_VALUE)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Kết quả tìm kiếm:");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        txtLyrics.setColumns(20);
        txtLyrics.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtLyrics.setRows(5);
        jScrollPane1.setViewportView(txtLyrics);

        txtArtist.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtBD.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtBP.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        txtTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addComponent(txtArtist, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBD, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtBP, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 643, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(61, 61, 61)
                        .addComponent(txtArtist, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
                        .addComponent(txtBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(txtBP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Chi tiết thông tin:");

        btnExit.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnExit.setText("Thoát");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        btnSearchOf.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSearchOf.setText("Nhạc của ca sĩ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel7)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnSearchOf)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnPlay)
                                    .addGap(18, 18, 18)
                                    .addComponent(btnExit))
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 768, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel4)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(btnPlay)
                            .addComponent(btnExit)
                            .addComponent(btnSearchOf))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
//        performSearch();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
        playVideo();
    }//GEN-LAST:event_btnPlayActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_formWindowClosed

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnExitActionPerformed
        
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                JFXPanel jfxPanel = new JFXPanel();
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSearchOf;
    private javax.swing.JComboBox<String> btnType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable resultTable;
    private javax.swing.JTextField txtArtist;
    private javax.swing.JTextField txtBD;
    private javax.swing.JTextField txtBP;
    private javax.swing.JTextArea txtLyrics;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTitle;
    // End of variables declaration//GEN-END:variables
}
