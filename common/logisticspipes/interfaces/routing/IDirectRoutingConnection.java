package logisticspipes.interfaces.routing;

import logisticspipes.logisticspipes.IRoutedItem.TransportMode;
import logisticspipes.routing.ItemRoutingInformation;
import logisticspipes.utils.item.ItemIdentifier;

public interface IDirectRoutingConnection {
	public int getConnectionResistance();
	public void addItem(ItemRoutingInformation info);
}
