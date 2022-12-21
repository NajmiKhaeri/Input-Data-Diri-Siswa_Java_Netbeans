/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ptspbo2;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author NajmiKAP
 */
public class FormMenu extends javax.swing.JFrame {

    private String level;
    private String filename;
    Connection con;
    DefaultTableModel tableModel;

    /**
     * Creates new form menu
     */
    public FormMenu() {
        initComponents();
        //ambil ukuran layar
        Dimension layar = Toolkit.getDefaultToolkit().getScreenSize();

        //buat posisi tengah
        int x = layar.width / 2 - this.getSize().width / 2;
        int y = layar.height / 2 - this.getSize().height / 2;

        // set  lokasi JFrame
        this.setLocation(x, y);

        con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1/biodata", "root", "");

        } catch (Exception e) {
            System.out.println("ERROR KONEKSI KE DATABASE:\n" + e);
        }

        refreshTable();
        tampilDataKelas();

    }

    public void setLevel(String l) {
        level = l;
        if (l.equals("admin")) {
            user.setText(l);
        } else {
            user.setText(l);
        }
    }
    

    private void tampilDataKelas() {
        String sqlCari = "SELECT kelas FROM kelas";
        cmbKelas.addItem("-Pilih Kelas-");
        try {
            PreparedStatement ps = con.prepareStatement(sqlCari);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String output = rs.getString(1);
                cmbKelas.addItem(output);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Salah di method tampil data kelas");
        }
    }

    private void refreshTable() {
        tableModel = new DefaultTableModel(null, new Object[]{"NIS", "NAMA", "KELAS", "JENIS KELAMIN", "ALAMAT", "NO HP", "TANGGAL LAHIR", "FOTO"});
        tableData.setModel(tableModel);
        tableModel.getDataVector().removeAllElements();
        String searchnama = search.getText();
        if (!searchnama.equals("")) {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM siswa WHERE nama LIKE ?");
                ps.setString(1, "%" + searchnama + "%");
                ResultSet r = ps.executeQuery();
                while (r.next()) {
                    Object[] data = {
                        r.getString(1),
                        r.getString(2),
                        r.getString(3),
                        r.getString(4),
                        r.getString(5),
                        r.getString(6),
                        r.getString(7),
                        r.getString(8)
                    };
                    tableModel.addRow(data);
                }
            } catch (Exception e) {
                System.out.println("ERROR KUERI KE DATABASE:\n" + e + "\n\n");
            }
        } else {
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM siswa");
                ResultSet r = ps.executeQuery();
                while (r.next()) {
                    Object[] data = {
                        r.getString(1),
                        r.getString(2),
                        r.getString(3),
                        r.getString(4),
                        r.getString(5),
                        r.getString(6),
                        r.getString(7),
                        r.getString(8)
                    };
                    tableModel.addRow(data);
                }
            } catch (Exception e) {
                System.out.println("ERROR KUERI KE DATABASE:\n" + e + "\n\n");
            }
        }
    }

    public void tambahData() {
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO siswa VALUES (?,?,?,?,?,?,?,?)");
            String jk;
            String newpath = "src/upload";
            File directory = new File(newpath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File fileawal = null;
            File fileakhir = null;
            String ext = this.filename.substring(filename.lastIndexOf('.'));
            fileawal = new File(filename);
            fileakhir = new File(newpath + "/" + nis.getText() + ext);
            Files.copy(fileawal.toPath(), fileakhir.toPath());
            ps.setString(1, nis.getText());
            ps.setString(2, nama.getText());
            ps.setString(3, cmbKelas.getSelectedItem().toString());
            ps.setString(4, rdlaki.getText());
            ps.setString(5, rdcewe.getText());
            ps.setString(6, alamat.getText());
            ps.setString(7, hp.getText());
            ps.setString(8, tanggal.getDateFormatString());
            String kelas = cmbKelas.getSelectedItem().toString();
            if (kelas.equals("x")) {
                kelas = "1";
                ps.setString(3, kelas);
            } else if (kelas.equals("xi")) {
                kelas = "2";
                ps.setString(3, kelas);
            } else if (kelas.equals("xii")) {
                kelas = "3";
                ps.setString(3, kelas);
            }
            jk = "";
            if (rdlaki.isSelected()) {
                jk = "Laki - laki";
                ps.setString(4, jk);
            } else if (rdcewe.isSelected()) {
                jk = "Perempuan";
                ps.setString(4, jk);
            }
            ps.setString(5, alamat.getText());
            ps.setString(6, hp.getText());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(tanggal.getDate());
                ps.setString(7, date);
                ps.setString(8, fileakhir.toString());
                JOptionPane.showMessageDialog(null, "Data Berhasil di tambahkan");

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Salah di method tambahData()");
            }
            ps.executeUpdate();

            refreshTable();
            nis.setText("");
            nama.setText("");
            cmbKelas.setSelectedIndex(0);
            buttonGroup1.clearSelection();
            alamat.setText("");
            hp.setText("");
            tanggal.setDate(null);
            gambar.setIcon(null);
            link.setText("");
            this.filename = null;
        } catch (Exception e) {
            System.out.print("ERROR KUERI INSERT KE DATA BASE: \n" + e + "\n\n");
        }
    }

    public void ubahData() {
        if (this.filename != null) {
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE siswa SET nis=?, nama=?, kelas=?, jk=?, alamat=?, no_hp=?, tanggal_lahir=?, foto=? WHERE nis=?");
                String kelamin;
                String newpath = "src/upload";
                File directory = new File(newpath);
                if (!directory.exists()) {
                    directory.mkdirs();
                    
                }
                File fileawal = null;
                File fileakhir = null;
                String ext = this.filename.substring(filename.lastIndexOf('.'));
                fileawal = new File(filename);
                fileakhir = new File(newpath + "/" + nis.getText() + ext);
                if (fileakhir.exists()) {
                    fileakhir.delete();
                }
                Files.copy(fileawal.toPath(), fileakhir.toPath());
                ps.setString(1, nis.getText());
                ps.setString(2, nama.getText());
                ps.setString(3, cmbKelas.getSelectedItem().toString());
                ps.setString(4, rdlaki.getText());
                ps.setString(5, rdcewe.getText());
                ps.setString(6, alamat.getText());
                ps.setString(7, hp.getText());
                ps.setString(8, tanggal.getDateFormatString());
                String kelas = cmbKelas.getSelectedItem().toString();
                if (kelas.equals("x")) {
                    kelas = "1";
                    ps.setString(3, kelas);
                } else if (kelas.equals("xi")) {
                    kelas = "2";
                    ps.setString(3, kelas);
                } else if (kelas.equals("xii")) {
                    kelas = "3";
                    ps.setString(3, kelas);
                }
                kelamin = "";
                if (rdlaki.isSelected()) {
                    kelamin = "Laki - laki";
                    ps.setString(4, kelamin);
                } else if (rdcewe.isSelected()) {
                    kelamin = "Perempuan";
                    ps.setString(4, kelamin);
                }
                ps.setString(5, alamat.getText());
                ps.setString(6, hp.getText());
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String date = sdf.format(tanggal.getDate());
                    ps.setString(7, date);
                    ps.setString(8, fileakhir.toString());
                    ps.setString(9, nis.getText());
                    JOptionPane.showMessageDialog(null, "Data Berhasil di update");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Salah di method ubahData()" + e);
                }
                ps.executeUpdate();

                refreshTable();
                nis.setText("");
                nama.setText("");
                cmbKelas.setSelectedIndex(0);
                buttonGroup1.clearSelection();
                alamat.setText("");
                hp.setText("");
                tanggal.setDate(null);
                gambar.setIcon(null);
                link.setText("");
                this.filename = null;
            } catch (Exception e) {
                System.out.print("ERROR KUERI UPDATE KE DATA BASE: \n" + e + "\n\n");
            }
        } else {
            try {
                PreparedStatement ps = con.prepareStatement("UPDATE siswa SET nis=?, nama=?, kelas=?, jk=?,  alamat=?, no_hp=?, tanggal_lahir=? WHERE nis=?");
                String kelamin;
                ps.setString(1, nis.getText());
                ps.setString(2, nama.getText());
                ps.setString(3, cmbKelas.getSelectedItem().toString());
                ps.setString(4, rdlaki.getText());
                ps.setString(5, rdcewe.getText());
                ps.setString(6, alamat.getText());
                ps.setString(7, hp.getText());
                ps.setString(8, tanggal.getDateFormatString());
                String kelas = cmbKelas.getSelectedItem().toString();
                if (kelas.equals("x")) {
                    kelas = "1";
                    ps.setString(3, kelas);
                } else if (kelas.equals("xi")) {
                    kelas = "2";
                    ps.setString(3, kelas);
                } else if (kelas.equals("xii")) {
                    kelas = "3";
                    ps.setString(3, kelas);
                }
                kelamin = "";
                if (rdlaki.isSelected()) {
                    kelamin = "Laki - laki";
                    ps.setString(4, kelamin);
                } else if (rdcewe.isSelected()) {
                    kelamin = "Perempuan";
                    ps.setString(4, kelamin);
                }
                ps.setString(5, alamat.getText());
                ps.setString(6, hp.getText());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(tanggal.getDate());
                ps.setString(7, date);
                ps.setString(8, nis.getText());
                JOptionPane.showMessageDialog(null, "Data Berhasil di update");
                ps.executeUpdate();

                refreshTable();
                nis.setText("");
                nama.setText("");
                cmbKelas.setSelectedIndex(0);
                buttonGroup1.clearSelection();
                alamat.setText("");
                hp.setText("");
                tanggal.setDate(null);
                gambar.setIcon(null);
                link.setText("");
                this.filename = null;
            } catch (Exception e) {
                System.out.print("ERROR KUERI UPDATE KE DATA BASE: \n" + e + "\n\n");
            }
        }

    }

    public void cariData() {

    }

    public void hapusData() {
        try {
            PreparedStatement ps = con.prepareStatement("DELETE FROM siswa WHERE nis=?");
            ps.setString(1, nis.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data Berhasil Dihapus");
            File file = new File(link.getText());
            file.delete();
            refreshTable();
            nis.setText("");
            nama.setText("");
            cmbKelas.setSelectedIndex(0);
            buttonGroup1.clearSelection();
            alamat.setText("");
            hp.setText("");
            tanggal.setDate(null);
            gambar.setIcon(null);
            link.setText("");
            this.filename = "";
        } catch (Exception e) {
            System.out.print("ERROR KUERI DELETE KE DATA BASE: \n" + e + "\n\n");
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableData = new javax.swing.JTable();
        cari = new javax.swing.JButton();
        search = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        nis = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        nama = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cmbKelas = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        rdlaki = new javax.swing.JRadioButton();
        rdcewe = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        alamat = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        hp = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tanggal = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        foto = new javax.swing.JButton();
        gambar = new javax.swing.JLabel();
        link = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        user = new javax.swing.JMenu();
        logout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 0, 204));

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(102, 0, 102));

        tableData.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        tableData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableDataMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tableData);

        cari.setText("RESET");
        cari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariActionPerformed(evt);
            }
        });

        search.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        search.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        search.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchKeyReleased(evt);
            }
        });

        jButton1.setText("HAPUS");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("UBAH");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("TAMBAH");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 22)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 255, 204));
        jLabel1.setText("SEARCH");

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));

        jLabel11.setFont(new java.awt.Font("Serif", 1, 27)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 153));
        jLabel11.setText("TABEL HASIL DATA");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(186, 186, 186)
                .addComponent(jLabel11)
                .addContainerGap(211, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addComponent(jButton3)
                        .addGap(110, 110, 110)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(112, 112, 112)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(701, 701, 701))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(242, 242, 242)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cari))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cari))
                        .addGap(23, 23, 23)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(415, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab("Data Diri", jPanel2);

        jLabel12.setFont(new java.awt.Font("Serif", 1, 27)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 153));
        jLabel12.setText("SILAHKAN INPUT DATA");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("NIS");

        nis.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        nis.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("NAMA");

        nama.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        nama.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("KELAS");

        cmbKelas.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("JENIS KELAMIN");

        buttonGroup1.add(rdlaki);
        rdlaki.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        rdlaki.setText("L");

        rdcewe.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rdcewe);
        rdcewe.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        rdcewe.setText("P");
        rdcewe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdceweActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("ALAMAT");

        alamat.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        alamat.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("NO HP");

        hp.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        hp.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        hp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                hpKeyPressed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("TANGGAL LAHIR");

        tanggal.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 19)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("FOTO");

        foto.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        foto.setText("Pilih Gambar");
        foto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fotoActionPerformed(evt);
            }
        });

        gambar.setBackground(new java.awt.Color(204, 204, 204));
        gambar.setForeground(new java.awt.Color(204, 255, 0));
        gambar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 102, 0)));

        link.setEditable(false);
        link.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jPanel3.setBackground(new java.awt.Color(51, 0, 51));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 0));
        jLabel2.setText("User Login :");

        Luser.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Luser.setForeground(new java.awt.Color(255, 255, 255));
        Luser.setText("User");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("USERNAME :");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("EMAIL        :");

        Lemail.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        Lemail.setForeground(new java.awt.Color(255, 255, 255));
        Lemail.setText("Email");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 255, 0));
        jLabel15.setText("Status Aktif");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel15))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Luser))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Lemail))
                            .addComponent(jLabel2))
                        .addGap(0, 74, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Luser)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(Lemail))
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setText("©2022. NajmiKhaeriArrisaPutra");

        jButton4.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        jButton4.setText("Logout");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(nis)
                                    .addComponent(nama)
                                    .addComponent(cmbKelas, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(rdlaki, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(rdcewe, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addGap(79, 79, 79)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(hp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(744, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(foto)
                                .addGap(18, 18, 18)
                                .addComponent(gambar, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(link, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(jButton4)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(140, 140, 140)
                                .addComponent(jLabel3))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(68, 68, 68)
                                .addComponent(jLabel12)
                                .addGap(40, 40, 40)
                                .addComponent(nis, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(nama, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(cmbKelas, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(rdlaki)
                            .addComponent(rdcewe))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(alamat, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(hp, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(tanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(jLabel10))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gambar, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(foto, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(link, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(105, 105, 105)
                        .addComponent(jLabel16))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 611, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        link.getAccessibleContext().setAccessibleName("");

        user.setText("user");
        user.setFont(new java.awt.Font("Segoe UI", 0, 21)); // NOI18N

        logout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        logout.setFont(new java.awt.Font("Segoe UI", 0, 19)); // NOI18N
        logout.setText("Logout");
        logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutActionPerformed(evt);
            }
        });
        user.add(logout);

        jMenuBar1.add(user);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutActionPerformed
        // TODO add your handling code here:
        FormLogin log = new FormLogin();
        log.setVisible(true);
        this.dispose();
        JOptionPane.showMessageDialog(this,"Terimakasih :)");
    }//GEN-LAST:event_logoutActionPerformed

    private void rdceweActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdceweActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rdceweActionPerformed

    private void fotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fotoActionPerformed
        // TODO add your handling code here:
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            ImageIcon icon = new ImageIcon(f.toString());
            Image img = icon.getImage().getScaledInstance(gambar.getWidth(), gambar.getHeight(), Image.SCALE_DEFAULT);
            ImageIcon ic = new ImageIcon(img);
            gambar.setIcon(ic);
            this.filename = f.getAbsolutePath();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Anda Belum Memilih Gambar");
        }
    }//GEN-LAST:event_fotoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        ubahData();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void tableDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableDataMouseClicked
        // TODO add your handling code here:
        nis.setText(tableModel.getValueAt(tableData.getSelectedRow(), 0).toString());
        nama.setText(tableModel.getValueAt(tableData.getSelectedRow(), 1).toString());
        String kelas = tableModel.getValueAt(tableData.getSelectedRow(), 2).toString();
        if (kelas.equals("1")) {
            cmbKelas.setSelectedIndex(1);
        } else if (kelas.equals("2")) {
            cmbKelas.setSelectedIndex(2);
        } else if (kelas.equals("3")) {
            cmbKelas.setSelectedIndex(3);
        } else {
            cmbKelas.setSelectedIndex(0);
        }
        String kelamin = tableModel.getValueAt(tableData.getSelectedRow(), 3).toString();
        if (kelamin.equals("Laki - laki")) {
            rdlaki.setSelected(true);
        } else if (kelamin.equals("Perempuan")) {
            rdcewe.setSelected(true);
        } else {
            buttonGroup1.clearSelection();
        }
        alamat.setText(tableModel.getValueAt(tableData.getSelectedRow(), 4).toString());
        hp.setText(tableModel.getValueAt(tableData.getSelectedRow(), 5).toString());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse((String) tableData.getValueAt(tableData.getSelectedRow(), 6));

            tanggal.setDate(date);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
        link.setText(tableModel.getValueAt(tableData.getSelectedRow(), 7).toString());
        File file = new File(tableModel.getValueAt(tableData.getSelectedRow(), 7).toString());
        ImageIcon icon = new ImageIcon(file.toString());
        Image img = icon.getImage().getScaledInstance(gambar.getWidth(), gambar.getHeight(), Image.SCALE_DEFAULT);
        ImageIcon c = new ImageIcon(img);
        gambar.setIcon(c);

    }//GEN-LAST:event_tableDataMouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        tambahData();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        hapusData();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void hpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hpKeyPressed
        // TODO add your handling code here:
        char c = evt.getKeyChar();
        if (Character.isLetter(c)) {
            JOptionPane.showMessageDialog(this, "Masukkan angka saja");
            hp.setText("");
        } else {
            hp.setEditable(true);
        }
    }//GEN-LAST:event_hpKeyPressed

    private void cariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariActionPerformed
        // TODO add your handling code here:
        search.setText("");
        refreshTable();
    }//GEN-LAST:event_cariActionPerformed

    private void searchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchKeyReleased
        // TODO add your handling code here:
        refreshTable();
    }//GEN-LAST:event_searchKeyReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(FormMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FormMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FormMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FormMenu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FormMenu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static final javax.swing.JLabel Lemail = new javax.swing.JLabel();
    public static final javax.swing.JLabel Luser = new javax.swing.JLabel();
    private javax.swing.JTextField alamat;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cari;
    private javax.swing.JComboBox<String> cmbKelas;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton foto;
    private javax.swing.JLabel gambar;
    private javax.swing.JTextField hp;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField link;
    private javax.swing.JMenuItem logout;
    private javax.swing.JTextField nama;
    private javax.swing.JTextField nis;
    private javax.swing.JRadioButton rdcewe;
    private javax.swing.JRadioButton rdlaki;
    private javax.swing.JTextField search;
    private javax.swing.JTable tableData;
    private com.toedter.calendar.JDateChooser tanggal;
    private javax.swing.JMenu user;
    // End of variables declaration//GEN-END:variables
}
