#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include "util.h"
#include "protocol.h"

#define BUFFER_SIZE strlen(RESPONSE_SUCCESS)
#define SERVER_PORT 21400

int send_file(int fd, char* file_name, int flags) {
    char *send_buffer;
    size_t ret, offset = 0;
    size_t page_size = (size_t) getpagesize();
    int break_time;

    struct message_header *header;

    FILE *file_to_send;

    break_time = 0;

    LOG(LOGGER_DEBUG, "Opening file %s", file_name);
    file_to_send = fopen(file_name, "r");
    if (file_to_send == NULL) {
        LOG(LOGGER_ERROR, "File %s could not be opened for reading", file_name);
        perror("fopen");
        return -1;
    }

    header = set_message_header_info(file_to_send, file_name);
    if (header == NULL) {
        LOG(LOGGER_ERROR, "Error in set_message_header");
        fclose(file_to_send);
        return -1;
    }

    send_buffer = (char*) malloc(page_size);

    construct_message_header(send_buffer, header);

    offset = HEADER_STATIC_SIZE + header->file_name_len;

    while (1) {
        ret = fread(send_buffer+offset, sizeof(char), page_size-offset, file_to_send);
        LOG(LOGGER_DEBUG, "Read %zd bytes", ret);
        if(ret + offset != page_size) {
            if(feof(file_to_send)) {
                LOG(LOGGER_DEBUG, "EOF, whole file read");
                break_time = 1;
            } else {
                LOG(LOGGER_ERROR, "error in fread(), read %zd bytes", ret);
                perror("fread");
                fclose(file_to_send);
                free(send_buffer);
                return -1;
            }
        }
        LOG(LOGGER_DEBUG, "no EOF yet, sending more");

        send_n(fd, send_buffer, ret+offset, flags);

        offset = 0;
        if (break_time) {
            break;
        }
    }

    free(header);
    free(send_buffer);
    fclose(file_to_send);
    LOG(LOGGER_DEBUG, "Whole file sent");
    return 0;
}

int socket_setup(char* address) {
    int ret;
    struct sockaddr_in server_addr;
    int client_socket;

    client_socket = socket(AF_INET, SOCK_STREAM, 0);
    if (client_socket == -1) {
        LOG(LOGGER_ERROR, "socket() failed");
        perror("socket");
        return 0;
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(SERVER_PORT);
    server_addr.sin_addr.s_addr = inet_addr(address);

    LOG(LOGGER_DEBUG, "Connecting to server at %s, port %d", address, SERVER_PORT);

    ret = connect(client_socket, (struct sockaddr *)&server_addr, sizeof(struct sockaddr_in));
    if (ret == -1) {
        LOG(LOGGER_ERROR, "connect() failed");
        perror("connect");
        return 0;
    }

    return client_socket;
}

int main(int argc, char* argv[]) {
    int ret;
    int client_socket;
    char buffer[BUFFER_SIZE];

    if (argc != 2) {
        printf("Usage: %s FILE_NAME\n", argv[0]);
        printf("\tFILE_NAME:\tName of file to send to server\n");
        exit(EXIT_SUCCESS);
    }

    LOG(LOGGER_DEBUG, "Setting up connection socket");
    client_socket = socket_setup("127.0.0.1");
    if (client_socket == 0) {
        exit(EXIT_FAILURE);
    }

    LOG(LOGGER_DEBUG, "Receiving data...");
    ssize_t bytes = recv_n(client_socket, buffer, sizeof(buffer), 0);
    if (bytes > 0) {
        buffer[bytes] = '\0';
        LOG(LOGGER_INFO, "Received %zd bytes from server: %s", bytes, buffer);
    }

    printf("Press enter to continue\n");
    while (getchar() != '\n');

    LOG(LOGGER_DEBUG, "Sending contents of file %s to server", argv[1]);

    ret = send_file(client_socket, argv[1], 0);
    if (ret == -1) {
        LOG(LOGGER_ERROR, "Error in send_file");
        close(client_socket);
        exit(EXIT_FAILURE);
    }

    close(client_socket);
    return EXIT_SUCCESS;
}
