import processing.core.PApplet;

public class Main extends PApplet{
    VideoEngine engine;
    public void settings(){
        size(1280,720);
    }
    public void setup(){
        engine = new VideoEngine(this);
        windowResizable(false);
    }

    public void draw(){
        engine.update();
    }

    public static void main(String[] args){
        PApplet.main("Main");
    }
}