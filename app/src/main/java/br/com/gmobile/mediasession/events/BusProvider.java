package br.com.gmobile.mediasession.events;

import com.squareup.otto.Bus;

/**
 * Created by felipearimateia on 06/10/15.
 */
public class BusProvider {

    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
        // No instances.
    }
}
