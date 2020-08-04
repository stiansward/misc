#include "node.h"

int main(int argc, char** argv) {
	// Read basic arguments
	base_port = atoi(argv[1]);
	own_ID = atoi(argv[2]);
	if (base_port + own_ID > 65535) {
		printf("ERROR: Port number must be < %d when ID is %d", (65535 - own_ID), own_ID);
		perror("main");
		exit(EXIT_FAILURE);
	}

	/* Uncomment this when UDP comm is implemented
	if (own_ID != 1) {
		int inbound_udp = create_inbound_udp(base_port, own_ID);
	} */

	// Create connection to server
	tcp_socket = create_tcp_connection(base_port);

	// Send peer info
	send_info(argc, argv, tcp_socket);

	// Receive routing table

	/* Uncomment this when UDP comm is implemented
	if (own_ID == 1) {
		read_data();
	} else {
		run_udp_server(inbound_udp);
	}*/

	cleanup();
	if (own_ID != 1) {
		close(inbound_udp);
	}
	return EXIT_SUCCESS;
}

uint16_t route(uint16_t dest) {
	/* Uncomment this when routing tables are implemented
	int i;

	for (i = 0; i < table->size; i++) {
		if (table->pairs[i].dest == dest) {
			return table->pairs[i].peer;
		}
	}
	return -1; */

	return dest; // Remove when routing tables are implemented
}

void read_data() {
	FILE* file;
	char line[1000];
	int dest_len, msg_len, packet_len, fd, dest_port;
	udp_msg* packet;
	char* msg;
	struct sockaddr_in sockaddr;

	sleep(1);

	file = fopen("data.txt", "r");
	if (file == NULL) {
		printf("ERROR: Could not open file \"data.txt\"");
		return;
	}
	if ((fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		printf("ERROR: Failed to create socket");
		perror("socket");
		return;
	}
	memset(&sockaddr, 0, sizeof(sockaddr));
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_addr.s_addr = INADDR_ANY;

	while (fgets(line, 1000, file) != NULL) {
		// Create UDP packet and convert to byte-stream
		dest_len = 0;
		while (line[dest_len++] != ' ');
		printf("dest_len = %d", dest_len);
		msg_len = dest_len;
		while (line[++msg_len] != '\n' || line[msg_len] != '\0');
		packet_len = sizeof(udp_msg) + (msg_len-dest_len) * sizeof(char);
		packet = malloc(packet_len);
		packet->packet_len = htons(packet_len);
		packet->dest = htons((uint16_t)atoi(strtok(line, " ")));
		packet->source = htons(1);
		memcpy(packet->message, &(line[dest_len]), (msg_len-dest_len));
		packet->message[msg_len-dest_len] = '\0';
		if (ntohs(packet->dest) == 1 && !strcmp(packet->message, "QUIT")) {
			free(packet);
			break;
		}
		msg = malloc(packet_len * sizeof(char));
		memcpy(msg, packet, packet_len*sizeof(char));

		dest_port = route(ntohs(packet->dest));
		sockaddr.sin_port = htons(base_port + dest_port);
		sendto(fd, msg, packet_len, MSG_CONFIRM,
			(const struct sockaddr*)&sockaddr, sizeof(sockaddr));

		free(packet);
		free(msg);
	}

	fclose(file);
}

int print_message(char* buf) {
	print_received_pkt(own_ID, (unsigned char*)buf);
	if(!strcmp(&(buf[6]), "QUIT")) {
		return 1;
	}
	return 0;
}

void forward_message(char* buf) {
	int fd;
	struct sockaddr_in sockaddr;

	print_forwarded_pkt(own_ID, (unsigned char*)buf);

	if ((fd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		printf("ERROR: Failed to create socket");
		perror("socket");
		return;
	}
	memset(&sockaddr, 0, sizeof(sockaddr));
	sockaddr.sin_family = AF_INET;
	sockaddr.sin_addr.s_addr = INADDR_ANY;
	sockaddr.sin_port = htons(base_port + route(ntohs(buf[2])));

	sendto(fd, buf, ntohs(buf[0]), MSG_CONFIRM,
		(const struct sockaddr*)&sockaddr, sizeof(sockaddr));

	close(fd);
}

void run_udp_server(int socket) {
	int ret;
	ssize_t nbytes;
	char buf[1000];

	while (1) {
		nbytes = recvfrom(socket, buf, 1000, 0, NULL, NULL);
		if (nbytes == -1) {
			printf("ERROR: recvfrom() failed\n");
			perror("recvfrom");
			return;
		}
		if (ntohs((uint16_t)buf[2]) == own_ID) {
			ret = print_message(buf);
			if (ret == 1) {
				break;
			}
		} else {
			forward_message(buf);
		}
	}
}

int create_inbound_udp(int base_port, int ownID) {
	int sockfd;
	struct sockaddr_in server_addr, client_addr;

	if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
		printf("ERROR: Failed to create UDP socket");
		perror("socket");
		return -1;
	}

	memset(&server_addr, 0, sizeof(server_addr));
	memset(&client_addr, 0, sizeof(client_addr));

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(base_port + own_ID);
	server_addr.sin_addr.s_addr = INADDR_ANY;

	if (bind(sockfd, (const struct sockaddr *)&server_addr,
					sizeof(server_addr)) < 0) {
		printf("ERROR: Failed to bind socket to port %d", ntohs(server_addr.sin_port));
		perror("bind");
		return -1;
	}
	return sockfd;
}

void send_info(int argc, char** argv, int server_socket) {
	int i, message_size, conn_index = 0;
	uint16_t num_peers;
	node_info* msg;
	char* buffer;
	ssize_t nbytes;

	num_peers = (uint16_t)(argc - 3);

	message_size = (num_peers * 2 + 2) * sizeof(uint16_t);

	msg = calloc(message_size, sizeof(char));
	msg->id = htons(atoi(argv[2]));
	msg->num_peers = htons(num_peers);
	for (i = 3; i < argc; i++) {
		msg->connections[conn_index++] = htons(atoi(strtok(argv[i], ":")));
		msg->connections[conn_index++] = htons(atoi(strtok(NULL, "")));
	}

	buffer = malloc(message_size);
	memcpy(buffer, msg, message_size);

	nbytes = write(server_socket, buffer, message_size);
	if (nbytes == -1) {
		printf("ERROR: Unknown error while sending to server");
		perror("write");
		free(buffer);
		free(msg);
		return;
	}

	printf("Message sent:\nNode ID: %d\nConnections(%d):\n", ntohs(msg->id), ntohs(msg->num_peers));
	for (i = 0; i < ntohs(msg->num_peers); i++) {
		printf("\tPeer:   %d\n\tWeight: %d\n", ntohs(msg->connections[i*2]),
																					ntohs(msg->connections[i*2+1]));
	}

	free(buffer);
	free(msg);
}

int create_tcp_connection(int base_port) {
	int ret;
	struct sockaddr_in server_addr;
	int client_socket;

	client_socket = socket(AF_INET, SOCK_STREAM, 0);
	if (client_socket == -1) {
			printf("ERROR: socket() failed");
			perror("socket");
			return 0;
	}

	server_addr.sin_family = AF_INET;
	server_addr.sin_port = htons(base_port);
	server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");

	ret = connect(client_socket, (struct sockaddr *)&server_addr, sizeof(struct sockaddr_in));
	if (ret == -1) {
			printf("ERROR: connect() failed");
			perror("connect");
			exit(-2);
	}

	return client_socket;
}

void cleanup() {
	close(tcp_socket);
}
