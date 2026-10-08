package com.courier.view.admin;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppCard;
import com.courier.view.common.components.StatCard;

public class ReportsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> reportType;
    private ReportChartPanel chartPanel;

    public ReportsPanel() {

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);

        setBorder(
            BorderFactory.createEmptyBorder(
                25, 30, 25, 30
            )
        );

        // ==========================================
        // TITLE
        // ==========================================

        JPanel titlePanel =
            new JPanel(new GridLayout(2, 1, 0, 3));

        titlePanel.setOpaque(false);

        JLabel title =
            new JLabel("Reports & Analytics");

        title.setFont(
            FontManager.semiBold(26)
        );

        title.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        JLabel subtitle =
            new JLabel(
                "Monitor shipment and delivery performance"
            );

        subtitle.setFont(
            FontManager.regular(13)
        );

        subtitle.setForeground(
            AppTheme.TEXT_SECONDARY
        );

        titlePanel.add(title);
        titlePanel.add(subtitle);

        add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // MAIN CONTENT
        // ==========================================

        JPanel mainPanel =
            new JPanel(new BorderLayout(15, 15));

        mainPanel.setOpaque(false);

        // ==========================================
        // REPORT FILTER
        // ==========================================

        AppCard filterCard = new AppCard();

        filterCard.setLayout(
            new FlowLayout(
                FlowLayout.LEFT,
                12,
                12
            )
        );

        JLabel reportLabel =
            new JLabel("Report:");

        reportLabel.setFont(
            FontManager.semiBold(13)
        );

        reportLabel.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        reportType =
            new JComboBox<>(
                new String[] {
                    "Monthly Shipments",
                    "Delivery Performance",
                    "Shipment Status"
                }
            );

        reportType.setFont(
            FontManager.regular(13)
        );

        reportType.setPreferredSize(
            new Dimension(190, 38)
        );

        JButton generateButton =
            new JButton("Generate Report");

        stylePrimaryButton(
            generateButton,
            145
        );

        JButton exportButton =
            new JButton("Export");

        styleSecondaryButton(
            exportButton,
            90
        );

        filterCard.add(reportLabel);
        filterCard.add(reportType);
        filterCard.add(generateButton);
        filterCard.add(exportButton);

        mainPanel.add(
            filterCard,
            BorderLayout.NORTH
        );

        // ==========================================
        // STATISTICS
        // ==========================================

        JPanel statsPanel =
            new JPanel(
                new GridLayout(1, 4, 15, 0)
            );

        statsPanel.setOpaque(false);

        statsPanel.add(
            new StatCard(
                "Total Shipments",
                "1,248"
            )
        );

        statsPanel.add(
            new StatCard(
                "Delivered",
                "850"
            )
        );

        statsPanel.add(
            new StatCard(
                "In Transit",
                "342"
            )
        );

        statsPanel.add(
            new StatCard(
                "Pending",
                "56"
            )
        );

        mainPanel.add(
            statsPanel,
            BorderLayout.CENTER
        );

        // ==========================================
        // CHART
        // ==========================================

        AppCard chartCard =
            new AppCard();

        chartCard.setLayout(
            new BorderLayout()
        );

        JLabel chartTitle =
            new JLabel(
                "Monthly Shipment Performance"
            );

        chartTitle.setFont(
            FontManager.semiBold(16)
        );

        chartTitle.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        chartTitle.setBorder(
            BorderFactory.createEmptyBorder(
                12, 15, 8, 15
            )
        );

        chartCard.add(
            chartTitle,
            BorderLayout.NORTH
        );

        chartPanel =
            new ReportChartPanel();

        chartCard.add(
            chartPanel,
            BorderLayout.CENTER
        );

        mainPanel.add(
            chartCard,
            BorderLayout.SOUTH
        );

        add(
            mainPanel,
            BorderLayout.CENTER
        );

        // ==========================================
        // EVENT HANDLING
        // ==========================================

        generateButton.addActionListener(e -> {

            String selectedReport =
                (String) reportType.getSelectedItem();

            chartPanel.setReportType(
                selectedReport
            );
        });

        exportButton.addActionListener(e -> {

            javax.swing.JOptionPane.showMessageDialog(
                this,
                "Report export functionality will be connected later.",
                "Export Report",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
            );
        });

        reportType.addActionListener(e -> {

            String selectedReport =
                (String) reportType.getSelectedItem();

            chartPanel.setReportType(
                selectedReport
            );
        });
    }

    // ==========================================
    // BUTTON STYLING
    // ==========================================

    private void stylePrimaryButton(
        JButton button,
        int width
    ) {

        button.setFont(
            FontManager.semiBold(13)
        );

        button.setForeground(
            AppTheme.CARD
        );

        button.setBackground(
            AppTheme.PRIMARY
        );

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setPreferredSize(
            new Dimension(width, 38)
        );
    }

    private void styleSecondaryButton(
        JButton button,
        int width
    ) {

        button.setFont(
            FontManager.semiBold(13)
        );

        button.setForeground(
            AppTheme.TEXT_PRIMARY
        );

        button.setBackground(
            AppTheme.SURFACE
        );

        button.setFocusPainted(false);

        button.setPreferredSize(
            new Dimension(width, 38)
        );
    }

    // ==========================================
    // SIMPLE CHART PANEL
    // ==========================================

    private static class ReportChartPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private String reportType =
            "Monthly Shipments";

        public ReportChartPanel() {

            setBackground(
                AppTheme.CARD
            );

            setPreferredSize(
                new Dimension(700, 230)
            );
        }

        public void setReportType(
            String reportType
        ) {

            this.reportType = reportType;

            repaint();
        }

        @Override
        protected void paintComponent(
            Graphics g
        ) {

            super.paintComponent(g);

            Graphics2D g2 =
                (Graphics2D) g.create();

            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            // Chart area

            int left = 60;
            int bottom = height - 45;
            int top = 30;
            int right = width - 30;

            // Axes

            g2.setColor(
                AppTheme.BORDER
            );

            g2.setStroke(
                new BasicStroke(1)
            );

            g2.drawLine(
                left,
                top,
                left,
                bottom
            );

            g2.drawLine(
                left,
                bottom,
                right,
                bottom
            );

            // Grid lines

            for (int i = 1; i <= 4; i++) {

                int y =
                    bottom
                    - ((bottom - top) * i / 5);

                g2.drawLine(
                    left,
                    y,
                    right,
                    y
                );
            }

            // Sample data

            int[] values = {
                120,
                180,
                145,
                230,
                200,
                270,
                250,
                310,
                290,
                340,
                360,
                390
            };

            String[] months = {
                "Jan", "Feb", "Mar", "Apr",
                "May", "Jun", "Jul", "Aug",
                "Sep", "Oct", "Nov", "Dec"
            };

            int chartWidth =
                right - left;

            int step =
                chartWidth / values.length;

            // Bars

            for (int i = 0;
                 i < values.length;
                 i++) {

                int barHeight =
                    (values[i] * 130) / 400;

                int x =
                    left
                    + (i * step)
                    + 5;

                int y =
                    bottom - barHeight;

                g2.setColor(
                    AppTheme.PRIMARY
                );

                g2.fillRoundRect(
                    x,
                    y,
                    Math.max(12, step - 10),
                    barHeight,
                    6,
                    6
                );

                g2.setColor(
                    AppTheme.TEXT_SECONDARY
                );

                g2.setFont(
                    FontManager.regular(10)
                );

                g2.drawString(
                    months[i],
                    x,
                    bottom + 20
                );
            }

            // Report name

            g2.setColor(
                AppTheme.TEXT_PRIMARY
            );

            g2.setFont(
                FontManager.semiBold(12)
            );

            g2.drawString(
                reportType,
                left,
                20
            );

            g2.dispose();
        }
    }
}