CFLAGS = -Wall -Wpedantic -std=gnu11 -g
BINARIES = routing_server node

.PHONY: all clean run

all: $(BINARIES)

routing_server: server.c protocol.c dijkstra.c
	gcc $(CFLAGS) $^ -o $@

node: node.c protocol.c dijkstra.c print_lib/print_lib.c
	gcc $(CFLAGS) $^ -o $@

run:
	./run_1.sh 23548
	./run_2.sh 43548

clean:
	rm -f routing_server
	rm -f node
