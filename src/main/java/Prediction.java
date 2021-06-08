import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import ru.tinkoff.invest.openapi.models.market.Candle;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Prediction {

    public static double[] getCandlesPrediction(List<Candle> candles, List<OffsetDateTime> futureDates){
        List<Double> x1 = new ArrayList<Double>();
        List<Double> x2 = new ArrayList<Double>();
        List<Double> y1 = new ArrayList<Double>();

        for (var item : candles) {
            Long ticks = Date.from(item.time.toInstant()).getTime();
            x1.add(ticks.doubleValue());
            y1.add(item.highestPrice.doubleValue());
        }

        for (var item : futureDates) {
            Long ticks = Date.from(item.toInstant()).getTime();
            x2.add(ticks.doubleValue());
        }

        double[] x1d = new double[x1.size()];
        double[] x2d = new double[x2.size()];
        double[] y1d = new double[y1.size()];
        for (int i = 0; i< x1d.length; i++){
            x1d[i] = x1.get(i);
        }
        for (int i = 0; i< x2d.length; i++){
            x2d[i] = x2.get(i);
        }
        for (int i = 0; i< y1d.length; i++){
            y1d[i] = y1.get(i);
        }
        double[] y2 = interpolateLinear(x1d, y1d, x2d);

        System.out.println(y2);

        return y2;
    }

    public static double[] interpolateLinear(double[] x1, double[] y1, double[] x2) {
        final PolynomialSplineFunction function = new LinearInterpolator().interpolate(x1, y1);
        final PolynomialFunction[] splines = function.getPolynomials();
        final PolynomialFunction firstFunction = splines[0];
        final PolynomialFunction lastFunction = splines[splines.length - 1];

        final double[] knots = function.getKnots();
        final double firstKnot = knots[0];
        final double lastKnot = knots[knots.length - 1];

        double[] resultList = Arrays.stream(x2).map(aDouble -> {
            if (aDouble > lastKnot) {
                return lastFunction.value(aDouble - knots[knots.length - 2]);
            } else if (aDouble < firstKnot)
                return firstFunction.value(aDouble - knots[0]);
            return function.value(aDouble);
        }).toArray();
        return resultList;
    }
}