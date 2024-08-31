package net.danh.dungeons.Party;

import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PartyManager {

    public static HashMap<String, String> partyManager = new HashMap<>();
    public static HashMap<String, List<String>> partyMember = new HashMap<>();
    public static HashMap<String, String> partyInformation = new HashMap<>();

    public static void createParty(Player p, String id) {
        if (!partyManager.containsValue(id)) {
            partyManager.put(p.getName(), id);
            partyInformation.put(id + PartyData.leader.getString(), p.getName());
            partyInformation.put(id + PartyData.display.getString(), getPartyDisplay(id));
            partyMember.put(id, Collections.singletonList(p.getName()));
            Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.create"))
                    .replace("<name>", getPartyDisplay(id)));
        } else Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.already_has_name"))
                .replace("<name>", getPartyDisplay(id)));
    }

    public static void disbandParty(Player p) {
        String id = getPartyID(p);
        if (partyManager.containsValue(id) && partyManager.containsKey(p.getName())) {
            if (partyInformation.get(id + PartyData.leader.getString()).contains(p.getName())) {
                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.disband"))
                        .replace("<name>", getPartyDisplay(id)));
                List<String> pInfo = new ArrayList<>(partyInformation.keySet());
                for (String key : pInfo) {
                    if (key.contains(id))
                        partyInformation.remove(key);
                }
                List<String> pManager = new ArrayList<>(partyManager.keySet());
                for (String key : pManager) {
                    if (partyManager.get(key).equalsIgnoreCase(id))
                        partyManager.remove(key, id);
                }
                partyMember.remove(id);
            } else Chat.sendMessage(p, Files.getMessage().getString("party.not_leader"));
        } else Chat.sendMessage(p, Files.getMessage().getString("party.not_in_party"));
    }

    @Contract(pure = true)
    public static @NotNull List<Player> getMembers(Player p) {
        List<Player> players = new ArrayList<>();
        if (inParty(p)) {
            if (isPartyLeader(p)) {
                String party = getPartyID(p);
                List<String> members = partyMember.get(party);
                members.forEach(member -> {
                    Player player = Bukkit.getPlayer(member);
                    if (player != null) {
                        players.add(player);
                    }
                });
            }
        }
        return players;
    }

    @Contract(pure = true)
    public static @NotNull List<String> getMembersName(Player p) {
        List<String> players = new ArrayList<>();
        if (inParty(p)) {
            if (isPartyLeader(p)) {
                String party = getPartyID(p);
                List<String> members = partyMember.get(party);
                players.addAll(members);
            }
        }
        return players;
    }

    public static @NotNull String getPartyDisplay(String id) {
        return partyInformation.containsKey(id + PartyData.display.getString())
                ? partyInformation.get(id + PartyData.display.getString()) : Objects.requireNonNull(Files.getConfig().getString("party.default_display"))
                .replace("<name>", Chat.caseOnWords(id));
    }

    public static boolean inParty(@NotNull Player p) {
        return partyManager.containsKey(p.getName());
    }

    public static boolean isPartyLeader(@NotNull Player p) {
        return inParty(p)
                && partyInformation.containsKey(getPartyID(p) + PartyData.leader.getString())
                && partyInformation.get(getPartyID(p) + PartyData.leader.getString()).equalsIgnoreCase(p.getName());
    }

    @Contract(pure = true)
    public static @Nullable String getPartyID(Player p) {
        if (inParty(p)) {
            return partyManager.get(p.getName());
        }
        return null;
    }

    public static @Nullable Player getPartyLeader(Player p) {
        if (inParty(p)) {
            if (!isPartyLeader(p)) {
                return Bukkit.getPlayer(partyInformation.get(getPartyID(p) + PartyData.leader.getString()));
            }
        } else Chat.sendMessage(p, Files.getMessage().getString("party.not_in_party"));
        return null;
    }

    public static void setDisplay(Player p, String display) {
        if (inParty(p)) {
            if (isPartyLeader(p)) {
                String party = getPartyID(p);
                String beforeDisplay = getPartyDisplay(party);
                partyInformation.replace(party + PartyData.display.getString(), Chat.normalColorize(display));
                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.rename"))
                        .replace("<name>", beforeDisplay)
                        .replace("<new_name>", getPartyDisplay(party)));
            } else Chat.sendMessage(p, Files.getMessage().getString("party.not_leader"));
        } else Chat.sendMessage(p, Files.getMessage().getString("party.not_in_party"));
    }

    public static void kick(Player p) {
        if (inParty(p)) {
            if (!isPartyLeader(p)) {
                String party = getPartyID(p);
                List<String> members = new ArrayList<>(partyMember.get(party));
                members.remove(p.getName());
                partyMember.replace(party, members);
                partyManager.remove(p.getName(), party);
                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.kick"))
                        .replace("<name>", getPartyDisplay(party)));
            } else Chat.sendMessage(p, Files.getMessage().getString("part.cant_kick_leader"));
        } else Chat.sendMessage(p, Files.getMessage().getString("party.not_in_party"));
    }

    public static void invite(Player leader, Player invited) {
        if (inParty(leader)) {
            if (isPartyLeader(leader)) {
                if (!inParty(invited)) {
                    if (!isPartyLeader(invited)) {
                        String party = getPartyID(leader);
                        List<String> members = new ArrayList<>(partyMember.get(party));
                        members.forEach(member -> {
                            Player p = Bukkit.getPlayer(member);
                            if (p != null) {
                                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.party_join"))
                                        .replace("<player>", invited.getName()));
                            }
                        });
                        members.add(invited.getName());
                        partyMember.replace(party, members);
                        partyManager.put(invited.getName(), party);
                        Chat.sendMessage(invited, Objects.requireNonNull(Files.getMessage().getString("party.player_join"))
                                .replace("<name>", getPartyDisplay(party)));
                    } else
                        Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_in_party"))
                                .replace("<name>", invited.getName()));
                } else
                    Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_in_party"))
                            .replace("<name>", invited.getName()));
            } else Chat.sendMessage(leader, Files.getMessage().getString("party.not_leader"));
        } else Chat.sendMessage(leader, Files.getMessage().getString("party.not_in_party"));
    }

    public enum PartyData {

        leader("_leader"),
        display("_display");

        PartyData(String id) {
        }

        @Contract(pure = true)
        public @NotNull String getString() {
            return name();
        }
    }

}
