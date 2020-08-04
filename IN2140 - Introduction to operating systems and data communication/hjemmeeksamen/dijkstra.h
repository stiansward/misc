#ifndef __DIJKSTRA_H__
#define __DIJKSTRA_H__

#include <stdio.h>
#include <stdlib.h>

#define INFINITY 999999
#define MAX_SIZE 10

int map[MAX_SIZE];

void dijkstra(int[MAX_SIZE][MAX_SIZE], int, int, int*);
void print_pred(int*, int);

#endif
