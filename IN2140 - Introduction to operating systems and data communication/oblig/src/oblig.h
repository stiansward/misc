#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef struct router_s router_s;

struct router_s
{
  unsigned char id;
  unsigned char flags;
  int num_of_connections;
  unsigned char connections[10];
  unsigned char namelength;
  char *name;
};

int num_of_routers;
router_s *routers[256];


void read_routers(char *);
void read_commands(char *);
void write_file(char *);
void print_info(router_s *);
int add_connection(unsigned char, unsigned char);
void delete_connection(router_s *, int);
void set_flag(router_s *, int, int);
void set_model(router_s *, char *);
void delete_router(int);
void find_path(router_s *, int, int[], int *, int[]);

void cleanup();
void test(char *);
void err(char *);


// Utilities
void bintostr(unsigned char);
void debug(char *);
int in_array(int[], int, int);
