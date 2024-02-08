package core.helpers;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;

public class OrbitPathCalculator {
	private double angularSpeed;
	private int steps;

	public OrbitPathCalculator(double angularSpeed, int steps) {
		this.angularSpeed = angularSpeed;
		this.steps = steps;
	}

	// Calculate the next x points for smoothing the path to the orbit
	public List<Vector2f> calculateSmoothOrbitPath(float currentAngle, float shipX, float shipY, double targetX, double targetY, double radius) {
		List<Vector2f> pathPoints = new ArrayList<>();
		double distanceToOrbitCenter = Math.sqrt(Math.pow(targetX - shipX, 2) + Math.pow(targetY - shipY, 2));
		double initialAngle = Math.atan2(shipY - targetY, shipX - targetX);

		// Determine direction of orbit on the first step
		angularSpeed = determineOrbitDirection(currentAngle, shipX, shipY, initialAngle, distanceToOrbitCenter, radius, targetX, targetY);

		for (int step = 0; step < steps; step++) {
			pathPoints.add(makePoint(step, initialAngle, angularSpeed, distanceToOrbitCenter, radius, targetX, targetY));
		}

		return pathPoints;
	}

	private double determineOrbitDirection(float currentAngle, float shipX, float shipY, double initialAngle, double distanceToOrbitCenter, double radius, double cx, double cy) {
		Vector2f pointCW = makePoint(1, initialAngle, angularSpeed, distanceToOrbitCenter, radius, cx, cy); // Sample point for clockwise
		Vector2f pointCCW = makePoint(1, initialAngle, -angularSpeed, distanceToOrbitCenter, radius, cx, cy); // Sample point for counter-clockwise

		// Simulate current position as a Vector2f for this example
		Vector2f currentPosition = new Vector2f(shipX, shipY);

		Vector2f minPoint = minTurnAngleToAnyPoint(currentAngle, currentPosition, pointCW, pointCCW);
		return minPoint.equals(pointCCW) ? -angularSpeed : angularSpeed;
	}

	// Create a point on the path based on the step
	private Vector2f makePoint(int step, double initialAngle, double angularSpeed, double distanceToOrbitCenter, double radius, double cx, double cy) {
		double t = (double) step / (steps - 1);

		t = Interpolation.easeInOutExpo(t, -150.0D);

		double targetAngle = initialAngle + angularSpeed * Math.toRadians(step);
		double lerpedRadius = Interpolation.linear(distanceToOrbitCenter, radius, t);

		float targetX = (float) (cx + lerpedRadius * Math.cos(targetAngle));
		float targetY = (float) (cy + lerpedRadius * Math.sin(targetAngle));

		return new Vector2f(targetX, targetY);
	}

	private static float normalizeAngle(float angle) {
		angle %= 360;
		
		if (angle > 180) {
			angle -= 360;
		} else if (angle < -180) {
			angle += 360;
		}

		return angle;
	}

	private static float calculateAngleToPoint(Vector2f from, Vector2f to) {
		float angle = (float) Math.toDegrees(Math.atan2(to.y - from.y, to.x - from.x));
		return normalizeAngle(angle);
	}

	public static Vector2f minTurnAngleToAnyPoint(float currentAngle, Vector2f currentPosition, Vector2f... targetPositions) {
		float minAngle = Float.MAX_VALUE;
		Vector2f minAngleVec = new Vector2f();

		for (Vector2f targetPosition : targetPositions) {
			float targetAngle = calculateAngleToPoint(currentPosition, targetPosition);
			float angleDifference = normalizeAngle(targetAngle - currentAngle);

			if (Math.abs(angleDifference) < Math.abs(minAngle)) {
				minAngle = angleDifference;
				minAngleVec = targetPosition;
			}
		}

		return minAngleVec;
	}
}