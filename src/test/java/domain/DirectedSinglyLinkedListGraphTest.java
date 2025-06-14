package domain;

import domain.list.ListException;
import domain.queue.QueueException;
import domain.stack.StackException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectedSinglyLinkedListGraphTest {

    @Test
    void test() {

        try {
            DirectedSinglyLinkedListGraph graph = new DirectedSinglyLinkedListGraph();
            for (char i = 'A'; i <= 'J'; i++) {
                graph.addVertex(i);
            }

            graph.addEdgeWeight('A', 'B', util.Utility.getPersonName());
            graph.addEdgeWeight('A', 'C', util.Utility.getPersonName());
            graph.addEdgeWeight('A', 'D', util.Utility.getPersonName());
            graph.addEdgeWeight('B', 'F', util.Utility.getPersonName());
            graph.addEdgeWeight('F', 'E', util.Utility.getPersonName());
            graph.addEdgeWeight('F', 'J', util.Utility.getPersonName());
            graph.addEdgeWeight('C', 'G', util.Utility.getPersonName());
            graph.addEdgeWeight('G', 'J', util.Utility.getPersonName());
            graph.addEdgeWeight('D', 'H', util.Utility.getPersonName());
            graph.addEdgeWeight('H', 'I', util.Utility.getPersonName());

            System.out.println(graph);  //toString
            System.out.println("DFS Transversal Tour: "+graph.dfs());
            System.out.println("BFS Transversal Tour: "+graph.bfs());

            //eliminemos vertices
            System.out.println("\nVertex deleted: E,J,I");
            graph.removeVertex('E');
            graph.removeVertex('J');
            graph.removeVertex('I');
            System.out.println(graph);  //toString

            System.out.println("Edge deleted: C-G, D-H, A-B");
            graph.removeEdge('C', 'G');
            graph.removeEdge('D', 'H');
            graph.removeEdge('A', 'B');
            System.out.println(graph);  //toString

        } catch (GraphException | ListException | StackException | QueueException e) {throw new RuntimeException(e);}
    }//end test


}