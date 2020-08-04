/*
 * Based on an implementation of Dijkstra's written by Neeraj Mishra
 * Source: https://www.thecrazyprogrammer.com/2014/03/dijkstra-algorithm-for-finding-shortest-path-of-a-graph.html
 */

#include "dijkstra.h"

void print_pred(int* pred, int size) {
  int i;

  printf("pred: ");
  for (i = 0; i < size; i++) {
    printf("[%d]%s", pred[i], (i == size-1)?"\n":" ");
  }
}

void dijkstra(int adj[MAX_SIZE][MAX_SIZE], int size, int startnode, int* pred) {
  int cost[size][size], distance[size], visited[size],
      count, mindistance, nextnode = 0, i, j;

  for (i = 0; i < size; i++) {
    for (j = 0; j < size; j++) {
      if (adj[i][j] == 0) {
        cost[i][j] = INFINITY;
      } else {
        cost[i][j] = adj[i][j];
      }
    }
  }

  for (i = 0; i < size; i++) {
    distance[i] = cost[startnode][i];
    pred[i] = startnode;
    visited[i] = 0;
  }

  distance[startnode] = 0;
  visited[startnode] = 1;
  count = 1;

  while (count < size - 1) {
    mindistance = INFINITY;

    for (i = 0; i < size; i++) {
      if (distance[i] < mindistance && !visited[i]) {
        mindistance = distance[i];
        nextnode = i;
      }
    }

    visited[nextnode] = 1;
    for (i = 0; i < size; i++) {
      if (!visited[i]) {
        if (mindistance + cost[nextnode][i] < distance[i]) {
          distance[i] = mindistance + cost[nextnode][i];
          pred[i] = nextnode;
        }
      }
    }
    count++;
  }
}
/* main-function for testing using the connections specified in run_1.sh
int main(int argc, char** argv) {
  int i;
  int j;
  int size;
  int* pred;

  size = 8;
  pred = malloc(size * sizeof(int));

  map[0] = 1;
  map[1] = 11;
  map[2] = 13;
  map[3] = 17;
  map[4] = 19;
  map[5] = 101;
  map[6] = 103;
  map[7] = 107;

  int adj[MAX_SIZE][MAX_SIZE];
  for (i = 0; i < size; i++) {
    for (j = 0; j < size; j++) {
      adj[i][j] = 0;
    }
  }

  adj[0][1] = 2;
  adj[0][6] = 6;
  adj[1][0] = 2;
  adj[1][2] = 7;
  adj[1][4] = 2;
  adj[2][1] = 7;
  adj[2][3] = 3;
  adj[2][5] = 4;
  adj[3][2] = 3;
  adj[3][7] = 2;
  adj[4][1] = 2;
  adj[4][5] = 2;
  adj[4][6] = 1;
  adj[5][2] = 4;
  adj[5][4] = 2;
  adj[5][7] = 2;
  adj[6][0] = 6;
  adj[6][4] = 1;
  adj[6][7] = 4;
  adj[7][3] = 2;
  adj[7][5] = 2;
  adj[7][6] = 4;

  dijkstra(adj, size, 0, pred);

  print_pred(pred, size);

  free(pred);
  return EXIT_SUCCESS;
}
*/
