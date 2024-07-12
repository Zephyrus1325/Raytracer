import processing.core.*;

import java.util.ArrayList;

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

                float[] v0_s = scale(v0_raw, parameter);
                float[] v1_s = scale(v1_raw, parameter);
                float[] v2_s = scale(v2_raw, parameter);

                float[] v0_r = rotate(v0_s, parameter);
                float[] v1_r = rotate(v1_s, parameter);
                float[] v2_r = rotate(v2_s, parameter);

                float[] v0_t = translate(v0_r, parameter);
                float[] v1_t = translate(v1_r, parameter);
                float[] v2_t = translate(v2_r, parameter);

                float[] v0 = projection(v0_t[0], v0_t[1], v0_t[2]);
                float[] v1 = projection(v1_t[0], v1_t[1], v1_t[2]);
                float[] v2 = projection(v2_t[0], v2_t[1], v2_t[2]);

                //a.triangle(v0[0], v0[1], v1[0], v1[1], v2[0], v2[1]);
                a.line(v0[0], v0[1],v1[0], v1[1]);
                a.line(v0[0], v0[1],v2[0], v2[1]);
                a.line(v1[0], v1[1],v2[0], v2[1]);


            }
        }
        a.popMatrix();
    }

    private float[] scale(float[] vertices, float[] parameters){

        final float scaleX = parameters[0];
        final float scaleY = parameters[1];
        final float scaleZ = parameters[2];

        final float[][] scaleMatrix = new float[][]{
                {scaleX, 0, 0},
                {0, scaleY, 0},
                {0, 0, scaleZ}};
        float x_scaled = (scaleMatrix[0][0] * vertices[0] + scaleMatrix[1][0] * vertices[1] + scaleMatrix[2][0] * vertices[2]);
        float y_scaled = (scaleMatrix[0][1] * vertices[0] + scaleMatrix[1][1] * vertices[1] + scaleMatrix[2][1] * vertices[2]);
        float z_scaled = (scaleMatrix[0][2] * vertices[0] + scaleMatrix[1][2] * vertices[1] + scaleMatrix[2][2] * vertices[2]);
        return new float[]{x_scaled, y_scaled, z_scaled};
    }

    private float[] translate(float[] vertices, float[] parameters){

        final float translateX = parameters[3];
        final float translateY = parameters[4];
        final float translateZ = parameters[5];

        final float[] translateMatrix = new float[]{
                translateX,
                translateY,
                translateZ};

        float x_translated = vertices[0] + translateMatrix[0];
        float y_translated = vertices[1] + translateMatrix[1];
        float z_translated = vertices[2] + translateMatrix[2];
        return new float[]{x_translated, y_translated, z_translated};
    }


    private float[] rotate(float[] vertices, float[] parameters ){

        final float angleX = parameters[6];
        final float angleY = parameters[7];
        final float angleZ = parameters[8];

        final float[][] rotationMatrix = new float[][]{
                {(float)(Math.cos(angleZ) * Math.cos(angleY)), (float)(Math.cos(angleZ) * Math.sin(angleY) * Math.sin(angleX) - Math.sin(angleZ) * Math.cos(angleX)), (float)(Math.cos(angleZ) * Math.sin(angleY) * Math.cos(angleX) + Math.sin(angleZ) * Math.sin(angleY))},
                {(float)(Math.sin(angleZ) * Math.cos(angleY)), (float)(Math.sin(angleZ) * Math.sin(angleY) * Math.sin(angleX) + Math.cos(angleZ) * Math.cos(angleX)), (float)(Math.sin(angleZ) * Math.sin(angleY) * Math.cos(angleX) - Math.cos(angleZ) * Math.sin(angleY))},
                {(float)(-Math.sin(angleY))                  , (float)(Math.cos(angleY) * Math.sin(angleX))                                                         , (float)(Math.cos(angleY) * Math.cos(angleX))                                                        }};
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

    private float[] normalVector(float[] v0, float[] v1, float[] v2){
        float[] vect1 = {v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2] };
        float[] vect2 = {v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2] };

        float norm_x = vect1[1] * vect2[2] - vect1[2] * vect2[1];
        float norm_y = vect1[2] * vect2[0] - vect1[0] * vect2[2];
        float norm_z = vect1[0] * vect2[1] - vect1[1] * vect2[0];

        return new float[]{norm_x, norm_y, norm_z};
    }
}
