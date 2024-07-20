import processing.core.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class VideoEngine {
    private final PApplet a;
    private final GUI gui;
    private final int minWidth = 500;
    private final int minHeight = 500;
    private float focalLength = 50;
    private ArrayList<float[]> rawObjects = new ArrayList<float[]>();
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
            rawObjects.add(gui.getMeshes());
            objects.add(gui.getMeshes());
        }
        if(gui.hasChanged()){
            updateVertices();
            renderBasic();
        }

        //if(a.millis() % 200 <= 20){renderBasic();}

    }
    private void updateVertices(){
        for(int i = 0; i < rawObjects.size(); i++){
            float[] raw_object = rawObjects.get(i);
            float[] parameter = parameters.get(i);
            float[] newObject = objects.get(i);
            for(int j = 0; j < raw_object.length; j += 9){

                float[] v0_raw = new float[]{raw_object[j], raw_object[j+1], raw_object[j+2]};
                float[] v1_raw = new float[]{raw_object[j+3], raw_object[j+4], raw_object[j+5]};
                float[] v2_raw = new float[]{raw_object[j+6], raw_object[j+7], raw_object[j+8]};

                float[] v0_s = scale(v0_raw, parameter);
                float[] v1_s = scale(v1_raw, parameter);
                float[] v2_s = scale(v2_raw, parameter);

                float[] v0_r = rotate(v0_s, parameter);
                float[] v1_r = rotate(v1_s, parameter);
                float[] v2_r = rotate(v2_s, parameter);

                float[] v0_t = translate(v0_r, parameter);
                float[] v1_t = translate(v1_r, parameter);
                float[] v2_t = translate(v2_r, parameter);

                newObject[j] = v0_t[0];
                newObject[j+1] = v0_t[1];
                newObject[j+2] = v0_t[2];
                newObject[j+3] = v1_t[0];
                newObject[j+4] = v1_t[1];
                newObject[j+5] = v1_t[2];
                newObject[j+6] = v2_t[0];
                newObject[j+7] = v2_t[1];
                newObject[j+8] = v2_t[2];
            }
            float[] sortedObject = sortVertices(newObject);
            objects.set(i, sortedObject);
        }
    }

    private void renderBasic(){
        a.background(150);
        a.pushMatrix();
        a.translate(a.width/2f, a.height/2f);
        a.stroke(64);
        a.strokeWeight(0.5f);
        for(int i = 0; i < objects.size(); i++){
            float[] object = objects.get(i);
            for(int j = 0; j < object.length; j += 9){
                float[] vert0 = {object[j], object[j+1], object[j+2]};
                float[] vert1 = {object[j+3], object[j+4], object[j+5]};
                float[] vert2 = {object[j+6], object[j+7], object[j+8]};
                float[] v0 = projection(vert0);
                float[] v1 = projection(vert1);
                float[] v2 = projection(vert2);
                float[] normal = normalVector(vert0, vert1, vert2);
                a.noStroke();
                a.fill((float)Math.cos(angle(new float[]{-1,0,-1}, normal)) * 200);
                a.triangle(v0[0], v0[1], v1[0], v1[1], v2[0], v2[1]);
                //a.line(v0[0], v0[1],v1[0], v1[1]);
                //a.line(v0[0], v0[1],v2[0], v2[1]);
                //a.line(v1[0], v1[1],v2[0], v2[1]);
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

    private float[] projection(float[] pos){
        float x_projected = (focalLength * pos[0] * 10f) / (pos[2] * 10f + focalLength);
        float y_projected = (focalLength * pos[1] * 10f) / (pos[2] * 10f + focalLength);
        return new float[]{x_projected, y_projected};
    }

    // Sorts vertices by distance, further first, closest last
    float[] sortVertices(float[] vertices){
        float[] sortedVertices = vertices;
        boolean isSorted;
        do{
            isSorted = true;
            for(int i = 0; i < sortedVertices.length-18; i += 9) {
                float[] v0_0 = {sortedVertices[i], sortedVertices[i+1], sortedVertices[i+2]};
                float[] v1_0 = {sortedVertices[i+3], sortedVertices[i+4], sortedVertices[i+5]};
                float[] v2_0 = {sortedVertices[i+6], sortedVertices[i+7], sortedVertices[i+8]};
                float[] v0_1 = {sortedVertices[i+9], sortedVertices[i+10], sortedVertices[i+11]};
                float[] v1_1 = {sortedVertices[i+12], sortedVertices[i+13], sortedVertices[i+14]};
                float[] v2_1 = {sortedVertices[i+15], sortedVertices[i+16], sortedVertices[i+17]};

                if(center(v0_1, v1_1, v2_1)[2] > center(v0_0, v1_0, v2_0)[2]){
                //if(Math.max(Math.max(v0_1[2], v1_1[2]), v2_1[2]) > Math.max(Math.max(v0_0[2], v1_0[2]), v2_0[2])){
                    float[] temp = {sortedVertices[i], sortedVertices[i+1], sortedVertices[i+2],
                            sortedVertices[i+3], sortedVertices[i+4], sortedVertices[i+5],
                            sortedVertices[i+6], sortedVertices[i+7], sortedVertices[i+8]};

                    sortedVertices[i] = sortedVertices[i+9];
                    sortedVertices[i+1] = sortedVertices[i+10];
                    sortedVertices[i+2] = sortedVertices[i+11];
                    sortedVertices[i+3] = sortedVertices[i+12];
                    sortedVertices[i+4] = sortedVertices[i+13];
                    sortedVertices[i+5] = sortedVertices[i+14];
                    sortedVertices[i+6] = sortedVertices[i+15];
                    sortedVertices[i+7] = sortedVertices[i+16];
                    sortedVertices[i+8] = sortedVertices[i+17];

                    sortedVertices[i+9] = temp[0];
                    sortedVertices[i+10] = temp[1];
                    sortedVertices[i+11] = temp[2];
                    sortedVertices[i+12] = temp[3];
                    sortedVertices[i+13] = temp[4];
                    sortedVertices[i+14] = temp[5];
                    sortedVertices[i+15] = temp[6];
                    sortedVertices[i+16] = temp[7];
                    sortedVertices[i+17] = temp[8];

                    isSorted = false;
                }
            }
        } while(!isSorted);
        return sortedVertices;
    }


    boolean intersect_triangle(float[] A, float[] B, float[] C, float[] rayOrigin, float[] rayDir) {
        float[] E1 = sub(B, A);
        float[] E2 = sub(C, A);
        float[] N = cross(E1, E2);
        float det = -dot(rayDir, N);
        float invdet = 1f/det;
        float[] AO = sub(rayOrigin, A);
        float[] DAO = cross(AO, rayDir);
        float u =  dot(E2,DAO) * invdet;
        float v = -dot(E1,DAO) * invdet;
        float t =  dot(AO,N)  * invdet;
        return (det >= 1e-6 && t >= 0.0 && u >= 0.0 && v >= 0.0 && (u+v) <= 1.0);
    }

    // -----------------------------------------------------------------------------------------------------------------
    // Linear Algebra Functions
    // -----------------------------------------------------------------------------------------------------------------

    float[] sum(float[] vect1, float[] vect2){
        // Check input
        if(vect1.length != vect2.length){
            throw new InvalidParameterException("Different Array Sizes: " + vect1.length + " and " + vect2.length);
        }
        // Make the calculation
        float[] sum = new float[vect1.length];
        for(int i = 0; i < vect1.length; i++){
            sum[i] = vect1[i] + vect2[i];
        }
        return sum;
    }

    float[] sub(float[] vect1, float[] vect2){
        // Check input
        if(vect1.length != vect2.length){
            throw new InvalidParameterException("Different Array Sizes: " + vect1.length + " and " + vect2.length);
        }

        // Make the calculation
        float[] sub = new float[vect1.length];
        for(int i = 0; i < vect1.length; i++){
            sub[i] = vect1[i] - vect2[i];
        }

        return sub;
    }

    float dot(float[] vect1, float[] vect2){
        // Check input
        if(vect1.length != vect2.length){
            throw new InvalidParameterException("Different Array Sizes: " + vect1.length + " and " + vect2.length);
        }

        // Make the calculation
        float dot = 0;
        for(int i = 0; i < vect1.length; i++){
            dot += vect1[i] * vect2[i];
        }

        return dot;
    }

    float[] cross(float[] vect1, float[] vect2){
        // Check input
        if(vect1.length != 3 || vect2.length != 3){
            throw new InvalidParameterException("Invalid Array Sizes: " + vect1.length + " and " + vect2.length);
        }

        // Make the calculation
        float cross_x = vect1[1] * vect2[2] - vect1[2] * vect2[1];
        float cross_y = vect1[2] * vect2[0] - vect1[0] * vect2[2];
        float cross_z = vect1[0] * vect2[1] - vect1[1] * vect2[0];

        return new float[]{cross_x, cross_y, cross_z};

    }

    private float[] center(float[] v0, float[] v1, float[] v2){
        return new float[]{(v0[0] + v1[0] + v2[0])/3f, (v0[1] + v1[1] + v2[1])/3f, (v0[2] + v1[2] + v2[2])/3f};
    }

    private float length(float[] vector){
        return (float)Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
    }

    private float angle(float[] vector0, float[] vector1){
        return (float)Math.acos( dot(vector0, vector1) / (length(vector0) * length(vector1)) );
    }
    private float[] normalize(float[] vector){
        float length = (float)Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        return new float[]{vector[0]/length, vector[1]/length, vector[2]/length};
    }


    private float[] normalVector(float[] v0, float[] v1, float[] v2){
        float[] vect1 = sub(v1, v0);
        float[] vect2 = sub(v2, v0);

        return cross(vect1, vect2);
    }
}
