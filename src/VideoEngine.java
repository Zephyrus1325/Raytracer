import hall.collin.christopher.stl4j.Triangle;
import hall.collin.christopher.stl4j.Vec3d;
import processing.core.*;

import java.util.ArrayList;
import java.util.List;

public class VideoEngine {
    private final PApplet a;
    private final GUI gui;
    private final int minWidth = 500;
    private final int minHeight = 500;
    private float focalLength = 50;
    private ArrayList<float[]> objects = new ArrayList<float[]>();
    private ArrayList<float[]> parameters = new ArrayList<float[]>();
    public VideoEngine(PApplet app){
        gui = new GUI(app);
        a = app;
    }

    public void update(){
        gui.update();
        parameters = gui.getParameters();
        focalLength = gui.getFocalLength();
        if(gui.isMeshAvailable()) {
            objects.add(gui.getMeshes());
        }
        //if(a.millis() % 200 <= 20){renderBasic();}
        renderBasic();
    }

    private void renderBasic(){
        a.background(150);
        a.pushMatrix();
        a.translate(a.width/2f, a.height/2f);
        a.stroke(64);
        a.strokeWeight(0.5f);
        for(int i = 0; i < objects.size(); i++){
            float[] object = objects.get(i);
            float[] parameter = parameters.get(i);

            for(int j = 0; j < object.length; j += 9){

                float[] v0_raw = new float[]{object[j], object[j+1], object[j+2]};
                float[] v1_raw = new float[]{object[j+3], object[j+4], object[j+5]};
                float[] v2_raw = new float[]{object[j+6], object[j+7], object[j+8]};

                float[] v0_r = rotate(v0_raw, parameter[6], parameter[7], parameter[8]);
                float[] v1_r = rotate(v1_raw, parameter[6], parameter[7], parameter[8]);
                float[] v2_r = rotate(v2_raw, parameter[6], parameter[7], parameter[8]);

                float[] v0 = projection(v0_r[0], v0_r[1], v0_r[2]);
                float[] v1 = projection(v1_r[0], v1_r[1], v1_r[2]);
                float[] v2 = projection(v2_r[0], v2_r[1], v2_r[2]);
                //a.triangle(v0[0], v0[1], v1[0], v1[1], v2[0], v2[1]);
                //if(j % 56 == 0){
                    a.line(v0[0], v0[1],v1[0], v1[1]);
                    a.line(v0[0], v0[1],v2[0], v2[1]);
                    a.line(v1[0], v1[1],v2[0], v2[1]);
                //}


            }
        }
        a.popMatrix();
    }

    private float[] rotate(float[] vertices, float angleX, float angleY, float angleZ){
        float[][] rotationMatrix = new float[][]{
                {(float)(Math.cos(angleZ) * Math.cos(angleY)), (float)(Math.cos(angleZ) * Math.sin(angleY) * Math.sin(angleX) - Math.sin(angleZ) * Math.cos(angleX)), (float)(Math.cos(angleZ) * Math.sin(angleY) * Math.cos(angleX) + Math.sin(angleZ) * Math.sin(angleY))},
                {(float)(Math.sin(angleZ) * Math.cos(angleY)), (float)(Math.sin(angleZ) * Math.sin(angleY) * Math.sin(angleX) - Math.cos(angleZ) * Math.cos(angleX)), (float)(Math.sin(angleZ) * Math.sin(angleY) * Math.cos(angleX) - Math.cos(angleZ) * Math.sin(angleY))},
                {(float)(-Math.sin(angleY))                  , (float)(Math.cos(angleY) * Math.sin(angleX))                                                         , (float)(Math.cos(angleY) * Math.cos(angleX))}};
        float x_rotated = (rotationMatrix[0][0] * vertices[0] + rotationMatrix[1][0] * vertices[1] + rotationMatrix[2][0] * vertices[2]);
        float y_rotated = (rotationMatrix[0][1] * vertices[0] + rotationMatrix[1][1] * vertices[1] + rotationMatrix[2][1] * vertices[2]);
        float z_rotated = (rotationMatrix[0][2] * vertices[0] + rotationMatrix[1][2] * vertices[1] + rotationMatrix[2][2] * vertices[2]);
        return new float[]{x_rotated, y_rotated, z_rotated};
    }
    private float[] projection(float x, float y, float z){
        float x_projected = (focalLength * x * 10f) / (z * 10f + focalLength);
        float y_projected = (focalLength * y * 10f) / (z * 10f + focalLength);
        return new float[]{x_projected, y_projected};
    }
}
