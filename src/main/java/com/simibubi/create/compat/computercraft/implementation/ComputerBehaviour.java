package com.simibubi.create.compat.computercraft.implementation;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.implementation.peripherals.DisplayLinkPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SequencedGearshiftPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SpeedControllerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SpeedGaugePeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StationPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StressGaugePeripheral;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class ComputerBehaviour extends AbstractComputerBehaviour {

	protected static final Capability<IPeripheral> PERIPHERAL_CAPABILITY =
		CapabilityManager.get(new CapabilityToken<>() {
		});
	LazyOptional<IPeripheral> peripheral;
	NonNullSupplier<IPeripheral> peripheralSupplier;

	public ComputerBehaviour(SmartBlockEntity te) {
		super(te);
		this.peripheralSupplier = getPeripheralFor(te);
	}

	public static NonNullSupplier<IPeripheral> getPeripheralFor(SmartBlockEntity te) {
		if (te instanceof SpeedControllerBlockEntity scbe)
			return () -> new SpeedControllerPeripheral(scbe, scbe.targetSpeed);
		if (te instanceof DisplayLinkBlockEntity dlbe)
			return () -> new DisplayLinkPeripheral(dlbe);
		if (te instanceof SequencedGearshiftBlockEntity sgbe)
			return () -> new SequencedGearshiftPeripheral(sgbe);
		if (te instanceof SpeedGaugeBlockEntity sgbe)
			return () -> new SpeedGaugePeripheral(sgbe);
		if (te instanceof StressGaugeBlockEntity sgbe)
			return () -> new StressGaugePeripheral(sgbe);
		if (te instanceof StationBlockEntity sbe)
			return () -> new StationPeripheral(sbe);

		throw new IllegalArgumentException("No peripheral available for " + te.getType()
			.getRegistryName());
	}

	@Override
	public <T> boolean isPeripheralCap(Capability<T> cap) {
		return cap == PERIPHERAL_CAPABILITY;
	}

	@Override
	public <T> LazyOptional<T> getPeripheralCapability() {
		if (peripheral == null || !peripheral.isPresent())
			peripheral = LazyOptional.of(peripheralSupplier);
		return peripheral.cast();
	}

	@Override
	public void removePeripheral() {
		if (peripheral != null)
			peripheral.invalidate();
	}

}
