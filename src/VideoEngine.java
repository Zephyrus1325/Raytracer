import processing.core.*;

public class VideoEngine {
    private final PApplet a;
    private final GUI gui;
    private final int minWidth = 500;
    private final int minHeight = 500;


    public VideoEngine(PApplet app){
        gui = new GUI(app);
        a = app;
    }

    public void update(){
        gui.update();
    }

}
