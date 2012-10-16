package com.eric.codejam;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

public class BruteForce implements PrisonSelectionAlgorithm {

	@Override
	public int findMinCost(int segStart, int segEnd, List<Integer> toBeFreed) {
		int minCost = Integer.MAX_VALUE;

		log.debug("findMinCost {} {} {}", segStart, segEnd, toBeFreed);
		if (segEnd <= segStart) {
			return 0;
		}

		if (toBeFreed.size() == 0) {
			return 0;
		}

		if (toBeFreed.size() == 1) {
			return segEnd - segStart;
		}

		int minIdx = -1;

		for (int i = 0; i < toBeFreed.size(); ++i) {
			int prisIdx = toBeFreed.get(i);

			Preconditions.checkState(!(prisIdx > segEnd || prisIdx < segStart));

			int cost = segEnd - segStart;

			if (i > 0) {
				List<Integer> cpy = new ArrayList<>(toBeFreed);
				cpy = cpy.subList(0, i);
				cost += findMinCost(segStart, prisIdx - 1, cpy);
			}

			if (i < toBeFreed.size() - 1) {
				List<Integer> cpy = new ArrayList<>(toBeFreed);
				cpy = cpy.subList(i + 1, toBeFreed.size());
				cost += findMinCost(prisIdx + 1, segEnd, cpy);
			}

			minCost = Math.min(minCost, cost);
			if (minCost == cost) {
				minIdx = prisIdx;
			}

		}

		log.debug(
				"findMinCost return {} freed prisoner {} params start {}  end {} to be freed {}",
				minCost, minIdx, segStart, segEnd, toBeFreed);
		return minCost;

	}

}
