#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <arpa/inet.h>
#include <sys/select.h>
#include <sys/time.h>
#include <stdlib.h>

#include "util.h"
#include "protocol.h"

#define MAX_NUM_CLIENTS 2
#define BACKLOG_SIZE 10
#define SERVER_PORT 21400


struct client {
	int fd;
	char *ip;
	unsigned short port;
};

struct client clients[MAX_NUM_CLIENTS];

void run_single_server(int sock);
int create_server_socket();

int main()
{
	int server_socket;

    server_socket = create_server_socket();

    run_single_server(server_socket);

	close(server_socket);
}

int create_server_socket() {
	int ret, yes, server_socket = 1;
	struct sockaddr_in server_addr;

    LOG(LOGGER_DEBUG, "Creating server socket");

	/* Create a socket. Notice that this is just a file descriptor */
	server_socket = socket(AF_INET, SOCK_DGRAM, 0);
	if (server_socket == -1) {
        LOG(LOGGER_ERROR, "socket() failed");
		perror("socket");
        exit(EXIT_FAILURE);
	}

    LOG(LOGGER_DEBUG, "Created server socket");

	/* Specify what type of connections we accept and the port we want to use */
	server_addr.sin_family = AF_INET; /* IPv4 address */
	server_addr.sin_port = htons(SERVER_PORT); /* Listen to port 6000. Encode 6000 using network byte order.*/
	server_addr.sin_addr.s_addr = INADDR_ANY; /* Accept connections from anyone. */

	/* Makes it so that you can reuse port immediately after previous user */
	setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));

    LOG(LOGGER_DEBUG, "Binding server socket to port %d", SERVER_PORT);

	/* Bind to this address */
	ret = bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr));
	if (ret) {
        LOG(LOGGER_ERROR, "bind() failed");
		perror("bind");
        exit(EXIT_FAILURE);
	}

    LOG(LOGGER_DEBUG, "Server socket succesfully bound");
    return server_socket;
}

/* Accept only one connection at the time */
void run_single_server(int sock)
{
    struct sockaddr_in src;
    socklen_t src_len = sizeof(src);
    char buf[RESPONSE_SUCCESS_LEN];

	while (1) {
        /* TODO */
        recvfrom(sock, buf, RESPONSE_SUCCESS_LEN, NULL, &src, src_len);
        printf("%s\n", buf);
	}
}
