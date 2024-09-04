package net.danh.dungeons.Party;

import net.danh.dungeons.Dungeon.StageManager;
import net.danh.dungeons.Dungeons;
import net.danh.dungeons.Resources.Chat;
import net.danh.dungeons.Resources.Files;
import net.danh.dungeons.Utils.CountdownTimer;
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
    public static HashMap<String, Player> inviteParty = new HashMap<>();
    public static HashMap<Player, CountdownTimer> sentInvite = new HashMap<>();

    public static void createParty(Player p, String id) {
        if (!partyManager.containsValue(id)) {
            if (partyManager.containsKey(p.getName()))
                partyManager.replace(p.getName(), id);
            else partyManager.put(p.getName(), id);
            if (partyInformation.containsKey(id + PartyData.leader.getString()))
                partyInformation.replace(id + PartyData.leader.getString(), p.getName());
            else partyInformation.put(id + PartyData.leader.getString(), p.getName());
            if (partyInformation.containsKey(id + PartyData.display.getString()))
                partyInformation.replace(id + PartyData.display.getString(), getPartyDisplay(id));
            else partyInformation.put(id + PartyData.display.getString(), getPartyDisplay(id));
            if (partyMember.containsKey(id))
                partyMember.replace(id, Collections.singletonList(p.getName()));
            else partyMember.put(id, Collections.singletonList(p.getName()));
            Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.create"))
                    .replace("<name>", getPartyDisplay(id)));
        } else Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.already_has_name"))
                .replace("<name>", getPartyDisplay(id)));
    }

    public static void disbandParty(Player p) {
        String id = getPartyID(p);
        if (id != null) {
            if (partyManager.containsValue(id) && partyManager.containsKey(p.getName())) {
                if (partyInformation.get(id + PartyData.leader.getString()).contains(p.getName())) {
                    if (StageManager.inDungeon(p))
                        StageManager.endPartyDungeon(p, false, true);
                    getMembers(p).forEach(partyMember -> Chat.sendMessage(partyMember, Objects.requireNonNull(Files.getMessage().getString("party.disband"))
                            .replace("<name>", getPartyDisplay(id))));
                    List<String> pInfo = new ArrayList<>(partyInformation.keySet());
                    for (String key : pInfo) {
                        if (key.contains(id))
                            partyInformation.remove(key);
                    }
                    List<String> pManager = new ArrayList<>(partyManager.keySet());
                    for (String key : pManager) {
                        if (partyManager.get(key).equalsIgnoreCase(id))
                            partyManager.remove(key);
                    }
                    partyMember.remove(id);
                } else Chat.sendMessage(p, Files.getMessage().getString("party.not_leader"));
            } else Chat.sendMessage(p, Files.getMessage().getString("party.not_in_party"));
        }
    }

    public static Player getPlayer(Player p) {
        return inParty(p) ? getPartyLeader(p) : p;
    }

    @Contract(pure = true)
    public static @NotNull List<Player> getMembers(Player p) {
        List<Player> players = new ArrayList<>();
        if (inParty(p)) {
            String party = getPartyID(p);
            List<String> members = partyMember.get(party);
            members.forEach(member -> {
                Player player = Bukkit.getPlayer(member);
                if (player != null) {
                    players.add(player);
                }
            });
        }
        return players;
    }

    @Contract(pure = true)
    public static @NotNull List<Player> getMembers(String partyID) {
        List<Player> players = new ArrayList<>();
        if (partyMember.containsKey(partyID)) {
            List<String> members = partyMember.get(partyID);
            members.forEach(member -> {
                Player player = Bukkit.getPlayer(member);
                if (player != null) {
                    players.add(player);
                }
            });
        }
        return players;
    }

    @Contract(pure = true)
    public static @NotNull List<String> getMembersName(Player p) {
        List<String> players = new ArrayList<>();
        if (inParty(p)) {
            String party = getPartyID(p);
            List<String> members = partyMember.get(party);
            players.addAll(members);
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
            return Bukkit.getPlayer(partyInformation.get(getPartyID(p) + PartyData.leader.getString()));
        }
        return null;
    }

    public static @Nullable Player getPartyLeader(String partyID) {
        if (!getMembers(partyID).isEmpty()) {
            for (Player player : getMembers(partyID)) {
                if (isPartyLeader(player))
                    return Bukkit.getPlayer(partyInformation.get(getPartyID(player) + PartyData.leader.getString()));
            }
        }
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
                getMembers(p).forEach(player -> {
                    if (player != p)
                        Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("party.kick"))
                                .replace("<name>", getPartyDisplay(party))
                                .replace("<player>", p.getName()));
                });
                Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.kick_player"))
                        .replace("<name>", getPartyDisplay(party)));
                List<String> members = new ArrayList<>(partyMember.get(party));
                members.remove(p.getName());
                partyMember.replace(party, members);
                partyManager.remove(p.getName(), party);
            } else Chat.sendMessage(p, Files.getMessage().getString("party.cant_kick_leader"));
        }
    }

    public static void sendInvite(Player leader, Player invited) {
        if (inParty(leader) && getPartyID(leader) != null) {
            if (isPartyLeader(leader)) {
                if (!inParty(invited)) {
                    if (!isPartyLeader(invited)) {
                        if (!StageManager.inDungeon(leader) && !StageManager.inDungeon(invited)) {
                            if (!(inviteParty.containsKey(leader.getName() + "_" + getPartyID(leader))
                                    && inviteParty.get(leader.getName() + "_" + getPartyID(leader)).equals(invited))) {
                                CountdownTimer countdownTimer = new CountdownTimer(Dungeons.getDungeonCore(), Files.getConfig().getInt("party.timeout_invite", 120)
                                        , () -> {
                                    inviteParty.put(leader.getName() + "_" + getPartyID(leader), invited);
                                    getMembers(leader).forEach(player ->
                                            Chat.sendMessage(player, Objects.requireNonNull(Files.getMessage().getString("party.send_invite"))
                                                    .replace("<name>", getPartyDisplay(getPartyID(leader)))
                                                    .replace("<player>", invited.getName())));
                                    Chat.sendMessage(invited, Objects.requireNonNull(Files.getMessage().getString("party.send_player_invite"))
                                            .replace("<name>", getPartyDisplay(getPartyID(leader)))
                                            .replace("<player>", invited.getName())
                                            .replace("<id>", Objects.requireNonNull(getPartyID(leader))));
                                },
                                        timer -> {

                                        },
                                        () -> {
                                            if (inviteParty.containsKey(leader.getName() + "_" + getPartyID(leader))
                                                    && inviteParty.get(leader.getName() + "_" + getPartyID(leader)).equals(invited)) {
                                                inviteParty.remove(leader.getName() + "_" + getPartyID(leader), invited);
                                                Chat.sendMessage(invited, Objects.requireNonNull(Files.getMessage().getString("party.expired_player_invite"))
                                                        .replace("<player>", leader.getName())
                                                        .replace("<name>", getPartyDisplay(getPartyID(leader))));
                                                Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.expired_invite"))
                                                        .replace("<player>", invited.getName()));
                                            }
                                        });
                                countdownTimer.scheduleTimer();
                                sentInvite.put(invited, countdownTimer);
                            } else
                                Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_invite"))
                                        .replace("<name>", getPartyDisplay(getPartyID(leader)))
                                        .replace("<player>", invited.getName())
                                        .replace("<id>", Objects.requireNonNull(getPartyID(leader))));
                        } else {
                            if (StageManager.inDungeon(leader))
                                Chat.sendMessage(leader,
                                        Objects.requireNonNull(Files.getMessage().getString("party.in_dungeon"))
                                                .replace("<player>", invited.getName()));
                            else Chat.sendMessage(leader,
                                    Objects.requireNonNull(Files.getMessage().getString("party.player_in_dungeon"))
                                            .replace("<player>", invited.getName()));
                        }
                    } else
                        Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_in_party"))
                                .replace("<name>", invited.getName()));
                } else
                    Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_in_party"))
                            .replace("<name>", invited.getName()));
            } else Chat.sendMessage(leader, Files.getMessage().getString("party.not_leader"));
        } else Chat.sendMessage(leader, Files.getMessage().getString("party.not_in_party"));
    }

    public static void invite(Player invited, String partyID) {
        Player leader = getPartyLeader(partyID);
        if (leader != null) {
            if (inParty(leader)) {
                if (isPartyLeader(leader)) {
                    if (!inParty(invited)) {
                        if (!isPartyLeader(invited)) {
                            if (!StageManager.inDungeon(leader) && !StageManager.inDungeon(invited)) {
                                if (inviteParty.containsKey(leader.getName() + "_" + partyID)
                                        && inviteParty.get(leader.getName() + "_" + partyID).equals(invited)) {
                                    inviteParty.remove(leader.getName() + "_" + partyID, invited);
                                    if (sentInvite.containsKey(invited))
                                        if (sentInvite.get(invited) != null)
                                            sentInvite.get(invited).cancelCountdown();
                                    List<String> members = new ArrayList<>(partyMember.get(partyID));
                                    members.forEach(member -> {
                                        Player p = Bukkit.getPlayer(member);
                                        if (p != null) {
                                            Chat.sendMessage(p, Objects.requireNonNull(Files.getMessage().getString("party.party_join"))
                                                    .replace("<player>", invited.getName()));
                                        }
                                    });
                                    members.add(invited.getName());
                                    partyMember.replace(partyID, members);
                                    partyManager.put(invited.getName(), partyID);
                                    Chat.sendMessage(invited, Objects.requireNonNull(Files.getMessage().getString("party.player_join"))
                                            .replace("<name>", getPartyDisplay(partyID)));
                                } else Chat.sendMessage(invited, Files.getMessage().getString("party.not_invited"));
                            } else {
                                if (StageManager.inDungeon(leader))
                                    Chat.sendMessage(leader,
                                            Objects.requireNonNull(Files.getMessage().getString("party.in_dungeon"))
                                                    .replace("<player>", invited.getName()));
                                else Chat.sendMessage(leader,
                                        Objects.requireNonNull(Files.getMessage().getString("party.player_in_dungeon"))
                                                .replace("<player>", invited.getName()));
                            }
                        } else
                            Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_in_party"))
                                    .replace("<name>", invited.getName()));
                    } else
                        Chat.sendMessage(leader, Objects.requireNonNull(Files.getMessage().getString("party.already_in_party"))
                                .replace("<name>", invited.getName()));
                } else Chat.sendMessage(leader, Files.getMessage().getString("party.not_leader"));
            } else Chat.sendMessage(leader, Files.getMessage().getString("party.not_in_party"));
        }
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
