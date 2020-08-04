#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/time.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "dijkstra.h"
#include "common.h"

typedef struct client {
    int fd;
    char *ip;
    unsigned short port;
    uint16_t id;
} client_s;

int base_port, num_nodes, remaining_nodes, server_socket, startnode;
uint16_t* dijkstra_map;
client_s** clients;
node_info** nodes;

void setup_data();
void cleanup_data();
int add_client(int socket, struct sockaddr_in *client_addr, socklen_t addrlen);
void remove_client(int fd);
void get_node_info(int listener);
int create_server_socket();
void create_routing_tables();
uint16_t map_get_pos(uint16_t);
