package demos.sphfluid;


/**
 * Kernel class, consists of 3 different kernel types
 * poly6 kernel, spiky kernel used for pressure computation and
 * a viscosity kernel used for the viscosity computation
 *
 * Brendan Galea 260464461
 */
public abstract class Kernel {

  /** Distance of smoothing function */
  protected double d;

  public Kernel(double distance){
    d = distance;
  }

  /**
   * Scalar weighting function
   * @param r The absolute value of the distance between particles
   * @return The scalar weighting
   */
  public abstract double weight(double r);
  public abstract double gradient(double r);
  public abstract double laplacian(double r);


  public double getDistance(){
    return d;
  }


  public static class Poly6 extends Kernel{
    private double precompWeight;
    private double precompLaplacian;
    private double precomGradient;
    private double d2;

    public Poly6(double distance){
      super(distance);
      d2 = distance*distance;
      // do pre-computation for distance now
      precompWeight =  (315/(64*Math.PI*Math.pow(distance, 9)));
      precomGradient =  (-945/(32*Math.PI*Math.pow(distance, 9)));
      precompLaplacian =  (945/(8*Math.PI*Math.pow(distance, 9)));
    }

    public double weight(double r){
      double r2 = r * r;
      if (r2 > d2) return 0;
      double tmp = d2 - r2;
      return (precompWeight * tmp * tmp * tmp);
    }

    public double gradient(double r){
      double r2 = r * r;
      if (r2 > d2) return 0;
      double tmp = d2 - r2;
      return  (precomGradient * tmp * tmp);
    }

    public double laplacian(double r){
      double r2 = r * r;
      if (r2 > d2) return 0;
      double tmp = d2 - r2;
      return  (precompLaplacian* tmp * (r2 - 0.75*tmp));
    }
  }


  public static class Spiky extends Kernel{
    private double precompWeight;
    private double precomGradient;

    public Spiky(double distance){
      super(distance);
      // do pre-computation for distance now
      precompWeight =  (15/(Math.PI*Math.pow(distance, 6)));
      precomGradient =  (-45/(Math.PI*Math.pow(distance, 6)));
    }

    public double weight(double r){
      double tmp = d - r;
      return precompWeight * tmp * tmp * tmp;
    }

    public double gradient(double r){
      if (r > d) return 0;
      double tmp = d - r;
      return (precomGradient/r) * tmp * tmp;
    }

    public double laplacian(double r){
      if (r > d) return 0;
      // does nothing, don't use spiky's laplacian
      return 0;
    }
  }


  public static class CubicSpline2D extends Kernel {

    private double normalizationFactor;

    public CubicSpline2D(double distance){
      super(distance);
      normalizationFactor = 1.0 / (6.9115035237383085 * distance);
    }

    @Override
    public double weight(double r) {
      double q = r / this.d;
      if (q > 2) return 0;
      if (q >= 1) return normalizationFactor * Math.pow(2 - q, 3);
      return normalizationFactor * (Math.pow(2 - q, 3) - 4 * Math.pow(1 - q, 3));
    }

    @Override
    public double gradient(double r) {
      double q = r / this.d;
      if (q > 2) return 0;
      if (q >= 1) return normalizationFactor * -3.0 * (2 - q) * (2 - q);
      return normalizationFactor * (-3.0 * (2 - q) * (2 - q) + 12 * (1 - q) * (1 - q));
    }

    @Override
    public double laplacian(double r) {
      double q = r / this.d;
      if (q > 2) return 0;
      if (q >= 1) return normalizationFactor * 6.0 * (2 - q);
      return normalizationFactor * (6.0 * (2 - q) - 24.0 * (1 - q));

    }
  }


  public static class Viscosity extends Kernel {
    private double precompWeight;
    private double precompLaplacian;
    private double precomGradient;
    private double d2;
    private double d3;

    public Viscosity(double distance){
      super(distance);
      d2 = distance*distance;
      d3 = d2 * distance;
      // do pre-computation for distance now
      precompWeight =  (15/(2*Math.PI*Math.pow(distance, 3)));
      precomGradient =  (15/(2*Math.PI*Math.pow(distance, 3)));
      precompLaplacian =  (45/(Math.PI*Math.pow(distance, 5)));
    }

    public double weight(double r){
      if (r > d) return 0;
      double v1 = (-r*r*r)/(2*d3);
      double v2 = r*r/d2;
      double v3 = d/(2*r);
      return precompWeight*(v1+v2+v3-1);
    }

    public double gradient(double r){
      if (r > d) return 0;
      double v1 = (-3*r)/(2*d3);
      double v2 = 2/d2;
      double v3 = -d/(2*r*r*r);
      return precomGradient*(v1+v2+v3);
    }

    public double laplacian(double r){
      if (r > d) return 0;
      return precompLaplacian * (1-r/d);
    }
  }



  public static void main(String args[]) {

    double h = 0.01;
    double iterations = 1000000;
    Kernel spline = new CubicSpline2D(h);

    // integrate
    double sum = 0;
    for (int i = 0; i < iterations; i++) {
      double r = h * i / iterations;
      if (spline.weight(r) < 0)
        System.out.println("Spline negative" + r);
      sum += 2 * Math.PI * r * spline.weight(r) * (1.0 / iterations);
    }
    System.out.println("Integral is " + sum);
    System.out.println(spline.gradient(0));
  }

}