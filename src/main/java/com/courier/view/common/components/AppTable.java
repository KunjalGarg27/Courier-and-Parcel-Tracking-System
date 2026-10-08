package com.courier.view.common.components;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import com.courier.util.AppTheme;
import com.courier.util.FontManager;

public class AppTable extends JTable {

    private static final long serialVersionUID = 1L;

    public AppTable(Object[][] data, String[] columns) {
        super(data, columns);

        setFont(FontManager.regular(14));
        setForeground(AppTheme.TEXT_PRIMARY);
        setBackground(AppTheme.CARD);

        setRowHeight(42);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(AppTheme.BORDER);

        setSelectionBackground(AppTheme.SURFACE);
        setSelectionForeground(AppTheme.TEXT_PRIMARY);

        setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        setFillsViewportHeight(true);

        JTableHeader header = getTableHeader();

        header.setFont(FontManager.semiBold(14));
        header.setBackground(AppTheme.SURFACE);
        header.setForeground(AppTheme.TEXT_PRIMARY);

        header.setPreferredSize(
            new Dimension(0, 45)
        );

        header.setReorderingAllowed(false);
    }
}