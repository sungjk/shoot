package io.sungjk.core

import org.scalatest.flatspec.AnyFlatSpec

/**
 * Created by jeremy on 2020/02/04.
 */
class DirectedGraphSpec extends AnyFlatSpec {
    "The generated graph" should "be the same" in {
        val graph = DirectedGraph(List(
            Edge(Node(1), Node(2)),
            Edge(Node(2), Node(3)),
            Edge(Node(3), Node(1)),
            Edge(Node(1), Node(4)),
            Edge(Node(1), Node(5))
        ))
        val expected = Map[Node, Set[Node]](
            Node(1) -> Set(Node(1), Node(4), Node(5)),
            Node(2) -> Set(Node(3)),
            Node(3) -> Set(Node(1)),
            Node(4) -> Set(),
            Node(5) -> Set()
        )
        assert((graph.graph.keySet -- expected.keySet).isEmpty)
        assert((expected.keySet -- graph.graph.keySet).isEmpty)
    }

    "bfs" should "return the empty list when given a node that does not exist" in {
        val graph = DirectedGraph(List(
            Edge(Node(1), Node(2)),
            Edge(Node(2), Node(3)),
            Edge(Node(3), Node(1)),
            Edge(Node(1), Node(4)),
            Edge(Node(1), Node(5))
        ))
        assert(graph.bfs(Node(42)) == List.empty)
    }

    "bfs" should "return all paths" in {
        val graph = DirectedGraph(List(
            Edge(Node(1), Node(2)),
            Edge(Node(1), Node(3)),
            Edge(Node(2), Node(4)),
            Edge(Node(3), Node(4))
        ))
        val expected = List(
            Set(Node(1), Node(2), Node(4)),
            Set(Node(1), Node(3), Node(4))
        )
        assert(graph.bfs(Node(1)) == expected)
    }

    "bfs" should "return the visited node on cycle" in {
        val graph = DirectedGraph(List(
            Edge(Node(1), Node(2)),
            Edge(Node(2), Node(3)),
            Edge(Node(3), Node(1))
        ))
        val expected = List(Set(Node(1), Node(2), Node(3)))
        assert(graph.bfs(Node(1)) == expected)
    }
}
