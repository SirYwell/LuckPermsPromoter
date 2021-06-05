package de.sirywell.luckpermspromoter;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class LuckPermsPromoterPlugin extends Plugin implements Listener {
    private final Map<String, String> playerRoles = new HashMap<>();
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        this.luckPerms = LuckPermsProvider.get();
        loadFromFile();
        getProxy().getPluginManager().registerListener(this, this);
    }

    private void loadFromFile() {
        try {
            Files.createDirectories(getDataFolder().toPath());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to create data folder", e);
        }
        var path = Path.of(getDataFolder().getAbsolutePath(), "players.csv");
        if (!Files.isRegularFile(path)) {
            getLogger().warning("No players.csv found");
            return;
        }
        try {
            var lines = Files.readAllLines(path);
            for (String line : lines) {
                var split = line.split("[,;]");
                this.playerRoles.put(split[0].toLowerCase(Locale.ROOT), split[1]);
            }
            getLogger().info("Loaded roles for " + playerRoles.size() + " players");
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to read from players.csv", e);
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        var name = event.getPlayer().getName().toLowerCase(Locale.ROOT);
        var role = this.playerRoles.get(name);
        if (role == null) return;
        var user = this.luckPerms.getPlayerAdapter(ProxiedPlayer.class).getUser(event.getPlayer());
        var result = user.data().add(
                Node.builder("group." + role)
                        .expiry(Duration.ofDays(2L))
                        .build()
        );
        if (result.wasSuccessful()) {
            this.luckPerms.getUserManager().saveUser(user);
            this.playerRoles.remove(name);
        }
    }
}
