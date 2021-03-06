package logisticspipes.proxy.specialinventoryhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.InvalidVersionSpecificationException;
import cpw.mods.fml.common.versioning.VersionRange;
import logisticspipes.utils.item.ItemIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;

public class StorageDrawersInventoryHandler extends SpecialInventoryHandler {

	private final IDrawerGroup _drawer;
	private final boolean _hideOnePerStack;
	private final boolean _hideOnePerType;

	private StorageDrawersInventoryHandler(TileEntity tile, boolean hideOnePerStack, boolean hideOne, int cropStart, int cropEnd) {
		_drawer = (IDrawerGroup) tile;
		_hideOnePerStack = hideOnePerStack;
		_hideOnePerType = hideOne;
	}

	public StorageDrawersInventoryHandler() {
		_drawer = null;
		_hideOnePerStack = false;
		_hideOnePerType = false;
	}

	@Override
	public boolean init() {
		List<ModContainer> modList = Loader.instance().getModList();
		for (int i = 0, n = modList.size(); i < n; i++) {
			ModContainer mod = modList.get(i);
			if (mod.getModId().equals("StorageDrawers")) {
				try {
					VersionRange validVersions = VersionRange.createFromVersionSpec("[1.3.4,)");
					ArtifactVersion version = new DefaultArtifactVersion(mod.getVersion());
					return validVersions.containsVersion(version);
				}
				catch (InvalidVersionSpecificationException e) {
					return false;
				}
			}
		}

		return false;
	}

	@Override
	public boolean isType(TileEntity tile) {
		return tile instanceof IDrawerGroup;
	}

	@Override
	public SpecialInventoryHandler getUtilForTile(TileEntity tile, ForgeDirection dir, boolean hideOnePerStack, boolean hideOne, int cropStart, int cropEnd) {
		return new StorageDrawersInventoryHandler(tile, hideOnePerStack, hideOne, cropStart, cropEnd);
	}


	@Override
	public int itemCount(ItemIdentifier itemIdent) {
		int count = 0;
		boolean first = true;
		for (int i = 0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if (drawer == null)
				continue;

			if (!drawer.isEmpty() && ItemIdentifier.get(drawer.getStoredItemPrototype()).equals(itemIdent)) {
				count += drawer.getStoredItemCount() - ((_hideOnePerStack || (_hideOnePerType && first)) ? 1 : 0);
				first = false;
			}
		}

		return count;
	}

	@Override
	public ItemStack getMultipleItems(ItemIdentifier itemIdent, int count) {
		ItemStack stack = null;
		for (int i = 0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if (drawer == null || drawer.isEmpty())
				continue;

			if (ItemIdentifier.get(drawer.getStoredItemPrototype()).equals(itemIdent)) {
				if (stack == null) {
					stack = drawer.getStoredItemCopy();
					stack.stackSize = 0;
				}

				int avail = Math.min(count, drawer.getStoredItemCount());
				drawer.setStoredItemCount(drawer.getStoredItemCount() - avail);

				stack.stackSize += avail;
				count -= avail;

				if (count <= 0)
					break;
			}
		}

		return stack;
	}

	@Override
	public Set<ItemIdentifier> getItems() {
		Set<ItemIdentifier> result = new TreeSet<ItemIdentifier>();
		for(int i=0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if(drawer != null && !drawer.isEmpty() && drawer.getStoredItemCount() > 0) {
				result.add(ItemIdentifier.get(drawer.getStoredItemPrototype()));
			}
		}
		return result;
	}
	
	@Override
	public HashMap<ItemIdentifier, Integer> getItemsAndCount() {
		HashMap<ItemIdentifier, Integer> result = new HashMap<ItemIdentifier, Integer>();
		for(int i=0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if(drawer != null && !drawer.isEmpty()) {
				int count = drawer.getStoredItemCount();
				if (count > 0) {
					ItemIdentifier ident = ItemIdentifier.get(drawer.getStoredItemPrototype());
					if (result.containsKey(ident)) {
						result.put(ident, result.get(ident) + count);
					} else {
						result.put(ident, count);
					}
				}
			}
		}
		return result;
	}

	@Override
	public ItemStack getSingleItem(ItemIdentifier itemIdent) {
		return getMultipleItems(itemIdent, 1);
	}

	@Override
	public boolean containsItem(ItemIdentifier itemIdent) {
		for(int i=0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if(drawer != null && !drawer.isEmpty()) {
				if(drawer.canItemBeStored(itemIdent.makeNormalStack(1))) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsUndamagedItem(ItemIdentifier itemIdent) {
		for(int i=0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if(drawer != null && !drawer.isEmpty()) {
				if(ItemIdentifier.get(drawer.getStoredItemPrototype()).getUndamaged().equals(itemIdent)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int roomForItem(ItemIdentifier item) {
		return roomForItem(item, 0);
	}

	@Override
	public int roomForItem(ItemIdentifier itemIdent, int count) {
		int room = 0;
		for (int i = 0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if (drawer == null)
				continue;

			ItemStack protoStack = itemIdent.makeNormalStack(1);
			if (drawer.canItemBeStored(protoStack)) {
				if (drawer.isEmpty()) {
					room += drawer.getMaxCapacity(protoStack);
				}
				else {
					room += drawer.getRemainingCapacity();
				}
			}

			if (count != 0 && room >= count)
				return count;
		}

		return room;
	}

	@Override
	public ItemStack add(ItemStack stack, ForgeDirection from, boolean doAdd) {
		ItemStack st = stack.copy();
		st.stackSize = 0;

		for (int i = 0; i < _drawer.getDrawerCount(); i++) {
			if (!_drawer.isDrawerEnabled(i))
				continue;

			IDrawer drawer = _drawer.getDrawer(i);
			if (drawer == null)
				continue;

			if (drawer.canItemBeStored(stack)) {
				int avail = 0;
				if (drawer.isEmpty()) {
					avail = Math.min(stack.stackSize, drawer.getMaxCapacity(stack));
					drawer.setStoredItem(stack.copy(), avail);
				}
				else {
					avail = Math.min(stack.stackSize, drawer.getRemainingCapacity());
					drawer.setStoredItemCount(drawer.getStoredItemCount() + avail);
				}

				stack.stackSize -= avail;
				st.stackSize += avail;

				if (stack.stackSize <= 0)
					break;
			}
		}

		return st;
	}

	@Override
	public boolean isSpecialInventory() {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return _drawer.getDrawerCount();
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (!_drawer.isDrawerEnabled(i))
			return null;

		return _drawer.getDrawer(i) != null ? _drawer.getDrawer(i).getStoredItemCopy() : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (!_drawer.isDrawerEnabled(i))
			return null;

		IDrawer drawer = _drawer.getDrawer(i);
		if (drawer == null || drawer.isEmpty())
			return null;

		int avail = Math.min(j, drawer.getStoredItemCount());
		drawer.setStoredItemCount(drawer.getStoredItemCount() - avail);

		ItemStack stack = drawer.getStoredItemCopy();
		stack.stackSize = avail;

		return stack;
	}
}
