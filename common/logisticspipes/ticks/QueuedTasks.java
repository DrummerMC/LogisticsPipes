package logisticspipes.ticks;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import logisticspipes.proxy.MainProxy;
import logisticspipes.transport.LPTravelingItem;
import logisticspipes.utils.tuples.Pair;
import lombok.SneakyThrows;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public class QueuedTasks {
	
	@SuppressWarnings("rawtypes")
	private static LinkedList<Callable> queue = new LinkedList<Callable>();
	
	private static LinkedList<Callable<Boolean>> adjChecks = new LinkedList<Callable<Boolean>>();
	
	// called on server shutdown only.
	public static void clearAllTasks() {
		queue.clear();
		adjChecks.clear();
	}
	
	@SuppressWarnings("rawtypes")
	public static void queueTask(Callable task) {
		synchronized (queue) {
			queue.add(task);
		}
	}
	
	public static void queueAdjCheck(Callable<Boolean> task) {
		synchronized (adjChecks) {
			adjChecks.add(task);
		}
	}
	
	@SuppressWarnings({"rawtypes" })
	@SubscribeEvent
	@SneakyThrows(Exception.class)
	public void tickEnd(ServerTickEvent event) {
		if(event.phase != Phase.END) return;
		Callable call = null;
		while(!queue.isEmpty()) {
			synchronized (queue) {
				call = queue.removeFirst();
			}
			if(call != null) {
				try {
					call.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		Callable<Boolean> adjCall = null;
		long start = System.currentTimeMillis();
		int totalCount = 0;
		while(!adjChecks.isEmpty() && (System.currentTimeMillis() - start < 10/* || totalCount < 200*/)) {
			int count = 0;
			//while(!adjChecks.isEmpty() && count < 100) {
				synchronized (adjChecks) {
					adjCall = adjChecks.removeFirst();
				}
				if(adjCall != null) {
					if(adjCall.call()) {
						count++;
						totalCount++;
					}
			//	}
			}
		}
		if(totalCount > 0) System.out.println(totalCount);
		MainProxy.proxy.tick();
		synchronized(LPTravelingItem.forceKeep) {
			Iterator<Pair<Integer, Object>> iter = LPTravelingItem.forceKeep.iterator();
			while(iter.hasNext()) {
				Pair<Integer, Object> pair = iter.next();
				pair.setValue1(pair.getValue1() - 1);
				if(pair.getValue1() < 0) {
					iter.remove();
				}
			}
		}
	}
}
