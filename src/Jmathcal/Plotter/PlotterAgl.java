package Jmathcal.Plotter;

public class PlotterAgl {
    public static void main(String[] args) {
        double p = 0.00001;
        interface NumFunction<T>{T evaluate(T... input);}
        NumFunction<Double> f = new NumFunction<Double>() {
            @Override
            public Double evaluate(Double... input) {
                return Math.pow(input[0],2)-3-input[1];
            };
        };

        NumFunction<Double> dfx = new NumFunction<Double>() {
            @Override
            public Double evaluate(Double... input) {
                return (f.evaluate(input[0]+p, input[1])-f.evaluate(input))/p;
            };
        };
        NumFunction<Double> dfy = new NumFunction<Double>() {
            @Override
            public Double evaluate(Double... input) {
                return (f.evaluate(input[0], input[1]+p)-f.evaluate(input))/p;
            };
        };
        NumFunction<Double> dfxx = new NumFunction<Double>() {
            @Override
            public Double evaluate(Double... input) {
                return (dfx.evaluate(input[0]+p, input[1])-dfx.evaluate(input))/p;
            };
        };
        NumFunction<Double> dfyy = new NumFunction<Double>() {
            @Override
            public Double evaluate(Double... input) {
                return (dfy.evaluate(input[0], input[1]+p)-dfy.evaluate(input))/p;
            };
        };
        NumFunction<Double> dfxy = new NumFunction<Double>() {
            @Override
            public Double evaluate(Double... input) {
                return (dfx.evaluate(input[0], input[1]+1)-dfx.evaluate(input))/p;
            };
        };
        Double[] input = {2.0,4.0};
        System.out.println(System.currentTimeMillis());
        double DFX = dfx.evaluate(input);
        double DFY = dfy.evaluate(input);
        double DFXX = dfxx.evaluate(input);
        double DFYY = dfyy.evaluate(input);
        double DFXY = dfxy.evaluate(input);
        System.out.println(Math.pow(DFX*DFX+DFY*DFY, 1.5)/(DFY*DFY*DFXX-2*DFX*DFY*DFXY+DFX*DFX*DFYY));
        System.out.println(System.currentTimeMillis());
        System.out.println((int)(-1535/6)*6);

        System.out.println("===============");
        double x = 1;
        double y = 0;
        while (Math.abs(f.evaluate(x,y)) > 0.0000001) {
            x = x - f.evaluate(x,y)/dfx.evaluate(x,y);
            System.out.println(x);
        }
    
    }

}
