package com.courier.main;

import javax.swing.SwingUtilities;
import com.courier.view.common.BaseFrame;
import com.courier.view.admin.DashboardPanel;
import com.courier.view.admin.TrackingPanel;

public class GUITest {

	public static void main(String[] args) {

	    SwingUtilities.invokeLater(() -> {

	        BaseFrame frame = new BaseFrame(
	            "Dashboard",
	            "Kunjal",
	            "Administrator"
	        );

	        TrackingPanel tracking = new TrackingPanel();

	        frame.setContent(tracking);


	        frame.setVisible(true);
	    });
	}
}