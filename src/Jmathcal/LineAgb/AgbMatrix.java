package Jmathcal.LineAgb;

import java.math.BigDecimal;
import java.util.Arrays;

public class AgbMatrix {

    private AgbVector[] vectors;
    private final int MatrixLength;
    private final int VectorLength;

    public AgbMatrix(int MatrixLength, int VectorLength) {
        this.MatrixLength = MatrixLength;
        this.VectorLength = VectorLength;
        vectors = new AgbVector[MatrixLength];
    }

    public AgbMatrix(BigDecimal[][] matrix) {
        MatrixLength = matrix.length;
        int vectorSize = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (vectorSize < matrix[i].length) vectorSize = matrix[i].length;
        }
        VectorLength = vectorSize;
        this.vectors = new AgbVector[MatrixLength];

        for (int i = 0; i < MatrixLength; i++) {
            this.addVector(new AgbVector(VectorLength), i);
            for (int j = 0; j < matrix[i].length; j++) {
                this.addValue(matrix[i][j], i, j);
            }
        }
    }

    public int getMatrixLength() {
        return MatrixLength;
    }

    public int getVectorLength() {
        return VectorLength;
    }

    public BigDecimal getValue(int indexInMatrix, int indexInVector) {
        return vectors[indexInMatrix].getValue(indexInVector);
    }

    public AgbVector getVector(int indexInMatrix) {
        return vectors[indexInMatrix].expandVector(0);
    }

    public void addValue(BigDecimal value, int indexInMatrix, int indexInVector) {
        if (vectors[indexInMatrix] == null)
            vectors[indexInMatrix] = new AgbVector(indexInVector);
        vectors[indexInMatrix].addValue(value, indexInVector);
    }

    public void addVector(AgbVector vector, int indexInMatrix) {
        if (vector.getLength() != VectorLength) {
            throw new VectorOutOfMatrixBoundException();
        } else if(vectors[indexInMatrix] != null) {
            throw new ArithmeticException("spaced already filled.");
        } else {
            vectors[indexInMatrix] = vector;
        }
    }

    public AgbMatrix multiply (BigDecimal scalar) {

        AgbMatrix returnMatrix = new AgbMatrix(MatrixLength, VectorLength);

        for (int i = 0; i < MatrixLength; i++) {
            returnMatrix.addVector(vectors[i].multiply(scalar), i);
        }

        return returnMatrix;
    }

    public AgbVector getVectorByVIndex(int indexInVector) {
        AgbVector returnVector = new AgbVector(MatrixLength);

        for (int i = 0; i < MatrixLength; i++) {
            returnVector.addValue(vectors[i].getValue(indexInVector), i);
        }

        return returnVector;
    }

    public AgbMatrix add(AgbMatrix augend) {

        AgbMatrix returnMatrix = new AgbMatrix(MatrixLength, VectorLength);

        if (!this.ifSameSize(augend))
            throw new ArithmeticException("adding not equal size Matrix");

        for (int i = 0; i < MatrixLength; i++) {
            returnMatrix.addVector(this.vectors[i].add(augend.getVector(i)), i);
        }

        return returnMatrix;
    }

    public AgbVector multiplyByVector(AgbVector multiplicand) {
        return multiplicand.multiplyByMatrix(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + MatrixLength;
        result = prime * result + VectorLength;
        result = prime * result + Arrays.hashCode(vectors);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AgbMatrix other = (AgbMatrix) obj;
        if (MatrixLength != other.MatrixLength)
            return false;
        if (VectorLength != other.VectorLength)
            return false;
        if (!Arrays.equals(vectors, other.vectors))
            return false;
        return true;
    }

    public boolean ifSameSize(AgbMatrix other) {
        if (MatrixLength != other.MatrixLength)
            return false;
        if (VectorLength != other.VectorLength)
            return false;
        return true;   
    }

    @Override
    public String toString() {
        String returnValue = "";
        for (int i = 0; i < vectors.length; i++) {
            returnValue = returnValue + vectors[i].toString();
        }
        return returnValue;
    }

    public static AgbMatrix getZeroMatrix(int MatrixLength, int VectorLength) {
        AgbMatrix returnMatrix = new AgbMatrix(MatrixLength, VectorLength);

        for (int i = 0; i < MatrixLength; i++) {
            returnMatrix.addVector(AgbVector.getZeroVector(VectorLength), i);
        }

        return returnMatrix;
    }

}

