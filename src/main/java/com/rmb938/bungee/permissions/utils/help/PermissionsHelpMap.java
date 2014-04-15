package com.rmb938.bungee.permissions.utils.help;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.rmb938.bungee.base.utils.help.*;
import com.rmb938.bungee.permissions.MN2BungeePermissions;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class PermissionsHelpMap implements HelpMap {

    private HelpTopic defaultTopic;
    private final Map<String, HelpTopic> helpTopics;
    private final MN2BungeePermissions plugin;

    public PermissionsHelpMap(MN2BungeePermissions plugin) {
        this.helpTopics = new TreeMap<>(HelpTopicComparator.topicNameComparatorInstance()); // Using a TreeMap for its explicit sorting on key
        this.plugin = plugin;

        Predicate indexFilter = Predicates.not(Predicates.instanceOf(CommandAliasHelpTopic.class));

        this.defaultTopic = new IndexHelpTopic("Index", null, null, Collections2.filter(helpTopics.values(), indexFilter), "Use /permissions help [n] to get page n of help.");
    }

    @Override
    public HelpTopic getHelpTopic(String topicName) {
        if (topicName.equals("")) {
            return defaultTopic;
        }

        if (helpTopics.containsKey(topicName)) {
            return helpTopics.get(topicName);
        }

        return null;
    }

    @Override
    public Collection<HelpTopic> getHelpTopics() {
        return helpTopics.values();
    }

    @Override
    public void addTopic(HelpTopic topic) {
// Existing topics take priority
        if (!helpTopics.containsKey(topic.getName())) {
            helpTopics.put(topic.getName(), topic);
        }
    }

    @Override
    public void clear() {
        helpTopics.clear();
    }
}
