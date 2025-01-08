package Gui.Admin;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import DSA.Admin.BorrowingHistory;
import DSA.Admin.Borrowed_requests.BorrowRequest;

public class ReportsDashboard extends JPanel {
    private final JTable borrowHistoryTable;
    private final DefaultTableModel borrowHistoryModel;
    private final JTable activityLogsTable;
    private final DefaultTableModel activityLogsModel;
    private final JComboBox<String> statusFilter;
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);

    public ReportsDashboard() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tabbed pane for different views
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Serif", Font.PLAIN, 14));

        // Create Borrowing History panel
        JPanel borrowingHistoryPanel = new JPanel(new BorderLayout(10, 10));
        borrowingHistoryPanel.setBackground(Color.WHITE);

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);

        JLabel filterLabel = new JLabel("Filter by Status:");
        statusFilter = new JComboBox<>(new String[]{"All", "Borrowed", "Returned"});
        JButton refreshButton = createStyledButton("↻ Refresh");

        filterPanel.add(filterLabel);
        filterPanel.add(statusFilter);
        filterPanel.add(refreshButton);

        // Create Borrowing History table
        String[] borrowColumns = {"ID", "User Name", "Book Title", "Author", "Copies", "Status"};
        borrowHistoryModel = new DefaultTableModel(borrowColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        borrowHistoryTable = new JTable(borrowHistoryModel);
        configureTable(borrowHistoryTable);

        // Create Activity Logs table
        String[] logColumns = {"Time", "User", "Action", "Details"};
        activityLogsModel = new DefaultTableModel(logColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        activityLogsTable = new JTable(activityLogsModel);
        configureTable(activityLogsTable);

        // Add components to panels
        borrowingHistoryPanel.add(filterPanel, BorderLayout.NORTH);
        borrowingHistoryPanel.add(new JScrollPane(borrowHistoryTable), BorderLayout.CENTER);

        JPanel activityLogsPanel = new JPanel(new BorderLayout());
        activityLogsPanel.setBackground(Color.WHITE);
        activityLogsPanel.add(new JScrollPane(activityLogsTable), BorderLayout.CENTER);

        // Add panels to tabbed pane
        tabbedPane.addTab("Borrowing History", borrowingHistoryPanel);
        tabbedPane.addTab("Activity Logs", activityLogsPanel);

        // Add tabbed pane to main panel
        add(tabbedPane, BorderLayout.CENTER);

        // Add listeners
        statusFilter.addActionListener(e -> filterBorrowingHistory());
        refreshButton.addActionListener(e -> refreshData());

        // Initial data load
        refreshData();
    }

    private void configureTable(JTable table) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(DARK_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFont(new Font("Serif", Font.PLAIN, 14));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(DARK_GREEN);
        button.setFocusPainted(false);
        button.setFont(new Font("Serif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_GREEN),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    private void refreshData() {
        refreshBorrowingHistory();
        refreshActivityLogs();
    }

    private void refreshBorrowingHistory() {
        borrowHistoryModel.setRowCount(0);
        List<BorrowRequest> historyList = BorrowingHistory.LoadAllHistory();

        for (BorrowRequest request : historyList) {
            Object[] row = new Object[]{
                    request.getId(),
                    request.getUser(),
                    request.getTitle(),
                    request.getAuthor(),
                    request.getCopies(),
                    request.getStatus()
            };
            borrowHistoryModel.addRow(row);
        }
    }

    private void refreshActivityLogs() {
        activityLogsModel.setRowCount(0);
        Object[] row1 = new Object[]{"2025-01-01 10:00", "John Doe", "Login", "User logged in successfully"};
        Object[] row2 = new Object[]{"2025-01-01 10:15", "John Doe", "Borrow", "Borrowed 'The Great Gatsby'"};
        activityLogsModel.addRow(row1);
        activityLogsModel.addRow(row2);
    }

    private void filterBorrowingHistory() {
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (selectedStatus.equals("All")) {
            refreshBorrowingHistory();
            return;
        }

        borrowHistoryModel.setRowCount(0);
        List<BorrowRequest> filteredHistory = BorrowingHistory.LoadHistoryByStatus(selectedStatus);

        for (BorrowRequest request : filteredHistory) {
            Object[] row = new Object[]{
                    request.getId(),
                    request.getUser(),
                    request.getTitle(),
                    request.getAuthor(),
                    request.getCopies(),
                    request.getStatus()
            };
            borrowHistoryModel.addRow(row);
        }
    }

    public void addActivityLog(String time, String user, String action, String details) {
        Object[] row = new Object[]{time, user, action, details};
        activityLogsModel.addRow(row);
    }
}