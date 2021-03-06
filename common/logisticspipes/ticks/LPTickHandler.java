package logisticspipes.ticks;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.routing.pathfinder.changedetection.LPWorldAccess;
import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.tuples.LPPosition;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class LPTickHandler {
	
	public static int adjChecksDone = 0;
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent event) {
		FluidIdentifier.initFromForge(true);
		SimpleServiceLocator.clientBufferHandler.clientTick(event);
		MainProxy.proxy.tickClient();
	}

	@SubscribeEvent
	public void serverTick(ServerTickEvent event) {
		HudUpdateTick.tick();
		SimpleServiceLocator.craftingPermissionManager.tick();
		SimpleServiceLocator.serverBufferHandler.serverTick(event);
		MainProxy.proxy.tickServer();
		adjChecksDone = 0;
	}

	private static Map<World, LPWorldInfo> worldInfo = new ConcurrentHashMap<World, LPWorldInfo>();

	@SubscribeEvent
	public void worldTick(WorldTickEvent event) {
		if(event.phase != Phase.END) return;
		if(event.side != Side.SERVER) return;
		LPWorldInfo info = LPTickHandler.getWorldInfo(event.world);
		info.worldTick++;
	}

	public static LPWorldInfo getWorldInfo(World world) {
		LPWorldInfo info = worldInfo.get(world);
		if(info == null) {
			info = new LPWorldInfo();
			worldInfo.put(world, info);
			world.addWorldAccess(new LPWorldAccess(world, info));
		}
		return info;
	}
	
	@Data
	public static class LPWorldInfo {
		@Getter
		@Setter(value=AccessLevel.PRIVATE)
		private long worldTick = 0;
		@Getter
		private Set<LPPosition> updateQueued = new HashSet<LPPosition>();
	}
}
