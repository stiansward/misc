package oblig5;

public class ConvexHullPar extends ConvexHull {
	int maxThreads, threads, start, stop, mtLvl;

	public ConvexHullPar(int n) {
		super(n);
		maxThreads = Runtime.getRuntime().availableProcessors();
	}

	public IntList findCoHull() {
		int dist;
		il = new IntList();
		IntList subset1 = new IntList();
		IntList subset2 = new IntList();
		Thread t1, t2;

		// Find highest level to do parallelization
		mtLvl = 1;
		while (mtLvl * mtLvl <= maxThreads) {
			mtLvl++;
		}

		// Find start and stop for the first line, and maximum X and Y values
		for (int i = 0; i < n; i++) {
			if (x[i] < x[start] || (x[i] == x[start] && y[i] < y[start]))
				start = i;
			else if (x[i] > x[stop] || (x[i] == x[stop] && y[i] > y[stop]))
				stop = i;
			if (y[i] > MAX_Y)
				MAX_Y = y[i];
		}
		MAX_X = x[stop];

		// Reduce points for each line
		for (int i = 0; i < n; i++) {
			if ((dist = distanceToLine(start, stop, i)) <= 0 && i != start && i != stop)
				subset1.add(i);
			if (dist >= 0)
				subset2.add(i);
		}

		t1 = new Thread(new Worker(start, stop, mtLvl, 1, subset1));
		t2 = new Thread(new Worker(stop, start, mtLvl, 1, subset2));
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		il.add(start);
		il.append(subset1);
		il.append(subset2);

		return il;
	}

	private class Worker implements Runnable {
		int start, stop, mtLvl, curLvl;
		IntList subset;
		Thread t1, t2;

		public Worker(int start, int stop, int mtLvl, int curLvl, IntList subset) {
			this.start = start;
			this.stop = stop;
			this.mtLvl = mtLvl;
			this.curLvl = curLvl;
			this.subset = subset;
		}

		public void run() {
			findCoHullRec(start, stop, subset);
		}

		private void findCoHullRec(int start, int stop, IntList subset) {
			int bestPoint = stop, dist, bestDist = 0;

			for (int i = 0; i < subset.size(); i++) {
				int p = subset.get(i);
				if ((dist = distanceToLine(start, stop, p)) <= bestDist) {
					if (dist < bestDist || isBetween(start, stop, p)) {
						bestPoint = p;
						bestDist = dist;
					}
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
				if (curLvl <= mtLvl) {
					t1 = new Thread(new Worker(start, bestPoint, mtLvl, curLvl + 1, subset1));
					t2 = new Thread(new Worker(bestPoint, stop, mtLvl, curLvl + 1, subset2));
					t1.start();
					t2.start();
					try {
						t1.join();
						t2.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					subset.clear();
					subset.append(subset1);
					subset.add(bestPoint);
					subset.append(subset2);
				} else {
					subset.clear();
					findCoHullRec(start, bestPoint, subset1);
					subset.append(subset1);
					subset.add(bestPoint);
					findCoHullRec(bestPoint, stop, subset2);
					subset.append(subset2);
				}
			} else
				subset.clear();
		}
	}
}
