#include "common.h"
#include "print_lib/print_lib.h"

#define LOCALHOST "127.0.0.1"

typedef struct udp_msg {
  uint16_t packet_len;
  uint16_t dest;
  uint16_t source;
  char message[];
} udp_msg;

routing_table* table;

int base_port, own_ID, tcp_socket, inbound_udp;
void send_info(int, char**, int);
int create_tcp_connection(int);
int create_inbound_udp(int, int);
int create_outbound_udp(int, int);
void read_data();
void run_udp_server(int);
uint16_t route(uint16_t);
void cleanup();
