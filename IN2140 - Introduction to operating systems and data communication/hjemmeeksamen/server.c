#include "server.h"

int main(int argc, char** argv) {
	base_port = atoi(argv[1]);
	num_nodes = atoi(argv[2]);

	// Open listening socket
	server_socket = create_server_socket();

	// Create internal client structure
	setup_data();

	// Receive messages from nodes, store in graph
	get_node_info(server_socket);

	// Create routing tables
	create_routing_tables();

	// Send to nodes

	// Close connections

	// Clean up
	close(server_socket);
	cleanup_data();
}

void create_routing_tables() {
	int* pred;

	pred = calloc(num_nodes, sizeof(int));

	// Create adjacency matrix from connection data in nodes

	// dijkstra(adj, num_nodes, pred)

	// Use pred to create routing table for each node

	free(pred);
}

void setup_data() {
	int i;

	clients = malloc(num_nodes * sizeof(client_s*));
	for (i = 0; i < num_nodes; i++) {
		clients[i] = malloc(sizeof(client_s));
		clients[i]->fd = -1;
	}
	nodes = (node_info**)calloc(num_nodes, sizeof(node_info*));
	dijkstra_map = calloc(num_nodes, sizeof(uint16_t));
}

void cleanup_data() {
	int i;

	for (i = 0; i < num_nodes; i++) {
		free(nodes[i]);
		free(clients[i]);
	}
	free(nodes);
	free(clients);
	free(dijkstra_map);
}

uint16_t map_get_pos(uint16_t node_id) {
	int i;
	for (i = 0; i < num_nodes; i++) {
		if (dijkstra_map[i] == node_id) {
			return i;
		}
	}
	return -1;
}

void map_add_node(node_info* msg) {
	int i;

	for (i = 0; i < num_nodes; i++) {
		if (!nodes[i]) {
			printf("Adding node %d to map[%d]\n", msg->id, i);
			nodes[i] = malloc(sizeof(node_info) + msg->num_peers * 2 * sizeof(uint16_t));
			dijkstra_map[i] = msg->id;
			if (msg->id == 1) {
				startnode = i;
			}
			return;
		}
	}
}

void remove_client(int fd) {
	int i;

	for (i = 0; i < num_nodes; i++) {
		if (clients[i]->fd == fd) {
			printf("Disconnection from port %u\n", clients[i]->port);
			clients[i]->fd = -1;
			free(clients[i]->ip),
			remaining_nodes--;
			return;
		}
	}
}

void handle_message(int client_socket, fd_set* fds) {
	char *recv_buf;
	int i;
	uint16_t node_id;
	uint16_t num_peers;
	ssize_t nbytes;
	node_info *msg;

	/* Read metadata */
	recv_buf = malloc(2 * sizeof(uint16_t));
	nbytes = read(client_socket, recv_buf, (2 * sizeof(uint16_t)));
	if (nbytes == 0 || nbytes == -1) {
		free(recv_buf);
		remove_client(client_socket);
		FD_CLR(client_socket, fds);
		return;
	}
	msg = malloc(sizeof(node_info));
	memcpy(msg, recv_buf, 2 * sizeof(uint16_t));
	node_id = ntohs(msg->id);
	num_peers = ntohs(msg->num_peers);

	// Store ID in client list
	for (i = 0; i < num_nodes; i++) {
		if (clients[i]->fd == client_socket) {
			clients[i]->id = node_id;
		}
	}

	/* Read payload */
	recv_buf = realloc(recv_buf, (num_peers * 2 + 2) * sizeof(uint16_t));
	nbytes = read(client_socket, recv_buf, (num_peers * 2 * sizeof(uint16_t)));
	if (nbytes == 0) {
		free(recv_buf);
		free(msg);
		remove_client(client_socket);
		FD_CLR(client_socket, fds);
		return;
	} else if (nbytes == -1) {
		printf("Unknown error in read (%d)", (int)nbytes);
	}
	msg = realloc(msg, (num_peers * 2 + 2) * sizeof(uint16_t));
	msg->id = node_id;
	msg->num_peers = num_peers;
	memcpy(msg->connections, recv_buf, (num_peers * 2 * sizeof(uint16_t)));
	for (i = 0; i < num_peers*2; i++) {
		msg->connections[i] = ntohs(msg->connections[i]);
	}

	map_add_node(msg);

	// Print message
	printf("Received info:\n");
	printf("Node ID: %d\nConnections(%d):\n", msg->id, msg->num_peers);
	for (i = 0; i < num_peers; i++) {
		printf("\tPeer:   %d\n\tWeight: %d\n", msg->connections[i*2], msg->connections[i*2+1]);
	}

	free(msg);
	free(recv_buf);
}

