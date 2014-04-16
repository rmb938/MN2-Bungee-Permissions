package com.rmb938.bungee.permissions.database;

import com.mongodb.*;
import com.rmb938.bungee.base.MN2BungeeBase;
import com.rmb938.bungee.permissions.MN2BungeePermissions;
import com.rmb938.bungee.permissions.entity.Group;
import com.rmb938.bungee.permissions.entity.Permission;
import com.rmb938.database.DatabaseAPI;
import com.rmb938.jedis.net.command.bungee.NetCommandBTB;
import com.rmb938.jedis.net.command.bungee.NetCommandBTS;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.util.CaseInsensitiveSet;

import java.util.*;

public class PermissionsLoader {

    private final MN2BungeePermissions plugin;
    private final MN2BungeeBase basePlugin;

    public PermissionsLoader(MN2BungeePermissions plugin) {
        this.plugin = plugin;
        this.basePlugin = (MN2BungeeBase) plugin.getProxy().getPluginManager().getPlugin("MN2BungeeBase");
        createTable();
        loadGroups();
    }

    public void createTable() {
        if (DatabaseAPI.getMongoDatabase().collectionExists("mn2_permissions_groups") == false) {
            DatabaseAPI.getMongoDatabase().createCollection("mn2_permissions_groups");
        }
    }

    public void loadGroups() {
        Group.getGroups().clear();
        Map.Entry<DBCursor, MongoClient> dbCursorMongoClientEntry = DatabaseAPI.getMongoDatabase().findMany("mn2_permissions_groups");
        MongoClient mongoClient = dbCursorMongoClientEntry.getValue();
        DBCursor dbCursor = dbCursorMongoClientEntry.getKey();

        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            String groupName = (String) dbObject.get("groupName");
            loadGroup(groupName);
        }
        dbCursor.close();

        DatabaseAPI.getMongoDatabase().returnClient(mongoClient);

