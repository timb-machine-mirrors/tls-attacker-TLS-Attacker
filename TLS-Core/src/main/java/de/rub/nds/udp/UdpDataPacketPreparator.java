/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.udp;

import de.rub.nds.tlsattacker.core.layer.data.Preparator;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;

public class UdpDataPacketPreparator extends Preparator<UdpDataPacket> {

    public UdpDataPacketPreparator(Chooser chooser, UdpDataPacket udpDataPacket) {
        super(chooser, udpDataPacket);
    }

    @Override
    public void prepare() {
        getObject().setData(getObject().getConfigData());
    }
}
