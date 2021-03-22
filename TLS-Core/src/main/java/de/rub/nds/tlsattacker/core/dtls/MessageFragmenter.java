/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.dtls;

import de.rub.nds.tlsattacker.core.protocol.message.DtlsHandshakeMessageFragment;
import de.rub.nds.tlsattacker.core.protocol.message.HandshakeMessage;
import de.rub.nds.tlsattacker.core.protocol.serializer.HandshakeMessageSerializer;
import de.rub.nds.tlsattacker.core.protocol.serializer.ProtocolMessageSerializer;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.util.LinkedList;
import java.util.List;
import org.bouncycastle.util.Arrays;

/**
 * Class used to split HandshakeMessages into DTLS fragments.
 */
public class MessageFragmenter {

    private final int maxFragmentLength;

    public MessageFragmenter(int maxFragmentLength) {
        this.maxFragmentLength = maxFragmentLength;
    }

    /**
     * Takes a message and splits it into prepared fragments.
     *
     * @param  message
     * @param  context
     * @return
     */
    public List<DtlsHandshakeMessageFragment> fragmentMessage(HandshakeMessage message, TlsContext context) {
        ProtocolMessageSerializer serializer = message.getHandler(context).getSerializer(message);
        byte[] bytes;
        if (serializer instanceof HandshakeMessageSerializer) {// This is necessary because of SSL2 messages...
            HandshakeMessageSerializer handshakeMessageSerializer =
                (HandshakeMessageSerializer) serializer;
            bytes = handshakeMessageSerializer.serializeHandshakeMessageContent();
        } else {
            bytes = serializer.serializeProtocolMessageContent();
        }
        List<DtlsHandshakeMessageFragment> dtlsFragments =
            generateFragments(message, bytes, maxFragmentLength, context);
        return dtlsFragments;
    }

    /**
     * Generates a single fragment carrying the contents of the message as payload.
     *
     * @param  message
     * @param  context
     * @return
     */
    public DtlsHandshakeMessageFragment wrapInSingleFragment(HandshakeMessage message, TlsContext context) {
        ProtocolMessageSerializer serializer = message.getHandler(context).getSerializer(message);
        byte[] bytes;
        if (serializer instanceof HandshakeMessageSerializer) {// This is necessary because of SSL2 messages...
            HandshakeMessageSerializer handshakeMessageSerializer =
                (HandshakeMessageSerializer) message.getHandler(context).getSerializer(message);
            bytes = handshakeMessageSerializer.serializeHandshakeMessageContent();
        } else {
            bytes = serializer.serializeProtocolMessageContent();
        }
        List<DtlsHandshakeMessageFragment> fragments = generateFragments(message, bytes, bytes.length, context);

        return fragments.get(0);
    }

    private List<DtlsHandshakeMessageFragment> generateFragments(HandshakeMessage message, byte[] handshakeBytes,
        int maxFragmentLength, TlsContext context) {
        List<DtlsHandshakeMessageFragment> fragments = new LinkedList<>();
        int currentOffset = 0;
        do {
            byte[] fragmentBytes = Arrays.copyOfRange(handshakeBytes, currentOffset,
                Math.min(currentOffset + maxFragmentLength, handshakeBytes.length));
            int sequence;
            if (message.getMessageSequence() != null) {
                sequence = message.getMessageSequence().getValue();
            } else {
                // it is possible that not all messages are created under a DTLS context such that they do not have a
                // message sequence
                sequence = 0;
            }
            DtlsHandshakeMessageFragment fragment = new DtlsHandshakeMessageFragment(message.getHandshakeMessageType(),
                fragmentBytes, sequence, currentOffset, handshakeBytes.length);
            fragment.getHandler(context).prepareMessage(fragment);
            fragments.add(fragment);
            currentOffset += maxFragmentLength;
        } while (currentOffset < handshakeBytes.length);

        return fragments;
    }
}
