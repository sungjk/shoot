package io.sungjk.core

/**
 * Created by jeremy on 2020/01/30.
 */
case class Edge(from: Node, to: Node)
case class Node(id: Int)

final class DirectedGraph(val graph: Map[Node, Set[Node]]) {
    private def contains(node: Node): Boolean = graph contains node

    def bfs(start: Node): List[Set[Node]] = {
        if (!contains(start)) {
            return List.empty
        }

        def bfs0(nodes: List[Node], visited: Set[Node]): List[Set[Node]] = {
            val neighbors = nodes.flatMap{ graph(_) }.distinct.filterNot{ visited.contains }
            if (neighbors.isEmpty) List(visited) else {
                neighbors.flatMap { neighbor =>
                    bfs0(List(neighbor), visited + neighbor)
                }
            }
        }

        bfs0(List(start), Set(start))
    }

    def findPaths(start: Node, end: Node, visited: Set[Node]): List[Node] = ???
}

object DirectedGraph {
    private def edgesToMap(edges: List[Edge]): Map[Node, Set[Node]] = {
        edges.foldLeft(Map.empty[Node, Set[Node]])((acc, edge) => {
            val curr = acc.getOrElse(edge.from, Set.empty[Node])
            val xs = acc.updated(edge.from, curr + edge.to)
            if (xs.contains(edge.to)) xs else xs.updated(edge.to, Set.empty[Node])
        })
    }

    def apply(edges: List[Edge]): DirectedGraph =
        new DirectedGraph(edgesToMap(edges))
}
