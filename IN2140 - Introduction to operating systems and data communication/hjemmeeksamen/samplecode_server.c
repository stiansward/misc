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

struct client clients[MAX_NUM_CLIENTS] = { 0 };

int add_client(int socket, struct sockaddr_in *client_addr, socklen_t addrlen);
void remove_client(int fd);
void run_server(int listener);
int create_server_socket();

int main() {
    int i;
    int server_socket;

    server_socket = create_server_socket();

    for (i = 0; i < MAX_NUM_CLIENTS; ++i) {
        clients[i].fd = -1;
    }

    LOG(LOGGER_INFO, "Running in multiplexed server mode");
    run_server(server_socket);

    /* Clean up */
    for (i = 0; i < MAX_NUM_CLIENTS; ++i) {
        if (clients[i].fd != -1) {
            close(clients[i].fd);
            free(clients[i].ip);
        }
    }

    close(server_socket);
    return EXIT_SUCCESS;
}

int create_server_socket() {
    int ret;
    int yes = 1;
    int server_socket = 1;

    struct sockaddr_in server_addr;

    LOG(LOGGER_DEBUG, "Creating server socket");

    /* Create a socket. Notice that this is just a file descriptor */
    server_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (server_socket == -1) {
        LOG(LOGGER_ERROR, "socket() failed");
        perror("socket");
        exit(EXIT_FAILURE);
    }

    LOG(LOGGER_DEBUG, "Created server socket");

    /* Specify what type of connections we accept and the port we want to use */
    server_addr.sin_family = AF_INET; /* IPv4 address */
    server_addr.sin_port = htons(SERVER_PORT); /* Listen to port 21400. Encode 21400 using network byte order.*/
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
    LOG(LOGGER_DEBUG, "Trying to listen to socket...");

    /* Ready to listen for connections */
    ret = listen(server_socket, BACKLOG_SIZE);
    if (ret == -1) {
        LOG(LOGGER_ERROR, "listen() failed");
        perror("lisiten");
        exit(EXIT_FAILURE);
    }

    LOG(LOGGER_DEBUG, "Now listening on socket");

    return server_socket;
}

void remove_client(int fd) {
    int i;

    LOG(LOGGER_DEBUG, "Client disconnection at fd: %d", fd);
    for (i = 0; i < MAX_NUM_CLIENTS; ++i) {
        if (clients[i].fd == fd) {
            LOG(LOGGER_INFO, "Disconnection from IP address %s and port %u\n", clients[i].ip, clients[i].port);
            clients[i].fd = -1;
            free(clients[i].ip);
            LOG(LOGGER_DEBUG, "Client found and removed from internal structures");
            return;
        }
    }

    LOG(LOGGER_ERROR, "No client found at fd %d, no client removed", fd);
}

int add_client(int socket, struct sockaddr_in *client_addr, socklen_t addrlen) {
    char *ip_buffer = malloc(16);
    int i;

    LOG(LOGGER_DEBUG, "Attempting to add connecting client to internal client list");

    if (!inet_ntop(client_addr->sin_family, &(client_addr->sin_addr), ip_buffer, addrlen)) {
        LOG(LOGGER_ERROR, "Error converting IP address to string");
        perror("inet_ntop");
        strcpy(ip_buffer, "N/A");
    }

    LOG(LOGGER_INFO, "Connection from IP address %s and port %u\n", ip_buffer, ntohs(client_addr->sin_port));

    for (i = 0; i < MAX_NUM_CLIENTS; ++i) {
        /* We have capacity to handle this client*/
        if (clients[i].fd == -1) {
            LOG(LOGGER_DEBUG, "Room for client, adding and sending acceptance message");
            clients[i].fd = socket;
            clients[i].ip = ip_buffer;
            clients[i].port = ntohs(client_addr->sin_port);

            LOG(LOGGER_DEBUG, "Writing response to client: %s", RESPONSE_SUCCESS);
            write(socket, RESPONSE_SUCCESS, strlen(RESPONSE_SUCCESS));
            return 0;
        }
    }

    /* We don't have capacity to handle this client*/
    LOG(LOGGER_DEBUG, "Max capacity reached, client turned down");
    LOG(LOGGER_DEBUG, "Writing response to client: %s", RESPONSE_FULL);
    write(socket, RESPONSE_FULL, strlen(RESPONSE_FULL));
    free(ip_buffer);
    close(socket);
    return 1;
}

int handle_connection(int server_socket, fd_set *fds) {
    int client_socket;
    struct sockaddr_in client_addr;
    socklen_t addrlen;

    /* Accept new client */
    LOG(LOGGER_DEBUG, "fd %d is the listening socket for new connections!", server_socket);

    addrlen = sizeof(struct sockaddr_in);
    client_socket = accept(server_socket,
            (struct sockaddr*)&client_addr,
            &addrlen);

    if (client_socket == -1) {
        LOG(LOGGER_ERROR, "accept() failed, continuing...");
        perror("accept");
        return -1;
    }

    LOG(LOGGER_DEBUG, "Client accepted, client socket fd: %d", client_socket);
    if (add_client(client_socket, &client_addr, addrlen)) {
        return -1; //?
    }

    FD_SET(client_socket, fds);
    return client_socket;
}

