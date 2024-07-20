import hall.collin.christopher.stl4j.STLParser;
import hall.collin.christopher.stl4j.Triangle;
import hall.collin.christopher.stl4j.Vec3d;
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
    private boolean hasChanged = true;
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

        Group newObject = cp5.addGroup("focal_length")
                .setBackgroundColor(64)
                .setBackgroundHeight(height(1f))
                .setLabel("Focal Length");

        cp5.addSlider("focal_slider")
                .setLabel("Focal Lenght")
                .setPosition(width(0.1f),height(0.5f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(3000)
                .setMax(10000)
                .setValue(4000)
                .moveTo(newObject);
        objectSelector.addItem(newObject);
    }

    //Called by "Add Object" Bang
    public void addObject(){
        // Constants
        final float maxResize = 1;
        final float minResize = 0;
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
        cp5.addSlider("ScaleX"+ objectCounter)
                .setLabel("Scale X")
                .setPosition(width(0.1f),height(0.5f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(minResize)
                .setMax(maxResize)
                .setValue(1)
                .moveTo(newObject);
        cp5.addSlider("ScaleY"+ objectCounter)
                .setLabel("Scale Y")
                .setPosition(width(0.1f),height(2.7f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(minResize)
                .setMax(maxResize)
                .setValue(1)
                .moveTo(newObject);
        cp5.addSlider("ScaleZ"+ objectCounter)
                .setLabel("Scale Z")
                .setPosition(width(0.1f),height(4.9f))
                .setHeight(height(2f))
                .setWidth(width(15f))
                .setMin(minResize)
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
    public float[] getMeshes() {
        List<Triangle> triangles = objects.get(objects.size()-1);
        float[] vertex = new float[triangles.size() * 9];
        for(int i = 0; i < triangles.size(); i++){
            Triangle triangle = triangles.get(i);
            Vec3d[] vertices = triangle.getVertices();
            for(int j = 0; j < vertices.length; j++){
                vertex[(i*9)+(j*3)] = (float)vertices[j].x;
                vertex[(i*9)+(j*3)+1] = (float)vertices[j].y;
                vertex[(i*9)+(j*3)+2] = (float)vertices[j].z;
            }
        }
        hasChanged = true;
        isMeshAvailable = false;
        return vertex;
    }

    public boolean isMeshAvailable(){
        return isMeshAvailable;
    }
    public boolean hasChanged(){
        if(this.hasChanged){
            this.hasChanged = false;
            return true;
        }
        return false;
    }

    public float getFocalLength(){
        hasChanged |= cp5.getController("focal_slider").isMousePressed();
        return cp5.getController("focal_slider").getValue();
    }

    public ArrayList<float[]> getParameters(){
        ArrayList<float[]> list = new ArrayList<float[]>();
        for(int i = 0; i < objectCounter; i++) {
            float scaleX     = cp5.getController("ScaleX" + i).getValue();
            float scaleY     = cp5.getController("ScaleY" + i).getValue();
            float scaleZ     = cp5.getController("ScaleZ" + i).getValue();
            float translateX = cp5.getController("TranslateX" + i).getValue();
            float translateY = cp5.getController("TranslateY" + i).getValue();
            float translateZ = cp5.getController("TranslateZ" + i).getValue();
            float rotateX    = cp5.getController("RotateX" + i).getValue();
            float rotateY    = cp5.getController("RotateY" + i).getValue();
            float rotateZ    = cp5.getController("RotateZ" + i).getValue();
            float[] values = {scaleX, scaleY, scaleZ, translateX, translateY, translateZ, rotateX, rotateY, rotateZ};
            this.hasChanged |= cp5.getController("ScaleX" + i).isMousePressed() ||
                    cp5.getController("ScaleY" + i).isMousePressed() ||
                    cp5.getController("ScaleZ" + i).isMousePressed() ||
                    cp5.getController("TranslateX" + i).isMousePressed() ||
                    cp5.getController("TranslateY" + i).isMousePressed() ||
                    cp5.getController("TranslateZ" + i).isMousePressed() ||
                    cp5.getController("RotateX" + i).isMousePressed() ||
                    cp5.getController("RotateY" + i).isMousePressed() ||
                    cp5.getController("RotateZ" + i).isMousePressed();
            list.add(values);
        }
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
