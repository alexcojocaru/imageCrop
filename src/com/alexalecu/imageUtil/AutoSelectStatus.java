package com.alexalecu.imageUtil;

public enum AutoSelectStatus {
	Init,
	SelectBoundingRectangle,
	ReduceImageColors,
	FindEdgePoints,
	FindVertices,
	ComputeLargestRectangle,
	ComputeEdgeList,
	Canceled,
	Finished
}
