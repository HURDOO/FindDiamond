package kr.kro.hurdoo.finddiamond;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

public class DiamondFinder implements CommandExecutor {

    private final int[] dx = {1,-1,0,0,0,0};
    private final int[] dy = {0,0,0,-1,0,0}; // up doesn't required
    private final int[] dz = {0,0,0,0,1,-1};

    private class LocInt {

        public LocInt(int x,int y,int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public int x;
        public int y;
        public int z;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        Location loc = p.getLocation();
        loc.setY(16);

        Queue<LocInt> locQ = new ArrayDeque<>();
        HashMap<String,Boolean> visit = new HashMap<>();

        LocInt curLoc = new LocInt(loc.getBlockX(),loc.getBlockY(),loc.getBlockZ());
        locQ.add(curLoc);

        Chunk chunk = loc.getChunk();
        System.out.printf("Current Chunk: %d\n",chunk.getChunkKey());

        Thread thread = new Thread(() -> {
            boolean found = false;
            while(!locQ.isEmpty())
            {
                LocInt curLoc1 = locQ.poll();

                String hash = curLoc1.x + "/" + curLoc1.y + '/' + curLoc1.z;
                if(visit.get(hash) != null) continue;
                visit.put(hash,true);

                System.out.printf("Now Locating: %d %d %d\n",
                        curLoc1.x, curLoc1.y, curLoc1.z);

                Location loc1 = new Location(p.getWorld(), curLoc1.x, curLoc1.y, curLoc1.z);
                if(loc1.getBlock().getType().equals(Material.DIAMOND_ORE))
                {
                    locQ.clear();
                    visit.clear();

                    p.sendMessage(ChatColor.YELLOW + "다이아몬드 광석을 찾았다!");
                    p.sendMessage(ChatColor.GREEN + String.format("[%d %d %d]",
                            loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ()));
                    found = true;
                }
                else
                {
                    for(int i=0;i<6;i++)
                    {
                        Location nextLoc = loc1.clone().add(dx[i],dy[i],dz[i]);
                        if(!isSameChunk(chunk,nextLoc.getChunk()))
                        {
                            System.out.printf("%d: %d %d %d\n",
                                    nextLoc.getChunk().getChunkKey(),
                                    nextLoc.getBlockX(), nextLoc.getBlockY(), nextLoc.getBlockZ());
                            continue;
                        }
                        if(nextLoc.getY() < 0) continue;
                        LocInt nextLocInt = new LocInt(
                                nextLoc.getBlockX(), nextLoc.getBlockY(), nextLoc.getBlockZ());
                        locQ.add(nextLocInt);
                    }
                }
            }
            if(!found) p.sendMessage(ChatColor.RED + "다이아몬드를 찾지 못했습니다");
        });
        thread.start();
        p.sendMessage("다이아몬드 탐색을 시작합니다");
        return true;
    }

    private boolean isSameChunk(Chunk a,Chunk b)
    {
        //return a.getX() == b.getX() && a.getZ() == b.getZ();
        return a.getChunkKey() == b.getChunkKey();
    }
}
