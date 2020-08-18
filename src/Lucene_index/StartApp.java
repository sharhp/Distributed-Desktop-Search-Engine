package Lucene_index;

//import org.jdesktop.application.Application;//hps
import org.jdesktop.application.SingleFrameApplication;
public class StartApp extends SingleFrameApplication {
    @Override protected void startup() {
        show(new AppView(this));
    }
    @Override protected void configureWindow(java.awt.Window root) {
    }
    public static void main(String[] args) {
        launch(StartApp.class, args);
    }
}