int add_client(int socket, struct sockaddr_in* client_addr, socklen_t addrlen) {
	char* ip_buffer = malloc(16);
	int i;

	if (!inet_ntop(client_addr->sin_family, &(client_addr->sin_addr), ip_buffer, addrlen)) {
		printf("ERROR: Failed to convert IP address to string\n");
		perror("inet_ntop");
		strcpy(ip_buffer, "N/A");
	}

	for (i = 0; i < num_nodes; i++) {
		if (clients[i]->fd == -1) {
			clients[i]->fd = socket;
			clients[i]->ip = ip_buffer;
			clients[i]->port = ntohs(client_addr->sin_port);

			write(socket, RESPONSE_SUCCESS, strlen(RESPONSE_SUCCESS));
			printf("Client connected from port %d, added as fd %d\n", clients[i]->port, socket);
			return 0;
		}
	}

	/* No capacity to handle client */
	printf("ERROR: Server is out of capacity\n");
	write(socket, RESPONSE_FULL, strlen(RESPONSE_FULL));

	free(ip_buffer);
	close(socket);
	return 1;
}

int handle_connection(int server_socket, fd_set* fds) {
	int client_socket;
	struct sockaddr_in client_addr;
	socklen_t addrlen;

	/* Accept new client */
	addrlen = sizeof(struct sockaddr_in);
	client_socket = accept(server_socket, (struct sockaddr*)&client_addr, &addrlen);
	if (client_socket == -1) {
		printf("ERROR: accept() failed\n");
		perror("accept");
		return -1;
	}

	if (add_client(client_socket, &client_addr, addrlen)) {
		return -1;
	}

	FD_SET(client_socket, fds);
	return client_socket;
}

void get_node_info(int listener) {
	fd_set fds, read_fds;
	int i, client_socket, largest_fd = listener;

	FD_ZERO(&fds);
	FD_SET(listener, &fds);

	/* Receive peer information loop */
	remaining_nodes = num_nodes;
	while (remaining_nodes > 0) {
		read_fds = fds;

		printf("Listening...\n");
		if (select(largest_fd+1, &read_fds, NULL, NULL, NULL) == -1) {
			printf("ERROR: select() failed\n");
			perror("select");
			return;
		}

		/* Seek through file descriptors */
		for (i = 0; i <= largest_fd; i++) {
			if (FD_ISSET(i, &read_fds)) {
				if (i == listener) {
					printf("New connection\n");
					client_socket = handle_connection(i, &fds);
					if (client_socket == -1) {
						remaining_nodes--;
						continue;
					} else if (client_socket > largest_fd) {
						largest_fd = client_socket;
					}
				} else {
					printf("Message from %d\n", i);
					handle_message(i, &fds);
				}
			}
		}
	}
}

int create_server_socket() {
	int ret, yes = 1, server_socket = 1;
	struct sockaddr_in server_addr;

	/* Create file descriptor for socket */
	server_socket = socket(AF_INET, SOCK_STREAM, 0);
	if (server_socket == -1) {
		printf("socket() failed\n");
		perror("socket");
		exit(EXIT_FAILURE);
	}

	/* Specify what type of connections to accept and which port to use */
	server_addr.sin_family = AF_INET; // IPv4 address
	server_addr.sin_port = htons(base_port); // Which port to listen on
	server_addr.sin_addr.s_addr = INADDR_ANY; // Accept connections from anyone

	/* Set the port to be reusable immediately */
	setsockopt(server_socket, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int));

	ret = bind(server_socket, (struct sockaddr*)&server_addr, sizeof(server_addr));
	if (ret) {
		printf("bind() failed\n");
		perror("bind");
		exit(EXIT_FAILURE);
	}

	/* Ready to listen for connections */
	ret = listen(server_socket, num_nodes);
	if (ret == -1) {
		printf("listen() failed\n");
		perror("listen");
		exit(EXIT_FAILURE);
	}
	return server_socket;
}