void handle_message(int client_socket, fd_set *fds) {
    int i;
    ssize_t nbytes;
    char* recv_buf;
    //size_t page_size;

    struct message_header header;

    //page_size = (size_t) getpagesize();

    LOG(LOGGER_DEBUG, "fd %d belongs to an already connected client", client_socket);
    LOG(LOGGER_DEBUG, "Receiving data...");

    /* Receive message from client */

    recv_buf = (char*) malloc(HEADER_STATIC_SIZE);
    nbytes = recv_n(client_socket, recv_buf, HEADER_STATIC_SIZE, 0);
    if (nbytes == -1) {
        LOG(LOGGER_ERROR, "Error in recv_n");
        free(recv_buf);
        remove_client(client_socket);
        FD_CLR(client_socket, fds);
        return;
    } else if (nbytes == 0) {
        LOG(LOGGER_DEBUG, "No data in socket, client disconnection");
        free(recv_buf);
        remove_client(client_socket);
        FD_CLR(client_socket, fds);
        return;
    }
    deconstruct_message_header_static(recv_buf, &header);
    LOG(LOGGER_DEBUG, "Received %zd bytes from client", nbytes);
    LOG(LOGGER_DEBUG, "File size: %d", header.file_size);
    LOG(LOGGER_DEBUG, "File name len: %d", header.file_name_len);

    recv_buf = (char*) realloc(recv_buf, header.file_name_len + 1);
    nbytes = recv_n(client_socket, recv_buf, header.file_name_len, 0);
    if (nbytes == -1) {
        LOG(LOGGER_ERROR, "Error in recv_n");
        free(recv_buf);
        remove_client(client_socket);
        FD_CLR(client_socket, fds);
        return;
    } else if (nbytes == 0) {
        LOG(LOGGER_DEBUG, "No data in socket, client disconnection");
        free(recv_buf);
        remove_client(client_socket);
        FD_CLR(client_socket, fds);
        return;
    }
    deconstruct_message_header_file_name(recv_buf, &header);

//    printf("Press enter to continue\n");
//    while (getchar() != '\n');

    recv_buf = realloc(recv_buf, header.file_size);
    nbytes = recv_n(client_socket, recv_buf, header.file_size, 0);
    if (nbytes == -1) {
        LOG(LOGGER_ERROR, "Error in recv_n");
        free(header.file_name);
        free(recv_buf);
        remove_client(client_socket);
        FD_CLR(client_socket, fds);
        return;
    } else if (nbytes == 0) {
        LOG(LOGGER_DEBUG, "No data in socket, client disconnection");
        free(recv_buf);
        free(header.file_name);
        remove_client(client_socket);
        FD_CLR(client_socket, fds);
        return;
    }

    LOG(LOGGER_DEBUG, "Received %zd bytes from client", nbytes);
    LOG(LOGGER_DEBUG, "Data received from client:");
    for (i = 0; i < MAX_NUM_CLIENTS; ++i) {
        if (clients[i].fd == client_socket) {
            LOG(LOGGER_INFO, "%s(%u):", clients[i].ip, clients[i].port);
            LOG(LOGGER_DEBUG, "File name: %s", header.file_name);

            FILE* out_file = fopen(header.file_name, "w+");
            if (out_file == NULL) {
                perror("fopen");
                exit(EXIT_FAILURE);
            }
            fwrite(recv_buf, sizeof(char), (size_t)nbytes, out_file);
            fclose(out_file);
        }
    }

}

/* Accept up to MAX_NUM_CLIENT concurrent connections */
void run_server(int listener) {

    /* A bit more complicated example */
    fd_set fds;
    fd_set read_fds;

    int largest_fd = listener;
    int i;

    FD_ZERO(&fds);
    FD_SET(listener, &fds);

    LOG(LOGGER_DEBUG, "Entering select loop");

    /* As long as no errors */
    while (1) {

        read_fds = fds;

        LOG(LOGGER_DEBUG, "Calling select, blocking");
        if (select(largest_fd+1, &read_fds, NULL, NULL, NULL) == -1) {
            LOG(LOGGER_ERROR, "select() failed");
            perror("select");
            return;
        }

        /* Go through all possible file descriptors */
        LOG(LOGGER_DEBUG, "Looping through FDs up to %d", largest_fd);
        for (i = 0; i <= largest_fd; ++i) {
            if (FD_ISSET(i, &read_fds)) {
                LOG(LOGGER_DEBUG, "Activity at fd %d", i);
                if (i == listener) {
                    int client_socket = handle_connection(i, &fds);
                    if (client_socket == -1) {
                        continue;
                    } else if (client_socket > largest_fd) {
                        LOG(LOGGER_DEBUG, "Updating largest fd...");
                        largest_fd = client_socket;
                    }
                } else {
                    handle_message(i, &fds);
                }
            }
        } // For
    } // While
}
