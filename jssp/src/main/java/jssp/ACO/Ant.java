package jssp.ACO;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Ant {

    static INDArray mat1 = Nd4j.create(new float[]{1,2,3,4,5,6,7,8},new int[]{2,4});



    public Ant(){ }


    public static void main(String[] args) {
        System.out.println(Ant.mat1);

    }

}
