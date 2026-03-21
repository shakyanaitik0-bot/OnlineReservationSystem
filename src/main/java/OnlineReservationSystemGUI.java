import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OnlineReservationSystemGUI extends JFrame {

    static class Train {
        int trainNo;
        String name;
        String source;
        String destination;
        int totalSeats;
        int availableSeats;

        Train(int trainNo, String name, String source, String destination, int totalSeats) {
            this.trainNo = trainNo;
            this.name = name;
            this.source = source;
            this.destination = destination;
            this.totalSeats = totalSeats;
            this.availableSeats = totalSeats;
        }
    }

    static class Booking {
        static int idCounter = 1000;
        int bookingId;
        String passengerName;
        int age;
        int trainNo;
        int seatsBooked;

        Booking(String passengerName, int age, int trainNo, int seatsBooked) {
            this.bookingId = idCounter++;
            this.passengerName = passengerName;
            this.age = age;
            this.trainNo = trainNo;
            this.seatsBooked = seatsBooked;
        }
    }

    private final Map<Integer, Train> trains = new LinkedHashMap<>();
    private final java.util.List<Booking> bookings = new ArrayList<>();

    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    private final JTextField userField = new JTextField(12);
    private final JPasswordField passField = new JPasswordField(12);

    private final DefaultTableModel trainTableModel = new DefaultTableModel(
        new Object[]{"Train No","Name","Source","Destination","Available/Total"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable trainTable = new JTable(trainTableModel);

    private final DefaultTableModel bookingTableModel = new DefaultTableModel(
        new Object[]{"Booking ID","Name","Age","Train No","Seats"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable bookingTable = new JTable(bookingTableModel);

    private final String adminUser = "admin";
    private final String adminPass = "123";
    private final String userUser = "user";
    private final String userPass = "123";

    private String currentRole = null;

    public OnlineReservationSystemGUI() {
        super("Online Reservation System - GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        initData();
        initUI();
    }

    private void initData() {
        addTrainModel(new Train(101, "Shatabdi Express", "Delhi", "Kanpur", 50));
        addTrainModel(new Train(102, "Rajdhani Express", "Delhi", "Mumbai", 80));
        addTrainModel(new Train(103, "Intercity Express", "Lucknow", "Delhi", 60));
    }

    private void initUI() {
        JMenuBar menuBar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem logout = new JMenuItem("Logout");
        logout.addActionListener(e -> showLogin());
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        options.add(logout);
        options.add(exit);
        menuBar.add(options);
        setJMenuBar(menuBar);

        cardPanel.add(loginPanel(), "LOGIN");
        cardPanel.add(adminPanel(), "ADMIN");
        cardPanel.add(userPanel(), "USER");

        add(cardPanel);
        showLogin();
    }

    private JPanel loginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0; c.gridy = 0; p.add(new JLabel("Username:"), c);
        c.gridx = 1; p.add(userField, c);
        c.gridx = 0; c.gridy = 1; p.add(new JLabel("Password:"), c);
        c.gridx = 1; p.add(passField, c);

        JButton loginBtn = new JButton("Login");
        JButton guestBtn = new JButton("Continue as Guest");
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        JPanel btns = new JPanel();
        btns.add(loginBtn);
        btns.add(guestBtn);
        p.add(btns, c);

        JLabel note = new JLabel("Admin: admin/123  |  User: user/123");
        c.gridy = 3;
        p.add(note, c);

        loginBtn.addActionListener(e -> doLogin());
        guestBtn.addActionListener(e -> { currentRole = "USER"; cards.show(cardPanel, "USER"); });

        return p;
    }

    private JPanel adminPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.6);

        JPanel left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createTitledBorder("Trains"));
        left.add(new JScrollPane(trainTable), BorderLayout.CENTER);

        JPanel trainBtns = new JPanel();
        JButton addTrain = new JButton("Add Train");
        JButton editTrain = new JButton("Edit Train");
        JButton removeTrain = new JButton("Remove Train");
        trainBtns.add(addTrain);
        trainBtns.add(editTrain);
        trainBtns.add(removeTrain);
        left.add(trainBtns, BorderLayout.SOUTH);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Bookings"));
        right.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel bookBtns = new JPanel();
        JButton viewReport = new JButton("Report");
        JButton cancelBooking = new JButton("Cancel Booking");
        bookBtns.add(viewReport);
        bookBtns.add(cancelBooking);
        right.add(bookBtns, BorderLayout.SOUTH);

        split.setLeftComponent(left);
        split.setRightComponent(right);
        p.add(split, BorderLayout.CENTER);

        addTrain.addActionListener(e -> showAddTrainDialog());
        editTrain.addActionListener(e -> showEditTrainDialog());
        removeTrain.addActionListener(e -> removeSelectedTrain());
        cancelBooking.addActionListener(e -> adminCancelBooking());
        viewReport.addActionListener(e -> showReportDialog());

        return p;
    }

    private JPanel userPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel("USER DASHBOARD", SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Available Trains"));
        center.add(new JScrollPane(trainTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton bookBtn = new JButton("Book Ticket");
        JButton cancelBtn = new JButton("Cancel My Booking");
        JButton myBookingsBtn = new JButton("My Bookings (All)");
        bottom.add(bookBtn);
        bottom.add(cancelBtn);
        bottom.add(myBookingsBtn);

        p.add(center, BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        bookBtn.addActionListener(e -> showBookingDialog());
        cancelBtn.addActionListener(e -> userCancelBooking());
        myBookingsBtn.addActionListener(e -> showBookingsDialog());

        return p;
    }

    private void doLogin() {
        String u = userField.getText().trim();
        String p = new String(passField.getPassword());
        if (u.equals(adminUser) && p.equals(adminPass)) {
            currentRole = "ADMIN";
            cards.show(cardPanel, "ADMIN");
        } else if (u.equals(userUser) && p.equals(userPass)) {
            currentRole = "USER";
            cards.show(cardPanel, "USER");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
        userField.setText("");
        passField.setText("");
    }

    private void showLogin() {
        currentRole = null;
        cards.show(cardPanel, "LOGIN");
    }

    private void addTrainModel(Train t) {
        trains.put(t.trainNo, t);
        refreshTrainTable();
    }

    private void refreshTrainTable() {
        SwingUtilities.invokeLater(() -> {
            trainTableModel.setRowCount(0);
            for (Train t : trains.values()) {
                trainTableModel.addRow(new Object[]{
                    t.trainNo, t.name, t.source, t.destination,
                    t.availableSeats + "/" + t.totalSeats
                });
            }
        });
    }

    private void refreshBookingTable() {
        SwingUtilities.invokeLater(() -> {
            bookingTableModel.setRowCount(0);
            for (Booking b : bookings) {
                bookingTableModel.addRow(new Object[]{
                    b.bookingId, b.passengerName, b.age, b.trainNo, b.seatsBooked
                });
            }
        });
    }

    private void showAddTrainDialog() {
        JTextField no = new JTextField();
        JTextField name = new JTextField();
        JTextField src = new JTextField();
        JTextField dest = new JTextField();
        JTextField seats = new JTextField();

        Object[] fields = {"Train No:", no, "Name:", name, "Source:", src, "Destination:", dest, "Total Seats:", seats};
        int res = JOptionPane.showConfirmDialog(this, fields, "Add Train", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int tno = Integer.parseInt(no.getText().trim());
                int s = Integer.parseInt(seats.getText().trim());
                if (trains.containsKey(tno)) {
                    JOptionPane.showMessageDialog(this, "Train number already exists.");
                    return;
                }
                addTrainModel(new Train(tno, name.getText().trim(), src.getText().trim(), dest.getText().trim(), s));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void showEditTrainDialog() {
        int r = trainTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a train to edit"); return; }
        int tno = (int) trainTableModel.getValueAt(r, 0);
        Train t = trains.get(tno);
        if (t == null) return;

        JTextField name = new JTextField(t.name);
        JTextField src = new JTextField(t.source);
        JTextField dest = new JTextField(t.destination);
        JTextField seats = new JTextField(String.valueOf(t.totalSeats));

        Object[] fields = {"Name:", name, "Source:", src, "Destination:", dest, "Total Seats:", seats};
        int res = JOptionPane.showConfirmDialog(this, fields, "Edit Train " + tno, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int s = Integer.parseInt(seats.getText().trim());
                t.name = name.getText().trim();
                t.source = src.getText().trim();
                t.destination = dest.getText().trim();
                int booked = t.totalSeats - t.availableSeats;
                t.totalSeats = s;
                t.availableSeats = Math.max(0, s - booked);
                refreshTrainTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void removeSelectedTrain() {
        int r = trainTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a train to remove"); return; }
        int tno = (int) trainTableModel.getValueAt(r, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Remove train " + tno + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            trains.remove(tno);
            bookings.removeIf(b -> b.trainNo == tno);
            refreshTrainTable();
            refreshBookingTable();
        }
    }

    private void showBookingDialog() {
        int r = trainTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a train to book"); return; }
        int tno = (int) trainTableModel.getValueAt(r, 0);
        Train t = trains.get(tno);
        if (t == null) { JOptionPane.showMessageDialog(this, "Train not found"); return; }

        JTextField name = new JTextField();
        JTextField age = new JTextField();
        JTextField seats = new JTextField();
        Object[] fields = {
            "Train: " + t.trainNo + " - " + t.name + " (Available: " + t.availableSeats + ")", "",
            "Passenger Name:", name, "Age:", age, "Seats to book:", seats
        };
        int res = JOptionPane.showConfirmDialog(this, fields, "Book Ticket", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                String pname = name.getText().trim();
                int a = Integer.parseInt(age.getText().trim());
                int s = Integer.parseInt(seats.getText().trim());
                if (s <= 0) { JOptionPane.showMessageDialog(this, "Enter seats > 0"); return; }
                if (s > t.availableSeats) { JOptionPane.showMessageDialog(this, "Not enough seats available"); return; }
                Booking b = new Booking(pname, a, tno, s);
                bookings.add(b);
                t.availableSeats -= s;
                refreshTrainTable();
                refreshBookingTable();
                JOptionPane.showMessageDialog(this, "Booking successful! ID: " + b.bookingId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
            }
        }
    }

    private void showBookingsDialog() {
        refreshBookingTable();
        JOptionPane.showMessageDialog(this, new JScrollPane(bookingTable), "All Bookings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adminCancelBooking() {
        int r = bookingTable.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a booking to cancel"); return; }
        int bid = (int) bookingTableModel.getValueAt(r, 0);
        cancelBookingById(bid);
    }

    private void userCancelBooking() {
        String s = JOptionPane.showInputDialog(this, "Enter Booking ID to cancel:");
        if (s == null) return;
        try { int bid = Integer.parseInt(s.trim()); cancelBookingById(bid); }
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Invalid ID"); }
    }

    private void cancelBookingById(int bookingId) {
        Booking found = null;
        for (Booking b : bookings) if (b.bookingId == bookingId) { found = b; break; }
        if (found == null) { JOptionPane.showMessageDialog(this, "Booking ID not found"); return; }
        Train t = trains.get(found.trainNo);
        if (t != null) t.availableSeats += found.seatsBooked;
        bookings.remove(found);
        refreshTrainTable();
        refreshBookingTable();
        JOptionPane.showMessageDialog(this, "Booking cancelled: ID " + bookingId);
    }

    private void showReportDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Trains Report ---\n");
        for (Train t : trains.values()) {
            sb.append(String.format("%d | %s | %s->%s | %d/%d available\n",
                t.trainNo, t.name, t.source, t.destination, t.availableSeats, t.totalSeats));
        }
        sb.append("\n--- Bookings ---\n");
        for (Booking b : bookings) {
            sb.append(String.format("ID:%d Name:%s Train:%d Seats:%d\n",
                b.bookingId, b.passengerName, b.trainNo, b.seatsBooked));
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OnlineReservationSystemGUI app = new OnlineReservationSystemGUI();
            app.setVisible(true);
        });
    }
}