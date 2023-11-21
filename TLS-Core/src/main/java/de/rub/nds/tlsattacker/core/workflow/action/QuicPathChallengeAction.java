/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.workflow.action;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.rub.nds.modifiablevariable.HoldsModifiableVariable;
import de.rub.nds.tlsattacker.core.exceptions.ActionExecutionException;
import de.rub.nds.tlsattacker.core.quic.frame.PathChallengeFrame;
import de.rub.nds.tlsattacker.core.quic.frame.PathResponseFrame;
import de.rub.nds.tlsattacker.core.quic.frame.QuicFrame;
import de.rub.nds.tlsattacker.core.quic.packet.QuicPacket;
import de.rub.nds.tlsattacker.core.state.State;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

/**
 * Action to solve path challenge after connection migration by client. Handles variable number of
 * path challenges as servers can decide how many path challenges to send.
 */
@XmlRootElement
public class QuicPathChallengeAction extends TlsAction {

    @XmlTransient protected boolean executedAsPlanned = false;

    @XmlTransient protected List<MessageAction> executedActions = new ArrayList<>();

    @XmlTransient protected int pathChallengeCounter = 0;

    @XmlElement protected Boolean requireAtLeastOnePathChallenge = false;

        @HoldsModifiableVariable @XmlElementWrapper @XmlElementRef
    protected List<QuicFrame> quicFrames = new ArrayList<>();

    @HoldsModifiableVariable @XmlElementWrapper @XmlElementRef
    protected List<QuicPacket> quicPackets = new ArrayList<>();

    public QuicPathChallengeAction() {
        super();
    }

    public QuicPathChallengeAction(boolean requireAtLeastOnePathChallenge) {
        super();
        this.requireAtLeastOnePathChallenge = requireAtLeastOnePathChallenge;
    }

    @Override
    public void execute(State state) throws ActionExecutionException {
        MessageAction action;
        do {
            action = executeAction(state, new ReceiveQuicTillAction(new PathChallengeFrame()));
            if (action.executedAsPlanned()) {
                action = executeAction(state, new SendAction(new PathResponseFrame()));
                if (!action.executedAsPlanned()) {
                    executedAsPlanned = false;
                    return;
                }
                pathChallengeCounter++;
            } else if (pathChallengeCounter == 0 && requireAtLeastOnePathChallenge) {
                executedAsPlanned = false;
                return;
            } else {
                executedAsPlanned = true;
                return;
            }
        } while (action.executedAsPlanned());
    }

    private MessageAction executeAction(State state, MessageAction action)
            throws ActionExecutionException {
        action.execute(state);
        quicFrames.addAll(action.getQuicFrames());
        quicPackets.addAll(action.getQuicPackets());
        executedActions.add(action);
        return action;
    }

    @Override
    public String toString() {
        String string =
                getClass().getSimpleName() + ": " + (isExecuted() ? "\n" : "(not executed)");
        if (isExecuted()) {
            string +=
                    "\n\tExecuted Actions:"
                            + "\t"
                            + executedActions.stream()
                                    .map(MessageAction::toString)
                                    .collect(Collectors.joining("\n\t"));
        }
        return string;
    }

    @Override
    public String toCompactString() {
        return super.toCompactString()
                + " (performed "
                + pathChallengeCounter
                + " path challenge(s))";
    }

    @Override
    public void reset() {}

    @Override
    public boolean executedAsPlanned() {
        return executedAsPlanned;
    }
}
