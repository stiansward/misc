package oblig5;

public class ConvexHull {
	int n, MAX_X, MAX_Y;
	int[] x, y;
	IntList il;

	public ConvexHull(int n) {
		this.n = n;
		x = new int[n];
		y = new int[n];
		new NPunkter17(n).fyllArrayer(x, y);
	}

	public IntList findCoHull() {
		int start = 0, stop = 0, dist;
		MAX_X = 0;
		MAX_Y = 0;
		il = new IntList();
		IntList subset1 = new IntList();
		IntList subset2 = new IntList();

		// Find start/stop points and max values
		for (int i = 0; i < n; i++) {
			if (x[i] < x[start] || (x[i] == x[start] && y[i] < y[start]))
				start = i;
			if (x[i] > x[stop] || (x[i] == x[stop] && y[i] > y[stop]))
				stop = i;
			if (y[i] > MAX_Y)
				MAX_Y = y[i];
		}
		MAX_X = x[stop];

		// Reduce points for each line
		for (int i = 0; i < n; i++) {
			if (i != start && i != stop) {
				dist = distanceToLine(start, stop, i);
				if (dist <= 0)
					subset1.add(i);
				else
					subset2.add(i);
			}
		}

		il.add(start);
		findCoHullRec(start, stop, subset1);
		il.add(stop);
		findCoHullRec(stop, start, subset2);

		return il;
	}

	private void findCoHullRec(int start, int stop, IntList subset) {
		int bestPoint = stop, dist, bestDist = 0;

		for (int i = 0; i < subset.size(); i++) {
			int p = subset.get(i);
			if ((dist = distanceToLine(start, stop, p)) <= bestDist 
					&& (dist < bestDist || isBetween(start, stop, p))) {
				bestPoint = p;
				bestDist = dist;
			}
		}

		if (bestPoint != stop) {
			IntList subset1 = new IntList();
			IntList subset2 = new IntList();
			for (int i = 0; i < subset.size(); i++) {
				int p = subset.get(i);
				if (distanceToLine(start, bestPoint, p) <= 0 && p != bestPoint) {
					subset1.add(p);
				}
				if (distanceToLine(bestPoint, stop, p) <= 0 && p != bestPoint) {
					subset2.add(p);
				}
			}
			findCoHullRec(start, bestPoint, subset1);
			il.add(bestPoint);
			findCoHullRec(bestPoint, stop, subset2);
		}
	}

	protected boolean isBetween(int start, int stop, int i) {
		return ((x[i] >= x[start] && x[i] <= x[stop]) || (x[i] <= x[start] && x[i] >= x[stop]))
				&& ((y[i] >= y[start] && y[i] <= y[stop]) || (y[i] <= y[start] && y[i] >= y[stop]));
	}

	protected int distanceToLine(int start, int stop, int p) {
		return (y[start] - y[stop]) * x[p] + (x[stop] - x[start]) * y[p] + (y[stop] * x[start] - y[start] * x[stop]);
	}
}
