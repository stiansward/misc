/*
* ########  Mandatory assignment, IN2140  ########
*          Stian Carlsen Swärd (stiancsw)
*/

#include "oblig.h"

int main(int argc, char **argv)
{
  if (argc < 3)
  {
    printf("Not enough arguments given");
    return 1;
  }
  read_routers(argv[1]);
  read_commands(argv[2]);
  write_file(argv[1]);
  cleanup();
  return 0;
}

/**
* Read the router file, create structs for each,
* then add appropriate connections
* Parameters
* filename    name of the file containing the routers' info
*/
void read_routers(char *filename)
{
  FILE *file = fopen(filename, "r");
  unsigned char id, flags, namelength;
  router_s *router;

  printf("-----READING ROUTERS-----\n");

  fread(&num_of_routers, 4, 1, file);
  fgetc(file);    // Skip newline

  int i;
  for (i = 0; i < num_of_routers; i++)
  {
    id = fgetc(file);
    flags = fgetc(file);
    namelength = fgetc(file);

    // Allocates 112 bytes, initialized to 0
    router = calloc(sizeof(router_s), 1);

    router->id = id;
    router->flags = flags;
    router->namelength = namelength;
    // Allocates space for name, including terminating null-byte
    router->name = calloc(namelength * sizeof(char), 1);

    fread(router->name, 1, (namelength - 1), file);
    fgetc(file);

    routers[id] = router;
  }
  printf("Added %d routers to map\n", num_of_routers);

  // Create connections
  unsigned char R1, R2;
  int num_of_connections = 0, input;
  while ((input = fgetc(file)) != EOF)
  {
    R1 = (unsigned char) input;
    R2 = (unsigned char) fgetc(file);
    add_connection(R1, R2);
    num_of_connections++;
    fgetc(file);    // Skip newline
  }
  printf("Created %d connections\n\n\n", num_of_connections);
  fclose(file);
}

/**
* Write the current data structure to file,
* in the same format as read in read_routers
* Parameters
* filename    Name of the file to be written
*/
void write_file(char *filename)
{
  FILE *file = fopen(filename, "w");

  fwrite(&num_of_routers, 4, 1, file);
  fwrite("\n", 1, 1, file);

  int i;
  for (i = 0; i < 256; i++) {
    if (routers[i] != NULL)
    {
      fwrite(&routers[i]->id, sizeof(unsigned char), 1, file);
      fwrite(&routers[i]->flags, sizeof(unsigned char), 1, file);
      fwrite(&routers[i]->namelength, sizeof(unsigned char), 1, file);
      fwrite(routers[i]->name, sizeof(char), (routers[i]->namelength - 1) * sizeof(char), file);
      fwrite("\n", 1, 1, file);
    }
  }

  int j;
  for (i = 0; i < 256; i++)
  {
    if (routers[i] != NULL)
    {
      for (j = 0; j < routers[i]->num_of_connections; j++)
      {
        fwrite(&routers[i]->id, 1, 1, file);
        fwrite(&routers[routers[i]->connections[j]]->id, 1, 1, file);
        fwrite("\n", 1, 1, file);
      }
    }
  }
  fclose(file);
}

/**
* Read commands from file, and call appropriate methods
* Parameters
* filename    Name of the file containing the commands
*/
void read_commands(char *filename)
{
  FILE *file = fopen(filename, "r");
  char *command = malloc(256 * sizeof(char));

  printf("-----READING COMMANDS-----\n");
  int res;
  while ((res = fscanf(file, "%s ", command)) != EOF)
  {
    if (!strcmp(command, "print"))
    {
      int id;
      fscanf(file, "%d\n", &id);
      printf("Printing router %d:\n", id);
      print_info(routers[(unsigned char) id]);
    } else if (!strcmp(command, "sett_flag"))
    {
      int args[3];
      fscanf(file, "%d %d %d\n", &args[0], &args[1], &args[2]);
      set_flag(routers[args[0]], args[1], args[2]);
      printf("Set flag %d in router %d \"%s\" to %d\n",
              args[1], args[0], routers[args[0]]->name, args[2]);
    } else if (!strcmp(command, "sett_modell"))
    {
      int id;
      fscanf(file, "%d %[^\n]%*c", &id, command);
      set_model(routers[id], command);
      printf("Renamed router %d to %s\n", id, command);
    } else if (!strcmp(command, "legg_til_kobling"))
    {
      int ids[2];
      fscanf(file, "%d %d\n", &ids[0], &ids[1]);
      if (add_connection(ids[0], ids[1]))
      {
        printf("Added connection from %d to %d\n", ids[0], ids[1]);
      }
    } else if (!strcmp(command, "slett_router"))
    {
      int id;
      fscanf(file, "%d\n", &id);
      delete_router(id);
    } else if (!strcmp(command, "finnes_rute"))
    {
      int ids[2];
      fscanf(file, "%d %d\n", &ids[0], &ids[1]);
      printf("Does a route exist between routers %d and %d?\n", ids[0], ids[1]);
      int path[num_of_routers], visited[num_of_routers];
      memset(path, -1, sizeof(path));
      memset(visited, 0, sizeof(visited));
      int length_of_path = 0;
      find_path(routers[ids[0]], ids[1], path, &length_of_path, visited);
      if (length_of_path > 1)
      {
        printf("Yes: ");
        int i;
        for (i = 0; i <= length_of_path; i++)
        {
          if (i > 0) printf(" --> ");
          printf("%d", path[i]);
        }
        printf("\n");
      } else
      {
        printf("No.\n");
      }
    } else
    {
      err("Command not recognized:");
      printf("%s\n", command);
      fscanf(file, "%*[^\n]\n");
    }
    printf("-------------------\n");
  }
  free(command);
  fclose(file);
}

