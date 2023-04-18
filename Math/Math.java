import org.joml.Vector2f;

public class Math {
	/*
	* The fast inverse square root function used in Quake is a famous algorithm that was first discovered and
	* implemented by John Carmack in the mid-1990s. It was used to compute the inverse square root of a 32-bit
	* floating-point number in a way that was both fast and accurate enough for use in 3D graphics calculations.
	*/
	public static float Q_rsqrt(float number) {
	    float x2, y;
	    int i;
	    final float threehalfs = 1.5F;

	    x2 = number * 0.5F;
	    y = number;
	    i = Float.floatToRawIntBits(y);
	    i = 0x5f3759df - (i >> 1);
	    y = Float.intBitsToFloat(i);
	    y = y * (threehalfs - (x2 * y * y));
	    return y;
	}
	
	// Gets a random uniform point around x0, y0.
	public static Vector2f randomUniformVector(float r) {
		double angle = Math.random() * Math.PI * 2.0F;
		double radius = Math.sqrt(Math.random()) * r;
		float x = (float) (radius * Math.cos(angle));
		float y = (float) (radius * Math.sin(angle));

		return new Vector2f(x, y);
	}

	// Gets a random uniform point around a vectors position.
	public static Vector2f randomUniformVector(float r, Vector2f vector) {
		return randomUniformVector(r).add(vector);
	}
}
