package com.onlytanner.industrialmetallurgy.util;

import com.tterrag.registrate.AbstractRegistrate;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Registrate's own AbstractRegistrate#onData isn't idempotent against GatherDataEvent.Client
 * firing more than once on this NeoForge version -- the same underlying issue documented on
 * IndustrialMetallurgy.REGISTRATE's skipErrors(true) call (RegisterEvent also fires more than
 * once; AbstractRegistrate's own source acknowledges this in a comment but doesn't guard onData
 * the way onRegister can be guarded via skipErrors). A second firing tries to add a second,
 * duplicate-named data provider and crashes `./gradlew runData`. There's no public config flag
 * for this one, so it's guarded here with a plain one-shot flag instead.
 */
public final class IMRegistrate extends AbstractRegistrate<IMRegistrate> {

    private boolean dataProviderAdded;

    private IMRegistrate(String modid) {
        super(modid);
    }

    public static IMRegistrate create(String modid) {
        return new IMRegistrate(modid);
    }

    @Override
    protected void onData(GatherDataEvent event) {
        if (dataProviderAdded) {
            return;
        }
        dataProviderAdded = true;
        super.onData(event);
    }

}