/**
* Print the information stored about a router
* Parameters
* router    Pointer to the router
*/
void print_info(router_s *router)
{
  printf("%s\n", router->name);
  printf("ID: %12d\n", router->id);
  printf("Active?%9s\n", (router->flags & 1) ? "Yes" : "No");
  printf("WiFi?%11s\n", (router->flags & (1 << 1)) ? "Yes" : "No");
  printf("5GHz?%11s\n", (router->flags & (1 << 2)) ? "Yes" : "No");
  printf("Connections: %3d\n", router->num_of_connections);
  /*
  printf("%s\n"
          "ID: %12d\n"
          "Active?: %7s\n"
          "WiFi?: %9s\n"
          "5GHz?: %9s\n"
          "Connections: %3d\n",
          router->name,
          router->id,
          ((router->flags & 1) ? "Yes" : "No"),
          ((router->flags & (1 << 1)) ? "Yes" : "No"),
          ((router->flags & (1 << 2)) ? "Yes" : "No"),
          router->num_of_connections);
          */

  int i;
  for (i = 0; i < router->num_of_connections; i++)
  {
    if (i == 0) printf("Connected IDs:\n");
    printf("%16d\n", routers[router->connections[i]]->id);
  }
}

/**
* Set one of the flags in a router
* Parameters
* router    Pointer to the router
* pos       Position of the bit(s) ot be set
* value     Value of the bit(s) to set
*/
void set_flag(router_s *router, int pos, int value)
{
  switch (pos)
  {
    case 0 :
    case 1 :
    case 2 :
      if (value != 0 && value != 1)
      {
        err("Invalid flag value");
        return;
      }
      // Clear appropriate bit
      router->flags = (router->flags & (~(1 << pos)));
      break;
    case 4 :
      if (value < 0 || value > 15)
      {
        err("Invalid flag value");
        return;
      }
      // Clear 4 most significant bits
      router->flags = (router->flags & 15);
      break;
    default :
      err("Invalid flag position");
      return;
  }
  // Set appropriate bit(s)
  router->flags = (router->flags | (value << pos));
}

/**
* Rename a router
* Parameters
* router    Pointer to the router to rename
* newname   The new name to set
*/
void set_model(router_s *router, char *newname)
{
  int namelength = strlen(newname) + 1;
  if (namelength > 253)
  {
    err("Name is too large");
    return;
  }
  router->namelength = (unsigned char) namelength;
  // Reallocates space for new name, including terminating null-byte
  router->name = realloc(router->name, namelength * sizeof(char));
  strcpy(router->name, newname);
}

/**
* Delete a router from the data structure,
* after deleting every connection to said router
* Parameters
* id        ID number of the router to be deleted
*/
void delete_router(int id)
{
  if (routers[id] == NULL)
  {
    err("Router does not exist!");
    return;
  }
  int i;
  for (i = 0; i < num_of_routers; i++)
  {
    delete_connection(routers[i], id);
  }
  // Frees the space allocated to the router's name in read_routers()
  free(routers[id]->name);
  // Frees the router itself allocated in read_routers()
  free(routers[id]);
  routers[id] = NULL;
  num_of_routers--;
  printf("Deleted router %d\n", id);
}

/**
* Find a path between two routers, storing it as a list of IDs
* Parameters
* R1                Pointer to the router to search from
* R2                ID of the router to find a path to
* path              Array of the current path.
*                   If a path is found, the value of length_of_path
*                   will be the index of the target router
* length_of_path    Pointer to the current length of the path
*                   If no path is found, the value is zero
* visited           Map of which routers have already been checked
*/
void find_path(router_s * R1, int R2, int path[], int *length_of_path, int visited[])
{
  int i;
  // Check if R1 is already a part of the path, or has been visited before
  for (i = 0; i <= *length_of_path; i++)
  {
    if (visited[R1->id] || path[i] == R1->id) return;
  }
  path[(*length_of_path)++] = R1->id;
  visited[R1->id] = 1;
  for (i = 0; i < R1->num_of_connections; i++)
  {
    find_path(routers[R1->connections[i]], R2, path, length_of_path, visited);
    if (path[*length_of_path] == R2) return;
  }
  (*length_of_path)--;
}

/**
* Add a connection from one router to another
* The connection is from A to B, and is stored in A's list of connections
* Parameters
* from        ID of router A
* to          ID of router B
*/
int add_connection(unsigned char from, unsigned char to)
{
  if (routers[from]->num_of_connections <= 9)
  {
    int i;
    for (i = 0; i < routers[from]->num_of_connections; i++)
    {
      if (routers[from]->connections[i] == to)
      {
        printf("There is already a connection from %d to %d\n", from, to);
        return 0;
      }
    }
    routers[from]->connections[routers[from]->num_of_connections++] = to;
  } else
  {
    printf("ERROR: No free connections on router %d", routers[from]->id);
    return 0;
  }
  return 1;
}

void delete_connection(router_s *router, int id)
{
  int i, j;
  for (i = 0; i < router->num_of_connections; i++)
  {
    if (router->connections[i] == id)
    {
      for (j = i; j < (router->num_of_connections - 1); j++)
      {
        router->connections[i] = router->connections[i+1];
      }
      (router->num_of_connections)--;
    }
  }
}

/**
* Traverse the data structure, free'ing up all space allocated to the routers
*/
void cleanup()
{
  int i;
  for (i = 0; i < 256; i++)
  {
    if (routers[i] != NULL)
    {
      // Free the routers in the same way as done in delete_router()
      free(routers[i]->name);
      free(routers[i]);
    }
  }
}

/*
* Print a simple error message to the console
*/
void err(char *message)
{
  printf("ERROR: %s\n", message);
}
