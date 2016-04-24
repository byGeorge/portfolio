package common;

public class BezierTiming {

	private double a, b, c, d;

	public BezierTiming(double c0x, double c0y, double c1x, double c1y) {
		a = c0x;
		b = c0y;
		c = c1x;
		d = c1y;
	}


	// Source: Golan Levin et al
	// http://www.flong.com/texts/code/shapers_bez/

	public double at(double time) {
		double y0a = 0.00; // initial y
		double x0a = 0.00; // initial x 
		double y1a = b;    // 1st influence y   
		double x1a = a;    // 1st influence x 
		double y2a = d;    // 2nd influence y
		double x2a = c;    // 2nd influence x
		double y3a = 1.00; // final y 
		double x3a = 1.00; // final x 

		double A =   x3a - 3*x2a + 3*x1a - x0a;
		double B = 3*x2a - 6*x1a + 3*x0a;
		double C = 3*x1a - 3*x0a;   
		double D =   x0a;

		double E =   y3a - 3*y2a + 3*y1a - y0a;    
		double F = 3*y2a - 6*y1a + 3*y0a;             
		double G = 3*y1a - 3*y0a;             
		double H =   y0a;

		// Solve for t given x (using Newton-Raphelson), then solve for y given t.
		// Assume for the first guess that t = x.
		double currentt = time;
		int nRefinementIterations = 5;
		for (int i=0; i < nRefinementIterations; i++){
			double currentx = xFromT (currentt, A,B,C,D); 
			double currentslope = slopeFromT (currentt, A,B,C);
			currentt -= (currentx - time)*(currentslope);
			currentt = constrain(currentt, 0,1);
		} 

		double y = yFromT(currentt,  E,F,G,H);
		return y;

	}



	protected static double slopeFromT(double t, double A, double B, double C) {
		double dtdx = 1.0/(3.0*A*t*t + 2.0*B*t + C); 
		return dtdx;
	}

	protected static double xFromT(double t, double A, double B, double C, double D) {
		double x = A*(t*t*t) + B*(t*t) + C*t + D;
		return x;
	}

	protected static double yFromT(double t, double E, double F, double G, double H) {
		double y = E*(t*t*t) + F*(t*t) + G*t + H;
		return y;
	}

	protected static double constrain(double value, double minimum, double maximum) {
		if (value < minimum)
			return minimum;
		if (value > maximum)
			return maximum;
		return value;
	}


}
