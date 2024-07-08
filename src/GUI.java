import hall.collin.christopher.stl4j.STLParser;
import hall.collin.christopher.stl4j.Triangle;
import processing.core.PApplet;
import controlP5.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class GUI {
    private final PApplet a;
    private final ControlP5 cp5;
    private final Accordion objectSelector;

    private int objectCounter = 0;

    private ArrayList<List<Triangle>> objects = new ArrayList<List<Triangle>>();
    private boolean isMeshAvailable = false;

    private ArrayList<Float[]> controls = new ArrayList<Float[]>();

    public GUI(PApplet applet) {
        a = applet;
        cp5 = new ControlP5(a);
        cp5.addBang("Add Object")
                .setPosition(0,0)
                .setWidth(width(20))
                .setHeight(height(2.5f))
                .setLabelVisible(false)
                .plugTo(this, "addObject");

        objectSelector = cp5.addAccordion("Objects")
                .setWidth(width(20))
                .setPosition(0,height(2.5f));

    }

    //Called by "Add Object" Bang
    public void addObject(){
        // Constants
        final float maxResize = 10;
        final float maxTranslate = 200;

        // Request STL File from user
        String fileName = parseSTL();
        if(fileName == null){
            return;
        }

        // If file selected with success, create the new group
        Group newObject = cp5.addGroup("Object"+ objectCounter)
                .setBackgroundColor(64)
                .setBackgroundHeight(150)
                .setLabel(fileName.replace(".stl", ""));

        // Then, create all the sliders
        // Resize Sliders
        cp5.addSlider("ResizeX"+ objectCounter)
                .setLabel("Resize X")
                .setPosition(width(0.1f),height(0.5f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(-maxResize)
                .setMax(maxResize)
                .setValue(1)
                .moveTo(newObject);
        cp5.addSlider("ResizeY"+ objectCounter)
                .setLabel("Resize Y")
                .setPosition(width(0.1f),height(2.7f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(-maxResize)
                .setMax(maxResize)
                .setValue(1)
                .moveTo(newObject);
        cp5.addSlider("ResizeZ"+ objectCounter)
                .setLabel("Resize Z")
                .setPosition(width(0.1f),height(4.9f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(-maxResize)
                .setMax(maxResize)
                .setValue(1)
                .setTriggerEvent(ControlP5Constants.RELEASE)
                .moveTo(newObject);

        // Translation Sliders
        cp5.addSlider("TranslateX"+ objectCounter)
                .setLabel("Translate X")
                .setPosition(width(0.1f),height(7.1f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(-maxTranslate)
                .setMax(maxTranslate)
                .setValue(0)
                .moveTo(newObject);
        cp5.addSlider("TranslateY"+ objectCounter)
                .setLabel("Translate Y")
                .setPosition(width(0.1f),height(9.3f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(-maxTranslate)
                .setMax(maxTranslate)
                .setValue(0)
                .moveTo(newObject);
        cp5.addSlider("TranslateZ"+ objectCounter)
                .setLabel("Translate Z")
                .setPosition(width(0.1f),height(11.5f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(-maxTranslate)
                .setMax(maxTranslate)
                .setValue(0)
                .moveTo(newObject);

        // Rotation Sliders
        cp5.addSlider("RotateX"+ objectCounter)
                .setLabel("Rotate X")
                .setPosition(width(0.1f),height(13.7f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin((float)-Math.PI)
                .setMax((float)Math.PI)
                .setValue(0)
                .moveTo(newObject);
        cp5.addSlider("RotateY"+ objectCounter)
                .setLabel("Rotate Y")
                .setPosition(width(0.1f),height(15.9f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin((float)-Math.PI)
                .setMax((float)Math.PI)
                .setValue(0)
                .moveTo(newObject);
        cp5.addSlider("RotateZ"+ objectCounter)
                .setLabel("Rotate Z")
                .setPosition(width(0.1f),height(18.1f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin((float)-Math.PI)
                .setMax((float)Math.PI)
                .setValue(0)
                .moveTo(newObject);
        // Add 1 to object Counter
        objectCounter++;
        isMeshAvailable = true;
        // Add the new group to the Accordion
        objectSelector.addItem(newObject);
    }

    // Communication Funcions
    public ArrayList<List<Triangle>> getMeshes() {
        isMeshAvailable = false;
        return objects;
    }

    public boolean isMeshAvailable(){
        return isMeshAvailable;
    }

    public ArrayList<Float[]> getParameters(){
        ArrayList<Float[]> list = new ArrayList<Float[]>();

        return list;
    }

    public void update(){

    }

    // Support Functions
    private int width(float position){
        return (int) ((position * a.width) / 100f);
    }

    private int height(float position){
        return (int) ((position * a.height) / 100f);
    }

    private static File askFile(){
        JFileChooser jfc = new JFileChooser();
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("STL files", "stl");
        jfc.addChoosableFileFilter(filter);
        int action = jfc.showOpenDialog(null);
        if(action != JFileChooser.APPROVE_OPTION){
            return null;
        }
        return jfc.getSelectedFile();
    }

    private String parseSTL(){
        // Request File from user
        File file = askFile();
        if(file == null){
            System.out.println("Operation cancelled by user");
            return null;
        }

        // In fact, parse the data
        try {
            objects.add(STLParser.parseSTLFile(file.toPath()));
        } catch(Exception e){
            System.out.println("Error while creating STL File");
            return null;
        }

        return file.getName();

    }

}
