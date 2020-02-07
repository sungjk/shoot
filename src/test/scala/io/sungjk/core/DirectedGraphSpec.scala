package io.sungjk.core

import org.scalatest.flatspec.AnyFlatSpec

/**
 * Created by jeremy on 2020/02/04.
 */
class DirectedGraphSpec extends AnyFlatSpec {
    // 1 -> 2 -> 4
    // 1 -> 3 -> 4
    private val directedGraph = DirectedGraph(List(
        Edge(Node(1), Node(2)),
        Edge(Node(1), Node(3)),
        Edge(Node(2), Node(4)),
        Edge(Node(3), Node(4))
    ))

    private val cycleGraph = DirectedGraph(List(
        Edge(Node(1), Node(2)),
        Edge(Node(2), Node(3)),
        Edge(Node(3), Node(1))
    ))

    "The generated graph" should "be the same" in {
        val expected = Map[Node, Set[Node]](
            Node(1) -> Set(Node(2), Node(3)),
            Node(2) -> Set(Node(4)),
            Node(3) -> Set(Node(4)),
            Node(4) -> Set.empty
        )
        assert((directedGraph.graph.keySet -- expected.keySet).isEmpty)
        assert((expected.keySet -- directedGraph.graph.keySet).isEmpty)
    }

    "bfs" should "return the empty list when given a node that does not exist" in {
        assert(directedGraph.bfs(Node(42)) == List.empty)
    }

    "bfs" should "return all paths" in {
        val expected = List(
            Set(Node(1), Node(2), Node(4)),
            Set(Node(1), Node(3), Node(4))
        )
        assert(directedGraph.bfs(Node(1)) == expected)
    }

    "bfs" should "return the visited node on cycle" in {
        val expected = List(Set(Node(1), Node(2), Node(3)))
        assert(cycleGraph.bfs(Node(1)) == expected)
    }

    "findPaths" should "return empty list when given a start node that does not exist" in {
        assert(directedGraph.findPaths(Node(42), Node(4)) == List.empty)
    }

    "findPaths" should "return empty list when given an end node that does not exist" in {
        assert(directedGraph.findPaths(Node(1), Node(42)) == List.empty)
    }

    "findPaths" should "return all available paths from start to end" in {
        val expected = List(
            Set(Node(1), Node(2), Node(4)),
            Set(Node(1), Node(3), Node(4))
        )
        assert(directedGraph.findPaths(Node(1), Node(4)) == expected)
    }
}
