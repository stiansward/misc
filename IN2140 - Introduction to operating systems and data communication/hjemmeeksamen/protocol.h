/**
 * This header file is based on the header file from IN2140 Plenary Lectures
 * Source: https://github.uio.no/IN2140/PlenarySessions/blob/master/week12/protocol.h
 */

#ifndef __PROTOCOL_H
#define __PROTOCOL_H

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <unistd.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define RESPONSE_SUCCESS "Thank you for connecting!"
#define RESPONSE_FULL "Sorry, no more capacity!"
#define RESPONSE_SUCCESS_LEN strlen(RESPONSE_SUCCESS)

typedef struct node_info {
    uint16_t id;
    uint16_t num_peers;
    uint16_t connections[];
} node_info;

struct dest_peer_pair {
  uint16_t dest;
  uint16_t peer;
};

typedef struct routing_table {
  uint16_t size;
  struct dest_peer_pair pairs[];
} routing_table;

ssize_t send_n(int, void*, size_t, int);
ssize_t recv_n(int, void*, size_t, int);
struct message_header* set_message_header_info(FILE*, char*);
char* construct_node_info(node_info*);
void deconstruct_node_info_static(char*, node_info*);
void deconstruct_node_info_peers(char*, node_info*);

#endif