        if (Group.getGroups().containsKey(plugin.getMainConfig().defaultGroup) == false) {
            createGroup(plugin.getMainConfig().defaultGroup, 0);
        }
    }

    public void loadGroup(String groupName) {
        DBObject dbObject = DatabaseAPI.getMongoDatabase().findOne("mn2_permissions_groups", new BasicDBObject("groupName", groupName));
        if (dbObject == null) {
            plugin.getLogger().warning("Unknown Group " + groupName);
            return;
        }
        int weight = (Integer) dbObject.get("weight");
        Group group = new Group();
        group.setGroupName(groupName);
        group.setWeight(weight);

        BasicDBList inheritance = (BasicDBList) dbObject.get("inheritance");
        for (Object anInheritance : inheritance) {
            String groupName1 = (String) anInheritance;
            if (Group.getGroups().containsKey(groupName1)) {
                group.getInheritance().add(Group.getGroups().get(groupName1));
            } else {
                loadGroup(groupName1);
                if (Group.getGroups().containsKey(groupName1)) {
                    group.getInheritance().add(Group.getGroups().get(groupName1));
                } else {
                    plugin.getLogger().warning("Unknown group adding to group " + groupName + " removing");
                    DatabaseAPI.getMongoDatabase().updateDocument("mn2_permission_groups", new BasicDBObject("groupName", group.getGroupName()),
                            new BasicDBObject("$pull", new BasicDBObject("inheritance", groupName)));
                }
            }
        }

        if (group.getInheritance().isEmpty() == false) {
            Collections.sort(group.getInheritance(), new Comparator<Group>() {
                @Override
                public int compare(Group o1, Group o2) {
                    if (o1.getWeight() > o2.getWeight()) {
                        return -1;
                    }
                    if (o1.getWeight() < o2.getWeight()) {
                        return 1;
                    }
                    return 0;
                }
            });
        }

        BasicDBList permissions = (BasicDBList) dbObject.get("permissions");
        for (Object permission1 : permissions) {
            BasicDBObject dbObject1 = (BasicDBObject) permission1;
            String permissionString = (String) dbObject1.get("permission");
            String serverType = (String) dbObject1.get("serverType");
            Permission permission = new Permission();
            permission.setPermission(permissionString);
            permission.setServerType(serverType);
            group.getPermissions().add(permission);
        }
        Group.getGroups().put(groupName, group);
    }

    public void createGroup(String groupName, int weight) {
        BasicDBObject groupObject = new BasicDBObject("groupName", groupName);
        groupObject.append("weight", weight);
        groupObject.append("inheritance", new BasicDBList());
        groupObject.append("permissions", new BasicDBList());

        DatabaseAPI.getMongoDatabase().insert("mn2_permissions_groups", groupObject);
        loadGroup(groupName);
    }

    public void modifyGroupWeight(Group group, int weight) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_permissions_groups", new BasicDBObject("groupName", group.getGroupName()),
                new BasicDBObject("$set", new BasicDBObject("weight", weight)));
        loadGroups();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            loadUserInfo(player);
        }
        NetCommandBTS netCommandBTS = new NetCommandBTS("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTS.flush();
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTB.flush();
    }

    public void addGroupPermission(Group group, Permission permission) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_permissions_groups", new BasicDBObject("groupName", group.getGroupName()),
                new BasicDBObject("$push", new BasicDBObject("permissions", new BasicDBObject("permission", permission.getPermission()).append("serverType", permission.getServerType()))));
        loadGroups();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            loadUserInfo(player);
        }
        NetCommandBTS netCommandBTS = new NetCommandBTS("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTS.flush();
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTB.flush();
    }

    public void removeGroupPermission(Group group, Permission permission) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_permissions_groups", new BasicDBObject("groupName", group.getGroupName()),
                new BasicDBObject("$pull", new BasicDBObject("permissions", new BasicDBObject("permission", permission.getPermission()).append("serverType", permission.getServerType()))));
        loadGroups();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            loadUserInfo(player);
        }
        NetCommandBTS netCommandBTS = new NetCommandBTS("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTS.flush();
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTB.flush();
    }

    public void removeGroup(Group group) {
        DatabaseAPI.getMongoDatabase().remove("mn2_permissions_groups", new BasicDBObject("groupName", group.getGroupName()));
        loadGroups();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            loadUserInfo(player);
        }
        NetCommandBTS netCommandBTS = new NetCommandBTS("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTS.flush();
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTB.flush();
    }

    public void addGroupParent(Group group, Group parent) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_permissions_groups", new BasicDBObject("groupName", group.getGroupName()),
                new BasicDBObject("$push", new BasicDBObject("inheritance", parent.getGroupName())));
        loadGroups();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            loadUserInfo(player);
        }
        NetCommandBTS netCommandBTS = new NetCommandBTS("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTS.flush();
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTB.flush();
    }

    public void removeGroupParent(Group group, Group parent) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_permissions_groups", new BasicDBObject("groupName", group.getGroupName()),
                new BasicDBObject("$pull", new BasicDBObject("inheritance", parent.getGroupName())));
        loadGroups();
        for (ProxiedPlayer player : plugin.getProxy().getPlayers()) {
            loadUserInfo(player);
        }
        NetCommandBTS netCommandBTS = new NetCommandBTS("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTS.flush();
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadGroups", basePlugin.getIP(), "*");
        netCommandBTB.flush();
    }

    public void userAddGroup(String uuid, Group group) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", uuid),
                new BasicDBObject("$push", new BasicDBObject("groups", group.getGroupName())));
        ProxiedPlayer player = null;
        for (ProxiedPlayer player1 : plugin.getProxy().getPlayers()) {
            if (player1.getUniqueId().toString().equals(uuid)) {
                player = player1;
                break;
            }
        }
        if (player != null) {
            loadUserInfo(player);
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), player.getServer().getInfo().getName());
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        } else {
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), "*");
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        }
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadUser", basePlugin.getIP(), "*");
        netCommandBTB.addArg("playerUUID", uuid);
        netCommandBTB.flush();
    }

    public void userRemoveGroup(String uuid, Group group) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", uuid),
                new BasicDBObject("$pull", new BasicDBObject("groups", group.getGroupName())));
        ProxiedPlayer player = null;
        for (ProxiedPlayer player1 : plugin.getProxy().getPlayers()) {
            if (player1.getUniqueId().toString().equals(uuid)) {
                player = player1;
                break;
            }
        }
        if (player != null) {
            loadUserInfo(player);
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), player.getServer().getInfo().getName());
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        } else {
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), "*");
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        }
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadUser", basePlugin.getIP(), "*");
        netCommandBTB.addArg("playerUUID", uuid);
        netCommandBTB.flush();
    }

    public void userAddPermission(String uuid, Permission permission) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", uuid),
                new BasicDBObject("$push", new BasicDBObject("permissions", new BasicDBObject("permission", permission.getPermission()).append("serverType", permission.getServerType()))));
        ProxiedPlayer player = null;
        for (ProxiedPlayer player1 : plugin.getProxy().getPlayers()) {
            if (player1.getUniqueId().toString().equals(uuid)) {
                player = player1;
                break;
            }
        }
        if (player != null) {
            loadUserInfo(player);
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), player.getServer().getInfo().getName());
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        } else {
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), "*");
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        }
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadUser", basePlugin.getIP(), "*");
        netCommandBTB.addArg("playerUUID", uuid);
        netCommandBTB.flush();
    }

    public void userRemovePermission(String uuid, Permission permission) {
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", uuid),
                new BasicDBObject("$pull", new BasicDBObject("permissions", new BasicDBObject("permission", permission.getPermission()).append("serverType", permission.getServerType()))));
        ProxiedPlayer player = null;
        for (ProxiedPlayer player1 : plugin.getProxy().getPlayers()) {
            if (player1.getUniqueId().toString().equals(uuid)) {
                player = player1;
                break;
            }
        }
        if (player != null) {
            loadUserInfo(player);
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), player.getServer().getInfo().getName());
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        } else {
            NetCommandBTS netCommandBTS = new NetCommandBTS("reloadUser", basePlugin.getIP(), "*");
            netCommandBTS.addArg("playerUUID", uuid);
            netCommandBTS.flush();
        }
        NetCommandBTB netCommandBTB = new NetCommandBTB("reloadUser", basePlugin.getIP(), "*");
        netCommandBTB.addArg("playerUUID", uuid);
        netCommandBTB.flush();
    }

    public Map.Entry<String, ArrayList<Group>> userGetGroups(String uuid) {
        ArrayList<Group> groups = new ArrayList<>();
        DBObject userObject = DatabaseAPI.getMongoDatabase().findOne("mn2_users", new BasicDBObject("userUUID", uuid));
        if (userObject == null) {
            return null;
        }
        String userName = (String) userObject.get("lastUserName");
        BasicDBList groupsList = (BasicDBList) userObject.get("groups");
        for (Object aGroupsList : groupsList) {
            String groupName = (String) aGroupsList;
            if (Group.getGroups().containsKey(groupName)) {
                groups.add(Group.getGroups().get(groupName));
            }
        }

        return new AbstractMap.SimpleEntry<>(userName, groups);
    }

    public Map.Entry<String, ArrayList<Permission>> userGetPermissions(String uuid) {
        ArrayList<Permission> permissions = new ArrayList<>();
        DBObject userObject = DatabaseAPI.getMongoDatabase().findOne("mn2_users", new BasicDBObject("userUUID", uuid));
        if (userObject == null) {
            return null;
        }
        String userName = (String) userObject.get("lastUserName");
        BasicDBList permissionsList = (BasicDBList) userObject.get("permissions");
        for (Object aPermissionsList : permissionsList) {
            DBObject permissionObject = (DBObject) aPermissionsList;
            String permissionString = (String) permissionObject.get("permission");
            String serverType = (String) permissionObject.get("serverType");
            Permission permission = new Permission();
            permission.setPermission(permissionString);
            permission.setServerType(serverType);
            permissions.add(permission);
        }

        return new AbstractMap.SimpleEntry<>(userName, permissions);
    }

    private void addInheritance(Group group, ProxiedPlayer player) {
        for (Group group1 : group.getInheritance()) {
            addInheritance(group1, player);
        }
        for (Permission permission : group.getPermissions()) {
            if (permission.getServerType().equals("bungee") == false) {
                continue;
            }
            if (permission.getPermission().startsWith("-")) {
                player.setPermission(permission.getPermission().substring(1, permission.getPermission().length()), false);
            } else {
                player.setPermission(permission.getPermission(), true);
            }
        }
    }

    public void loadUserInfo(ProxiedPlayer player) {
        if (player == null) {
            return;
        }
        DBObject userObject = DatabaseAPI.getMongoDatabase().findOne("mn2_users", new BasicDBObject("userUUID", player.getUniqueId().toString()));
        if (userObject == null) {
            plugin.getLogger().warning("Unknown user permission info " + player.getName());
            return;
        }

        Collection<String> oldGroups = new CaseInsensitiveSet(player.getGroups());
        Collection<String> oldPermissions = new CaseInsensitiveSet(player.getPermissions());

        for (String groupName : oldGroups) {
            player.removeGroups(groupName);
        }

        for (String permission : oldPermissions) {
            player.setPermission(permission, false);
        }

        if (userObject.containsField("groups") == false) {
            createUserInfo(player);
            return;
        }

        BasicDBList groupsList = (BasicDBList) userObject.get("groups");
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<Permission> permissions = new ArrayList<>();
        for (Object aGroupsList : groupsList) {
            String groupName = (String) aGroupsList;
            if (Group.getGroups().containsKey(groupName)) {
                groups.add(Group.getGroups().get(groupName));
            } else {
                plugin.getLogger().warning("Unknown group adding to user " + groupName + " removing.");
                DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", player.getUniqueId().toString()),
                        new BasicDBObject("$pull", new BasicDBObject("groups", groupName)));
            }
        }

        if (groups.isEmpty() == false) {
            Collections.sort(groups, new Comparator<Group>() {
                @Override
                public int compare(Group o1, Group o2) {
                    if (o1.getWeight() > o2.getWeight()) {
                        return -1;
                    }
                    if (o1.getWeight() < o2.getWeight()) {
                        return 1;
                    }
                    return 0;
                }
            });
            addInheritance(groups.get(0), player);
        }

        BasicDBList permissionsList = (BasicDBList) userObject.get("permissions");
        for (Object aPermissionsList : permissionsList) {
            DBObject permissionObject = (DBObject) aPermissionsList;
            String permissionString = (String) permissionObject.get("permission");
            String serverType = (String) permissionObject.get("serverType");
            Permission permission = new Permission();
            permission.setPermission(permissionString);
            permission.setServerType(serverType);
            permissions.add(permission);
        }

        for (Permission permission : permissions) {
            if (permission.getServerType().equalsIgnoreCase("bungee") == false) {
                continue;
            }
            if (permission.getPermission().startsWith("-")) {
                player.setPermission(permission.getPermission().substring(1, permission.getPermission().length()), false);
            } else {
                player.setPermission(permission.getPermission(), true);
            }
        }
    }

    public void createUserInfo(ProxiedPlayer player) {
        if (player == null) {
            return;
        }
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", player.getUniqueId().toString()),
                new BasicDBObject("$set", new BasicDBObject("groups", new BasicDBList())));
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", player.getUniqueId().toString()),
                new BasicDBObject("$set", new BasicDBObject("permissions", new BasicDBList())));
        DatabaseAPI.getMongoDatabase().updateDocument("mn2_users", new BasicDBObject("userUUID", player.getUniqueId().toString()),
                new BasicDBObject("$push", new BasicDBObject("groups", plugin.getMainConfig().defaultGroup)));
    }
}
