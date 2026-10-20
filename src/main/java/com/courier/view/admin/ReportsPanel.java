
package com.courier.view.admin;

import com.courier.controller.ReportsController;
import com.courier.dao.ReportsDAO;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ReportsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Color GREEN = new Color(83, 108, 43);
    private static final Color BORDER = new Color(225, 219, 208);
    private static final Color MUTED = new Color(112, 119, 130);
    private static final Color WHITE = Color.WHITE;

    private final ReportsController controller = new ReportsController();

    private final JComboBox<String> reportSelector = new JComboBox<>(
        new String[] {
            "Monthly Shipments",
            "Delivery Performance",
            "Shipment Status Summary",
            "Agent Performance"
        }
    );

    private final JButton generateButton = new JButton("Generate Report");
    private final JButton exportButton = new JButton("Export CSV");

    private final JLabel totalValue = new JLabel("—");
    private final JLabel deliveredValue = new JLabel("—");
    private final JLabel transitValue = new JLabel("—");
    private final JLabel pendingValue = new JLabel("—");
    private final JLabel message = new JLabel("Ready to generate reports.");

    private final JPanel chartPanel = new JPanel(new BorderLayout());
    private final DefaultTableModel tableModel =
        new DefaultTableModel();

    private final JTable reportTable = new JTable(tableModel);

    private List<ReportsDAO.ReportRow> currentRows = List.of();
    private String[] currentColumns = new String[0];

    public ReportsPanel() {
        setLayout(new BorderLayout(0, 18));
        setBackground(AppTheme.BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(createHeader(), BorderLayout.NORTH);
        JScrollPane pageScroll = new JScrollPane(createMain());
        pageScroll.setBorder(null);
        pageScroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        pageScroll.getViewport().setBackground(AppTheme.BACKGROUND);

        add(pageScroll, BorderLayout.CENTER);

        generateButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(e -> exportCsv());
        reportSelector.addActionListener(e -> {
            // The selected report changes only when Generate is clicked.
        });

        loadDashboard();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(FontManager.semiBold(27));
        title.setForeground(AppTheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
            "Monitor shipment and delivery performance"
        );
        subtitle.setFont(FontManager.regular(13));
        subtitle.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    private JPanel createMain() {
        JPanel main = new JPanel();
        main.setOpaque(false);
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

        JPanel controls = new JPanel(new FlowLayout(
            FlowLayout.LEFT, 12, 22));
        controls.setBackground(WHITE);
        controls.setBorder(new LineBorder(BORDER, 1));
        controls.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel reportLabel = new JLabel("Report:");
        reportLabel.setFont(FontManager.semiBold(13));

        reportSelector.setFont(FontManager.regular(13));
        reportSelector.setPreferredSize(new Dimension(220, 40));

        styleButton(generateButton, GREEN);
        styleButton(exportButton, WHITE);
        exportButton.setForeground(AppTheme.TEXT_PRIMARY);
        exportButton.setBorder(new LineBorder(BORDER, 1));

        controls.add(reportLabel);
        controls.add(reportSelector);
        controls.add(generateButton);
        controls.add(exportButton);

        message.setFont(FontManager.regular(12));
        message.setForeground(MUTED);
        controls.add(message);

        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.setPreferredSize(new Dimension(900, 135));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 145));

        cards.add(createMetricCard("Total Shipments", totalValue));
        cards.add(createMetricCard("Delivered", deliveredValue));
        cards.add(createMetricCard("In Transit", transitValue));
        cards.add(createMetricCard("Pending", pendingValue));

        JPanel chartCard = new JPanel(new BorderLayout(0, 12));
        chartCard.setBackground(WHITE);
        chartCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(18, 22, 18, 22)
        ));
        chartCard.setPreferredSize(new Dimension(900, 300));
        chartCard.setMinimumSize(new Dimension(500, 260));
        chartCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        JLabel chartTitle = new JLabel("Monthly Shipment Performance");
        chartTitle.setFont(FontManager.semiBold(17));
        chartTitle.setForeground(AppTheme.TEXT_PRIMARY);

        chartPanel.setBackground(WHITE);
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);

        JPanel tableCard = new JPanel(new BorderLayout(0, 12));
        tableCard.setBackground(WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(18, 22, 18, 22)
        ));

        JLabel tableTitle = new JLabel("Generated Report Data");
        tableTitle.setFont(FontManager.semiBold(17));

        reportTable.setFont(FontManager.regular(12));
        reportTable.setRowHeight(28);
        reportTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        reportTable.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(reportTable);
        tableScroll.setPreferredSize(new Dimension(900, 220));
        tableScroll.setMinimumSize(new Dimension(400, 150));
        tableScroll.getVerticalScrollBar().setUnitIncrement(16);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.setPreferredSize(new Dimension(900, 300));
        tableCard.setMinimumSize(new Dimension(500, 240));
        tableCard.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        main.add(controls);
        main.add(Box.createVerticalStrut(18));
        main.add(cards);
        main.add(Box.createVerticalStrut(18));
        main.add(chartCard);
        main.add(Box.createVerticalStrut(18));
        main.add(tableCard);

        return main;
    }

    private JPanel createMetricCard(String title, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(0, 20));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER, 1),
            new EmptyBorder(20, 24, 20, 24)
        ));

        JLabel label = new JLabel(title);
        label.setFont(FontManager.regular(13));
        label.setForeground(MUTED);

        value.setFont(FontManager.semiBold(30));
        value.setForeground(AppTheme.TEXT_PRIMARY);

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private void loadDashboard() {
        generateButton.setEnabled(false);
        message.setText("Loading live statistics...");

        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                return new DashboardData(
                    controller.getSummary(),
                    controller.getMonthlyShipments()
                );
            }

            @Override
            protected void done() {
                generateButton.setEnabled(true);

                try {
                    DashboardData data = get();

                    totalValue.setText(
                        String.valueOf(data.summary.total()));
                    deliveredValue.setText(
                        String.valueOf(data.summary.delivered()));
                    transitValue.setText(
                        String.valueOf(data.summary.inTransit()));
                    pendingValue.setText(
                        String.valueOf(data.summary.pending()));

                    drawChart(data.months);
                    message.setText("Live statistics loaded.");

                } catch (Exception ex) {
                    message.setText("Unable to load statistics.");
                    showError(ex);
                }
            }
        }.execute();
    }

    private void generateReport() {
        String selected = (String) reportSelector.getSelectedItem();

        generateButton.setEnabled(false);
        message.setText("Generating " + selected + "...");

        new SwingWorker<List<ReportsDAO.ReportRow>, Void>() {
            @Override
            protected List<ReportsDAO.ReportRow> doInBackground()
                    throws Exception {
                return controller.generateReport(selected);
            }

            @Override
            protected void done() {
                generateButton.setEnabled(true);

                try {
                    currentRows = get();
                    populateTable(selected, currentRows);
                    message.setText(
                        currentRows.size() + " report rows generated.");

                    if ("Monthly Shipments".equals(selected)) {
                        loadDashboard();
                    }

                } catch (Exception ex) {
                    message.setText("Report generation failed.");
                    showError(ex);
                }
            }
        }.execute();
    }

    private void populateTable(
            String reportType,
            List<ReportsDAO.ReportRow> rows) {

        switch (reportType) {
            case "Delivery Performance" ->
                currentColumns = new String[] {
                    "Tracking Number", "Current Status", "Booked At",
                    "Delivered At", "Performance"
                };
            case "Shipment Status Summary" ->
                currentColumns = new String[] {
                    "Status", "Shipment Count"
                };
            case "Agent Performance" ->
                currentColumns = new String[] {
                    "Agent", "Assigned Shipments", "Completed"
                };
            default ->
                currentColumns = new String[] {
                    "Month", "Shipments"
                };
        }

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        for (String column : currentColumns) {
            tableModel.addColumn(column);
        }

        for (ReportsDAO.ReportRow row : rows) {
            tableModel.addRow(row.values());
        }

        for (int i = 0; i < reportTable.getColumnCount(); i++) {
            reportTable.getColumnModel().getColumn(i)
                .setPreferredWidth(170);
        }
    }

    private void drawChart(List<ReportsDAO.MonthCount> months) {
        chartPanel.removeAll();

        JPanel plot = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);

                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                int left = 45;
                int right = 15;
                int top = 12;
                int bottom = 30;
                int width = getWidth() - left - right;
                int height = getHeight() - top - bottom;

                if (months == null || months.isEmpty()
                        || width <= 0 || height <= 0) {
                    g.dispose();
                    return;
                }

                long max = months.stream()
                    .mapToLong(ReportsDAO.MonthCount::count)
                    .max().orElse(0);

                max = Math.max(1, max);

                g.setColor(BORDER);
                for (int i = 0; i <= 4; i++) {
                    int y = top + height - (height * i / 4);
                    g.drawLine(left, y, left + width, y);
                }

                int slot = Math.max(1, width / months.size());
                int barWidth = Math.max(8, slot - 12);

                for (int i = 0; i < months.size(); i++) {
                    ReportsDAO.MonthCount month = months.get(i);
                    int x = left + i * slot + (slot - barWidth) / 2;
                    int barHeight = (int) (
                        height * month.count() / (double) max
                    );
                    int y = top + height - barHeight;

                    g.setColor(GREEN);
                    g.fillRoundRect(
                        x, y, barWidth, Math.max(0, barHeight),
                        5, 5
                    );

                    g.setColor(MUTED);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 10));

                    String label = month.month();
                    int labelWidth = g.getFontMetrics().stringWidth(label);

                    g.drawString(
                        label,
                        x + (barWidth - labelWidth) / 2,
                        top + height + 18
                    );
                }

                g.dispose();
            }
        };
        
        plot.setPreferredSize(new Dimension(900, 210));
        plot.setMinimumSize(new Dimension(400, 150));

        plot.setBackground(WHITE);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBackground(WHITE);
        chartPanel.add(plot, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    private void exportCsv() {
        if (currentRows.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Generate a report before exporting.",
                "Nothing to Export",
                JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("kahinbhi-report.csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getParentFile(),
                file.getName() + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(
                file, StandardCharsets.UTF_8)) {

            writeCsvRow(writer, currentColumns);

            for (ReportsDAO.ReportRow row : currentRows) {
                writeCsvRow(writer, row.values());
            }

            JOptionPane.showMessageDialog(
                this,
                "Report exported successfully:\n" + file.getAbsolutePath(),
                "Export Complete",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void writeCsvRow(PrintWriter writer, String[] values) {
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                writer.print(',');
            }

            String value = values[i] == null ? "" : values[i];
            writer.print("\"" + value.replace("\"", "\"\"") + "\"");
        }

        writer.println();
    }

    private void styleButton(JButton button, Color background) {
        button.setFont(FontManager.semiBold(13));
        button.setBackground(background);
        button.setForeground(background.equals(WHITE)
            ? AppTheme.TEXT_PRIMARY : Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(11, 18, 11, 18));
        button.setCursor(Cursor.getPredefinedCursor(
            Cursor.HAND_CURSOR));
    }

    private void showError(Exception ex) {
        Throwable cause = ex.getCause() == null ? ex : ex.getCause();

        JOptionPane.showMessageDialog(
            this,
            cause.getMessage() == null
                ? "An unexpected error occurred."
                : cause.getMessage(),
            "Reports Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private record DashboardData(
        ReportsDAO.Summary summary,
        List<ReportsDAO.MonthCount> months
    ) {}
}
