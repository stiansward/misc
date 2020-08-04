#include "protocol.h"

ssize_t send_n(int fd, void *buf, size_t n, int flags) {
    char *char_buf;
    ssize_t total_bytes;
    ssize_t send_bytes;

    char_buf = buf;
    total_bytes = 0;

    while (total_bytes < n) {
        send_bytes = send(fd, char_buf+total_bytes, n-total_bytes, flags);

        if (send_bytes < 0) {
            perror("recv()");
            return -1;
        }

        if (send_bytes == 0) {
            break;
        }

        total_bytes += send_bytes;
        printf("Sent %zd of %zd bytes\n", total_bytes, n);
    }

    return total_bytes;
}

ssize_t recv_n(int fd, void *buf, size_t n, int flags) {
    char *char_buf;
    ssize_t total_bytes;
    ssize_t recv_bytes;

    char_buf = buf;
    total_bytes = 0;

    while (total_bytes < n) {
        recv_bytes = recv(fd, char_buf+total_bytes, n-total_bytes, flags);

        if (recv_bytes < 0) {
            perror("recv()");
            return -1;
        }

        if (recv_bytes == 0) {
            break;
        }

        total_bytes += recv_bytes;
        printf("Received %zd of %zd bytes\n", total_bytes, n);
    }
    return total_bytes;
}

char* construct_node_info(node_info* msg) {
  return NULL;
}

void deconstruct_node_info_static(char* buffer, node_info* msg) {

}
