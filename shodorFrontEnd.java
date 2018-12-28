import java.util.*;
import Jama.*;
class shodorFrontEnd extends shodorMatrix
{
    public static void main()
    {
        shodorFrontEnd obj=new shodorFrontEnd();
        obj.control();
    }
    public void control()
    {
        int mat[][]=matrixMaker();
        int A[][]=getMatrix(mat);
        int B[][]=getMatrixAns(mat);
        int og[][]=A;
        if(A.length!=A[0].length)
        {
            A=matrixFiller(A);
        }
        double mat_A[][]=intToDouble(A);
        double mat_B[][]=intToDouble(B);
        Matrix a=new Matrix(mat_A);
        Matrix b=new Matrix(mat_B);
        Matrix Ans=step1(a, b);
        Ans=matrixToZero(Ans);
        if(infinitesimalToZero(a.det())!=0)
            Ans=addToMatrix(Ans,a.det());
        Ans=absoluter(Ans);
        Ans=zeroRemover(Ans);
        if(infinitesimalToZero(a.det())==0)
            Ans=answerMatrix(a,b,Ans,og);
        Ans=roundOff(Ans);
        Ans=factoriser(Ans);
        dispMatrix(Ans);
    }
    
    double roundOff(double a)
    {
        int ctr=0;
        while(a<1)
        {
            a=a*10;
            ctr++;
        }
        double b=a%1;
        if((b)<=0.01||(b)>=0.99)
        a=Math.rint(a);
        a=a/(Math.pow(10,ctr));
        return a;
    }
    
    Matrix roundOff(Matrix mat)
    {
        double[][] arr=mat.getArray();
        for(int i=0;i<arr.length;i++)
        {
            for(int j=0;j<arr[0].length;j++)
            arr[i][j]=roundOff(arr[i][j]);
        }
        Matrix matFinal=new Matrix(arr);
        return matFinal;
    }
    
    Matrix factoriser(Matrix a)
    {
        double A[][]=a.getArray();
        double min=A[0][0];
        for(int i=0;i<A.length;i++)
        {
            if(A[i][0]<min)
                min=A[i][0];
        }
        boolean flag=true;
        for(int i=0;i<A.length;i++)
        {
            if(A[i][0]%min!=0)
            {
                flag=false;
                break;
            }
        }
        if(flag)
        {
            for(int i=0;i<A.length;i++)
            {
                A[i][0]=A[i][0]/min;
            }
        }
        Matrix m=new Matrix(A);
        return m;
    }
    
    public boolean isZero(double a)
    {
        if(a<1.0E-13)
        return true;
        else
        return false;
    }
    
    double infinitesimalToZero(double n)
    {
        if(isZero(n))
        return 0.0;
        else
        return n;
    }

    public void dispMatrix(Matrix A)
    {
        double[][] a=A.getArray();
        for(int i=0;i<a.length;i++)
        {
            for(int j=0;j<a[0].length;j++)
            {
                System.out.print(a[i][j]+" ");
            }
            System.out.println();
        }
    }

    Matrix step1(Matrix a,Matrix b)
    {
        double det=-1;
        if(a.det()<1.0E-14)
        det=0;
        else
        det=a.det();
        if(det!=0) 
            return ((a.inverse()).times(b)).times(a.det());
        else
        {
            a=colRemover(a);
            return a.inverse().times(b);
        }///FTTY
    }

    Matrix absoluter(Matrix a)
    {
        double[][] A=a.getArray();
        for(int i=0;i<A.length;i++)
        {
            for(int j=0;j<A[0].length;j++)
            {
                A[i][j]=Math.abs(A[i][j]);
            }
        }
        Matrix b=new Matrix(A);
        return b;
    }

    Matrix zeroRemover(Matrix A)
    {
        double a[][]=A.getArray();
        int ctr=0;
        for(int i=0;i<a.length;i++)
        {
            if(a[i][0]==0)
                ctr++;
        }
        double b[][]=new double[a.length-ctr][1];
        ctr=0;
        for(int i=0;i<a.length;i++)
        {
            if(a[i][0]!=0)
            {
                b[ctr][0]=a[i][0];
                ctr++;
            }
        }
        Matrix B=new Matrix(b);
        return B;
    }

    Matrix addToMatrix(Matrix m, double a)
    {
        double[][] arr=m.getArray();
        double arrFinal[][]=new double[arr.length+1][arr[0].length];
        int i;
        for(i=0;i<arr.length;i++)
        {
            for(int j=0;j<arr[0].length;j++)
                arrFinal[i][j]=arr[i][j];
        }
        arrFinal[i][0]=a;
        Matrix mFinal=new Matrix(arrFinal);
        return mFinal;
    }
    
    Matrix matrixToZero(Matrix mat)
    {
        double[][] arr=mat.getArray();
        for(int i=0;i<arr.length;i++)
        {
            for(int j=0;j<arr[0].length;j++)
            arr[i][j]=infinitesimalToZero(arr[i][j]);
        }
        Matrix matFinal=new Matrix(arr);
        return matFinal;
    }

    Matrix colRemover(Matrix mat)
    {
        double[][] arr=mat.getArray();
        double[][] arrFinal=new double[arr.length][arr[0].length-1];
        for(int i=0;i<arr.length;i++)
        {
            for(int j=0;j<arrFinal[0].length;j++)
                arrFinal[i][j]=arr[i][j];
        }
        Matrix matFinal=new Matrix(arrFinal);
        return matFinal;
    }

    double lastValue(Matrix a, Matrix b, Matrix ans, int[][] og)
    {
        double[][] B=b.getArray();
        double[][] A=a.getArray();
        double[][] Ans=ans.getArray();
        int diff=og.length-og[0].length;
        int index=-1;
        for(int i=0;i<B.length;i++)//to get a non-zero coefficient of the last compound variable
        {
            if(B[i][0]!=0)
            {
                index=i;
                break;
            }
        }
        int len =Ans.length;
        double[][] row=new double[1][len];//to get the corresponding row from a
        double[][] col=new double[len][1];
        //for converting ans to col
        for(int i=0;i<len;i++)//copying ans
            col[i][0]=Ans[i][0];
        for(int i=0;i<len;i++)//copying the corresponding row from a
            row[0][i]=A[index][i];
        Matrix mRow=new Matrix(row);
        Matrix mCol=new Matrix(col);
        Matrix last=mRow.times(mCol);
        return (last.get(0,0))/B[index][0];
    }

    Matrix lastRemover(Matrix mat)
    {
        double[][] arr=mat.getArray();
        double[][] arrFinal=new double[arr.length-1][1];
        for(int i=0;i<arr.length-1;i++)
            arrFinal[i][0]=arr[i][0];
        Matrix matFinal=new Matrix(arrFinal);
        return matFinal;
    }

    Matrix answerMatrix(Matrix a, Matrix b, Matrix ans, int[][] og)
    {
        double last=lastValue(a,b,ans,og);
        int diff=og.length-og[0].length;
        ans=addToMatrix(ans,last);
        return ans;
    }
}