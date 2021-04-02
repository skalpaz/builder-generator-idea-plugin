package pl.mjedynak.idea.plugins.builder.logger;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;

public class CustomLogger {

    private static final NotificationGroup STICKY_GROUP = new NotificationGroup("demo.notifications.balloon",NotificationDisplayType.STICKY_BALLOON,true);
    private static final NotificationGroup TOOL_WINDOW_GROUP = new NotificationGroup("demo.notifications.toolWindow",NotificationDisplayType.TOOL_WINDOW,true);

    public static void displayBalloonNotification(Project project, String textToDisplay) {
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);

        JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(textToDisplay, MessageType.INFO, null)
                .setFadeoutTime(7500)
                .createBalloon()
                .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                        Balloon.Position.atRight);
    }

    public static void displayStickyNotification(String title, String subtitle, String content) {
        displayNotification(STICKY_GROUP.createNotification(title,subtitle,content, NotificationType.INFORMATION));
    }

    public static void displayToolWindowNotification(String title, String subtitle, String content) {
        displayNotification(TOOL_WINDOW_GROUP.createNotification(title,subtitle,content, NotificationType.INFORMATION));
    }

    private static void displayNotification(Notification notification) {
        Notifications.Bus.notify(notification, null);
    }

    // Example:
    //    CustomLogger.displayStickyNotification(
    //            this.getClass().getName(),
    //                new Object() {}.getClass().getEnclosingMethod().getName(),
    //                "Start...");
}