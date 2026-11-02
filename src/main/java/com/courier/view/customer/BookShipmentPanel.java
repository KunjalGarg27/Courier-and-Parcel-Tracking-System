package com.courier.view.customer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.courier.controller.ShipmentController;
import com.courier.model.Address;
import com.courier.model.ServiceType;
import com.courier.model.Shipment;
import com.courier.util.AppTheme;
import com.courier.util.FontManager;
import com.courier.view.common.components.AppButton;
import com.courier.view.common.components.AppTextField;

public class BookShipmentPanel extends JPanel {

    private final long userId;
    private final ShipmentController shipmentController =
            new ShipmentController();

    private final AppTextField senderName = new AppTextField(20);
    private final AppTextField senderPhone = new AppTextField(20);
    private final AppTextField receiverName = new AppTextField(20);
    private final AppTextField receiverPhone = new AppTextField(20);

    private final AppTextField pickupLine1 = new AppTextField(20);
    private final AppTextField pickupLine2 = new AppTextField(20);
    private final AppTextField pickupCity = new AppTextField(20);
    private final AppTextField pickupState = new AppTextField(20);
    private final AppTextField pickupPostal = new AppTextField(20);
    private final AppTextField pickupCountry = new AppTextField(20);

    private final AppTextField deliveryLine1 = new AppTextField(20);
    private final AppTextField deliveryLine2 = new AppTextField(20);
    private final AppTextField deliveryCity = new AppTextField(20);
    private final AppTextField deliveryState = new AppTextField(20);
    private final AppTextField deliveryPostal = new AppTextField(20);
    private final AppTextField deliveryCountry = new AppTextField(20);

    private final JComboBox<ServiceType> serviceBox = new JComboBox<>();
    private final AppTextField description = new AppTextField(20);
    private final AppTextField weight = new AppTextField(20);

    private final AppButton bookButton = new AppButton("Book Shipment");

    private final JPanel form = new JPanel(new GridBagLayout());
    private int row = 0;

    public BookShipmentPanel(long userId) {

        this.userId = userId;

        setLayout(new BorderLayout(20, 20));
        setBackground(AppTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Book a Shipment");
        title.setFont(FontManager.semiBold(24));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        form.setBackground(AppTheme.SURFACE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        pickupCountry.setText("India");
        deliveryCountry.setText("India");

        buildForm();

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // AppButton's default text colour is dark; make it readable here.
        bookButton.setForeground(Color.WHITE);
        bookButton.addActionListener(e -> bookShipment());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setOpaque(false);
        south.add(bookButton);
        add(south, BorderLayout.SOUTH);

        loadServiceTypes();
    }

    // ---------- form layout ----------

    private void buildForm() {
        addSection("Sender & Receiver");
        addRow("Sender name *", senderName);
        addRow("Sender phone *", senderPhone);
        addRow("Receiver name *", receiverName);
        addRow("Receiver phone *", receiverPhone);

        addSection("Pickup address");
        addRow("Address line 1 *", pickupLine1);
        addRow("Address line 2", pickupLine2);
        addRow("City *", pickupCity);
        addRow("State *", pickupState);
        addRow("Postal code *", pickupPostal);
        addRow("Country", pickupCountry);

        addSection("Delivery address");
        addRow("Address line 1 *", deliveryLine1);
        addRow("Address line 2", deliveryLine2);
        addRow("City *", deliveryCity);
        addRow("State *", deliveryState);
        addRow("Postal code *", deliveryPostal);
        addRow("Country", deliveryCountry);

        addSection("Package");
        addRow("Service type *", serviceBox);
        addRow("Package description", description);
        addRow("Weight (kg) *", weight);

        // Pushes all rows to the top.
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0;
        filler.gridy = row;
        filler.weighty = 1;
        form.add(new JLabel(), filler);
    }

    private void addSection(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontManager.semiBold(16));
        label.setForeground(AppTheme.PRIMARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(14, 0, 6, 0);
        form.add(label, gbc);
    }

    private void addRow(String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(FontManager.medium(14));
        label.setForeground(AppTheme.TEXT_PRIMARY);

        GridBagConstraints l = new GridBagConstraints();
        l.gridx = 0;
        l.gridy = row;
        l.anchor = GridBagConstraints.WEST;
        l.insets = new Insets(4, 0, 4, 16);
        form.add(label, l);

        GridBagConstraints f = new GridBagConstraints();
        f.gridx = 1;
        f.gridy = row++;
        f.weightx = 1;
        f.fill = GridBagConstraints.HORIZONTAL;
        f.insets = new Insets(4, 0, 4, 0);
        form.add(field, f);
    }

    // ---------- data loading ----------

    private void loadServiceTypes() {
        try {
            List<ServiceType> types = shipmentController.getServiceTypes();

            for (ServiceType type : types) {
                serviceBox.addItem(type);
            }

            if (types.isEmpty()) {
                bookButton.setEnabled(false);
                JOptionPane.showMessageDialog(this,
                        "No active service types found in the database.",
                        "Setup Problem", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            bookButton.setEnabled(false);
            JOptionPane.showMessageDialog(this,
                    "Could not load service types. "
                            + "Check the database connection.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- booking ----------

    private void bookShipment() {
        try {
            Shipment shipment = new Shipment();
            shipment.setSenderName(senderName.getText());
            shipment.setSenderPhone(senderPhone.getText());
            shipment.setReceiverName(receiverName.getText());
            shipment.setReceiverPhone(receiverPhone.getText());

            ServiceType selected = (ServiceType) serviceBox.getSelectedItem();
            shipment.setServiceTypeId(
                    selected == null ? 0 : selected.getServiceTypeId());

            shipment.setPackageDescription(description.getText());
            shipment.setWeightKg(parseWeight(weight.getText()));

            Address pickup = buildAddress(pickupLine1, pickupLine2,
                    pickupCity, pickupState, pickupPostal, pickupCountry);
            Address delivery = buildAddress(deliveryLine1, deliveryLine2,
                    deliveryCity, deliveryState, deliveryPostal,
                    deliveryCountry);

            String trackingNumber = shipmentController.bookShipment(
                    userId, shipment, pickup, delivery);

            JOptionPane.showMessageDialog(this,
                    "Shipment booked successfully!\nTracking number: "
                            + trackingNumber,
                    "Booking Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Please check your details",
                    JOptionPane.WARNING_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "The shipment could not be saved. Nothing was booked.\n"
                            + "Details: " + e.getMessage(),
                    "Booking Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseWeight(String text) {
        String trimmed = text.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Weight must be a number, for example 2.5.");
        }
    }

    private Address buildAddress(
            AppTextField line1, AppTextField line2, AppTextField city,
            AppTextField state, AppTextField postal, AppTextField country) {

        Address address = new Address();
        address.setAddressLine1(line1.getText());
        address.setAddressLine2(line2.getText());
        address.setCity(city.getText());
        address.setState(state.getText());
        address.setPostalCode(postal.getText());
        address.setCountry(country.getText());
        return address;
    }

    private void clearForm() {
        AppTextField[] fields = {
                senderName, senderPhone, receiverName, receiverPhone,
                pickupLine1, pickupLine2, pickupCity, pickupState,
                pickupPostal, deliveryLine1, deliveryLine2, deliveryCity,
                deliveryState, deliveryPostal, description, weight
        };

        for (AppTextField field : fields) {
            field.setText("");
        }

        pickupCountry.setText("India");
        deliveryCountry.setText("India");

        if (serviceBox.getItemCount() > 0) {
            serviceBox.setSelectedIndex(0);
        }
    }
}