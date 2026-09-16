/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.utils;

import fashionflairboutique.models.User;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 *
 * @author Chethiya
 */
public class UIUtils {
    private static final int DISPLAY_DURATION = 5000; // 5 seconds
    private static final String TIMER_KEY = "LABEL_CLEAR_TIMER";

    /**
     * Displays a message on a label for a full 5 seconds.
     * If a new message arrives, it replaces the old text and resets the 5-second countdown.
     */
    public static void showTimedMessage(JLabel label, String message, Color color) {
        if (label == null) return;

        // 1. Check if this specific label already has a running timer
        Timer existingTimer = (Timer) label.getClientProperty(TIMER_KEY);
        if (existingTimer != null && existingTimer.isRunning()) {
            existingTimer.stop(); // Cancel old countdown so it won't clear prematurely
        }

        // 2. Set new content immediately
        label.setText(message);
        label.setForeground(color);

        // 3. Create a dedicated timer for this new message
        Timer newTimer = new Timer(DISPLAY_DURATION, e -> {
            label.setText("");
            label.putClientProperty(TIMER_KEY, null); // Cleanup property
        });
        newTimer.setRepeats(false);

        // 4. Attach and start timer
        label.putClientProperty(TIMER_KEY, newTimer);
        newTimer.start();
    }

    public static void showSuccess(JLabel label, String message) {
        showTimedMessage(label, message, new Color(46, 204, 113)); // Green
    }

    public static void showError(JLabel label, String message) {
        showTimedMessage(label, message, new Color(231, 76, 60)); // Red
    }
    
    public static Timer startLiveClock(JLabel lblDate, JLabel lblTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");

        LocalDateTime now = LocalDateTime.now();
        lblDate.setText(now.format(dateFormatter));
        lblTime.setText(now.format(timeFormatter));

        Timer timer = new Timer(1000, e -> {
            LocalDateTime current = LocalDateTime.now();
            lblDate.setText(current.format(dateFormatter));
            lblTime.setText(current.format(timeFormatter));
        });
        timer.start();
        return timer; // Return timer so caller frame can stop it on dispose
    }

    public static void displayUserDetails(JLabel lblUser, User user) {
        if (user != null && lblUser != null) {
            lblUser.setText(user.getFullName() + " (" + user.getRole() + ")");
        }
    }
}
