This is a simple BungeeCord plugin to give players a specific parent group using LuckPerms API.

### Setup

This plugin requires LuckPerms 5 to be installed.
It will read from a file called `players.csv` located in the data folder in the plugin.
If no such file is present, the plugin won't do anything, but a warning is printed to console.

The file should look like:
```csv
Player1,Role1
Player2,Role1
Player3,Role2
...
```
`;` is allowed as delimiter too.

The plugin will assign the specified roles to the players once they join. The role will expire 2 days later.