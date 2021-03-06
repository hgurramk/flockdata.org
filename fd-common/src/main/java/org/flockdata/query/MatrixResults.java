/*
 * Copyright (c) 2012-2014 "FlockData LLC"
 *
 * This file is part of FlockData.
 *
 * FlockData is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FlockData is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FlockData.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flockdata.query;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collection;

/**
 * Encapsulates edges and nodes that make up a result suitable for matrix analysis
 *
 * User: mike
 * Date: 12/06/14
 * Time: 2:17 PM
 */
public class MatrixResults {
    private long sampleSize;
    private long totalHits;
    Collection<EdgeResult> edges;  // From To
    Collection<KeyValue> nodes;    // Lookup table if edges contains just Ids

    public MatrixResults (){}

    public MatrixResults(Collection<EdgeResult> edgeResults) {
        this();
        setEdges(edgeResults);
    }

    public Collection<EdgeResult> getEdges() {
        return edges;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Collection<KeyValue> getNodes() {
        return nodes;
    }

    public void setEdges(Collection<EdgeResult> edges) {
        this.edges = edges;
    }

    public void setNodes(Collection<KeyValue> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "MatrixResults{" +
                "edges=" + edges.size() +
                "nodes=" + nodes.size() +
                '}';
    }

    public void setSampleSize(long sampleSize) {
        this.sampleSize = sampleSize;
    }

    public long getSampleSize() {
        return sampleSize;
    }

    public void setTotalHits(long matchingResults) {
        this.totalHits = matchingResults;
    }

    public long getTotalHits() {
        return totalHits;
    }
}
