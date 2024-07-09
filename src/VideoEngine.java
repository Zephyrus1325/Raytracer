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
    private final float focalLength = 50;
    private ArrayList<List<Triangle>> objects = new ArrayList<List<Triangle>>();
    private ArrayList<float[]> parameters = new ArrayList<float[]>();
    public VideoEngine(PApplet app){
        gui = new GUI(app);
        a = app;
    }

    public void update(){
        gui.update();
        parameters = gui.getParameters();
        if(gui.isMeshAvailable()) {
            objects = gui.getMeshes();
        }
        if(a.millis() % 200 <= 20){renderBasic();}
    }

    private void renderBasic(){
        a.background(150);
        a.pushMatrix();
        a.translate(a.width/2f, a.height/2f);
        a.stroke(64);
        a.strokeWeight(0.5f);
        for(int i = 0; i < objects.size(); i++){
            List<Triangle> object = objects.get(i);
            float[] parameter = parameters.get(i);
            for( Triangle polygon : object){
                Vec3d[] v = polygon.getVertices();

                float[] v0_r = rotate(v[0], parameter[6], parameter[7], parameter[8]);
                float[] v1_r = rotate(v[1], parameter[6], parameter[7], parameter[8]);
                float[] v2_r = rotate(v[2], parameter[6], parameter[7], parameter[8]);

                float[] v0 = projection(v0_r[0], v0_r[1], v0_r[2]);
                float[] v1 = projection(v1_r[0], v1_r[1], v1_r[2]);
                float[] v2 = projection(v2_r[0], v2_r[1], v2_r[2]);
                //a.triangle(v0[0], v0[1], v1[0], v1[1], v2[0], v2[1]);
                a.line(v0[0], v0[1],v1[0], v1[1]);
                a.line(v0[0], v0[1],v2[0], v2[1]);
                a.line(v1[0], v1[1],v2[0], v2[1]);
            }
        }
        a.popMatrix();
    }

    private float[] rotate(Vec3d vertice, float angleX, float angleY, float angleZ){
        float[][] rotationMatrix = new float[][]{
                {(float)(Math.cos(angleX) * Math.cos(angleY)), (float)(Math.cos(angleX) * Math.sin(angleY) * Math.sin(angleZ) - Math.sin(angleX) * Math.cos(angleZ)), (float)(Math.cos(angleX) * Math.sin(angleY) * Math.cos(angleZ) + Math.sin(angleX) * Math.sin(angleY))},
                {(float)(Math.sin(angleX) * Math.cos(angleY)), (float)(Math.sin(angleX) * Math.sin(angleY) * Math.sin(angleZ) - Math.cos(angleX) * Math.cos(angleZ)), (float)(Math.sin(angleX) * Math.sin(angleY) * Math.cos(angleZ) - Math.cos(angleX) * Math.sin(angleY))},
                {(float)(-Math.sin(angleY))                  , (float)(Math.cos(angleY) * Math.sin(angleZ))                                                         , (float)(Math.cos(angleY) * Math.cos(angleZ))}};
        float x_rotated = (float)(rotationMatrix[0][0] * vertice.x + rotationMatrix[0][1] * vertice.y + rotationMatrix[0][1] * vertice.z);
        float y_rotated = (float)(rotationMatrix[1][0] * vertice.x + rotationMatrix[1][1] * vertice.y + rotationMatrix[1][1] * vertice.z);
        float z_rotated = (float)(rotationMatrix[2][0] * vertice.x + rotationMatrix[2][1] * vertice.y + rotationMatrix[2][1] * vertice.z);
        return new float[]{x_rotated, y_rotated, z_rotated};
    }
    private float[] projection(float x, float y, float z){
        float x_projected = (focalLength * x * 10) / (z * 10 + focalLength);
        float y_projected = (focalLength * y * 10) / (z * 10 + focalLength);
        return new float[]{x_projected, y_projected};
    }
}
